package com.keti.collector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.keti.collector.repository.InfluxDBRepository;
import com.keti.collector.vo.GenerateVo;


@Service
public class GenerateSchemaService {

    @Value("${spring.multipart.location}")
    private String location = null;
    
    private final InfluxDBRepository influxDBRepository;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    
    public GenerateSchemaService(InfluxDBRepository influxDBRepository, ObjectMapper objectMapper) {
        this.influxDBRepository = influxDBRepository;
        this.objectMapper = objectMapper;
    }


    public String generateDatabase(GenerateVo generateVo) {
        JSONObject ifxDatabase = generateVo.getInfluxdb().getIfxDatabase();
        String database = ifxDatabase.get("db_main") + "__" + ifxDatabase.get("db_sub");

        influxDBRepository.createDatabase(database);

        return database;
    }


    public String useDatabase(GenerateVo generateVo) {
        JSONObject ifxDatabase = generateVo.getInfluxdb().getIfxDatabase();
        String database = ifxDatabase.get("db_main") + "__" + ifxDatabase.get("db_sub");

        influxDBRepository.swapDatabase(database);

        return database;
    }


    public String generateByInput(GenerateVo generateVo) throws IOException, ParseException, NumberFormatException {
        String encode = generateVo.getFile().getFlEncode();
        String fileName = generateVo.getFile().getFlName();
        String measurement = generateVo.getInfluxdb().getIfxMeasurement().get("mt_value").toString();
        List<JSONObject> columns = generateVo.getInfluxdb().getIfxColumns();

        LineIterator it = csvFileReader(encode, fileName);

        int cnt = -1;
        List<Point> entities = null;

        while(it.hasNext()) {
            cnt++;
            String line = it.nextLine();

            if(cnt == 0) {
                continue;
            }
            
            if(entities == null) {
                entities = new ArrayList<Point>();
            }

            String[] entity = line.split(",", -1);
            Point point = generateSeries(measurement, columns, entity);

            entities.add(point);

            if(cnt % 1000 == 0) {
                logger.info("commit: " + cnt/1000);
                influxDBRepository.save(entities);

                entities.clear();
                entities = null;
            }
        }

        if(cnt % 1000 != 0) {
            logger.info("commit: " + cnt/1000);
            influxDBRepository.save(entities);

            entities.clear();
            entities = null;
        }

        return Integer.toString(cnt);
    }


    public String generateByColumns(GenerateVo generateVo) throws IOException, ParseException, NumberFormatException {
        String encode = generateVo.getFile().getFlEncode();
        String fileName = generateVo.getFile().getFlName();
        int measurementIndex = Integer.parseInt(generateVo.getInfluxdb().getIfxMeasurement().get("mt_index").toString());
        List<JSONObject> columns = generateVo.getInfluxdb().getIfxColumns();

        LineIterator it = csvFileReader(encode, fileName);

        int cnt = -1;
        List<Point> entities = null;

        while(it.hasNext()) {
            cnt++;

            String line = it.nextLine();

            if(cnt == 0) {
                continue;
            }
            
            if(entities == null) {
                entities = new ArrayList<Point>();
            }

            String[] entity = line.split(",", -1);
            String measurement = entity[measurementIndex];
            
            Point point = generateSeries(measurement, columns, entity);

            entities.add(point);

            if(cnt % 1000 == 0) {
                logger.info("commit: " + cnt/1000);
                influxDBRepository.save(entities);

                entities.clear();
                entities = null;
            }
        }

        if(cnt % 1000 != 0) {
            logger.info("commit: " + cnt/1000);
            influxDBRepository.save(entities);

            entities.clear();
            entities = null;
        }

        return Integer.toString(cnt);
    }


    public String compareToValue(String type, String data, List<JSONObject> funcs) {
        String result = "";

        int funcsLength = funcs.size();
        if(funcsLength != 0) {
            for (JSONObject func : funcs) {
                String compareSign = func.get("compare_sign").toString();
                String compareValue = func.get("compare_value").toString();
    
                switch (compareSign) {
                    case "!=":
                        if(!data.equals(compareValue)) {
                            result = data;
                        }
                        break;
                              
                    case "==":
                        if(data.equals(compareValue)) {
                            result = data;
                        }
                        break;
                }
            }
        } else {
            result = data;
        }

        return result;
    }


    public Point generateSeries(String measurement, List<JSONObject> columns, String[] entity) throws ParseException {
        Builder builder = Point.measurement(measurement);

        for (JSONObject column : columns) {
            int dataIndex = Integer.parseInt(column.get("data_index").toString());
            String dataSet = column.get("data_set").toString();
            String dataType = column.get("data_type").toString();
            String dataFormat = column.get("data_format").toString();
            String dataValue = column.get("data_value").toString();
            List<JSONObject> dataFunc = objectMapper.convertValue(column.get("data_func"), new TypeReference<List<JSONObject>>(){});

            String compareEntity = compareToValue(dataType, entity[dataIndex], dataFunc);

            switch (dataSet) {
                case "time":
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dataFormat);
                    Date dt = simpleDateFormat.parse(entity[dataIndex]);

                    builder.time(dt.getTime(), TimeUnit.MILLISECONDS);
                    break;

                case "tag":
                    builder.tag(dataValue, compareEntity);
                    break;

                case "field":
                    if(dataType.equals("Float")) {
                        float series = !compareEntity.isEmpty() ? Float.parseFloat(compareEntity) : 0;
                        builder.addField(dataValue, series);
                    } else {
                        builder.addField(dataValue, compareEntity);
                    }

                    break;

                case "all":
                    builder.tag(dataValue, compareEntity);

                    if(dataType.equals("Float")) {
                        float series = !compareEntity.isEmpty() ? Float.parseFloat(compareEntity) : 0;
                        builder.addField(dataValue, series);
                    } else {
                        builder.addField(dataValue, compareEntity);
                    }

                    break;
            }
        }
        
        return builder.build();
    }


    public LineIterator csvFileReader(String encode, String fileName) throws IOException {
        File file = new File(location + fileName);
        
        return FileUtils.lineIterator(file, encode);
    }

}