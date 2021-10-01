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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.Point.Builder;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
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


    public List<String> validationByDatabase(String _mainDomain, String _subDomain) {
        List<String> databases = new ArrayList<>();

        QueryResult qr_databases = influxDBRepository.getDatabases();
        List<Result> qr_results = qr_databases.getResults();
        // String qr_error = qr_databases.getError();

        for (Result e : qr_results) {
            List<Series> r_series = e.getSeries();
            // String r_error = e.getError();

            for (Series ele : r_series) {
                List<List<Object>> s_values = ele.getValues();

                for (List<Object> element : s_values) {
                    String database = element.get(0).toString();

                    if(database.equals(_mainDomain + "__" + _subDomain)) {
                        databases.add(database);
                    }
                }
            }
        }

        return databases;
    }

    public List<String> validationByMeasurement(String _mainDomain, String _subDomain, String _measurement) {
        List<String> measurements = new ArrayList<>();

        QueryResult qr_measurements = influxDBRepository.getMeasurements(_mainDomain + "__" + _subDomain);
        List<Result> qr_results = qr_measurements.getResults();
        // String qr_error = qr_measurements.getError();

        for (Result e : qr_results) {
            List<Series> r_series = e.getSeries();
            // String r_error = e.getError();

            for (Series ele : r_series) {
                List<List<Object>> s_values = ele.getValues();

                for (List<Object> element : s_values) {
                    String measurement = element.get(0).toString();

                    if(measurement.equals(_measurement)) {
                        measurements.add(measurement);
                    }
                }
            }
        }

        return measurements;
    }


    public String useDatabase(GenerateVo generateVo) {
        JSONObject ifxDatabase = generateVo.getInfluxdb().getIfxDatabase();
        String database = ifxDatabase.get("db_main") + "__" + ifxDatabase.get("db_sub");

        influxDBRepository.swapDatabase(database);

        return database;
    }


    public String generateByDatabase(GenerateVo generateVo) {
        JSONObject ifxDatabase = generateVo.getInfluxdb().getIfxDatabase();
        String database = ifxDatabase.get("db_main") + "__" + ifxDatabase.get("db_sub");

        influxDBRepository.createDatabase(database);

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

            if(point != null) {
                entities.add(point);
            }
    
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

            if(point != null) {
                entities.add(point);
            }

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


    private Point generateSeries(String measurement, List<JSONObject> columns, String[] entity) throws ParseException {
        Builder builder = Point.measurement(measurement);

        for (JSONObject column : columns) {
            int dataIndex = Integer.parseInt(column.get("data_index").toString());
            String dataSet = column.get("data_set").toString();
            String dataType = column.get("data_type").toString();
            String dataFormat = column.get("data_format").toString();
            String dataValue = column.get("data_value").toString();
            List<JSONObject> dataFunc = objectMapper.convertValue(column.get("data_func"), new TypeReference<List<JSONObject>>(){});

            String compareToStringEntity = "";
            Float compareToFloatEntity = 0.00f;

            if(dataType.equals("Char")) {
                compareToStringEntity = compareToString(entity[dataIndex], dataFunc);

                if(compareToStringEntity == null) {
                    return null;
                }
            }

            if(dataType.equals("Float")) {
                compareToFloatEntity = 
                    !entity[dataIndex].isEmpty() ? compareToFloat(Float.parseFloat(entity[dataIndex]), dataFunc) : compareToFloat(0.00f, dataFunc);

                if(compareToFloatEntity == null) {
                    return null;
                }
            }

            switch (dataSet) {
                case "time":
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dataFormat);
                    Date dt = simpleDateFormat.parse(entity[dataIndex]);

                    builder.time(dt.getTime(), TimeUnit.MILLISECONDS);
                    break;

                case "tag":
                    builder.tag(dataValue, compareToStringEntity);
                    break;

                case "field":
                    if(dataType.equals("Float")) {
                        builder.addField(dataValue, compareToFloatEntity);
                    } else {
                        builder.addField(dataValue, compareToStringEntity);
                    }

                    break;

                case "all":
                    builder.tag(dataValue, compareToStringEntity);

                    if(dataType.equals("Float")) {
                        builder.addField(dataValue, compareToFloatEntity);
                    } else {
                        builder.addField(dataValue, compareToStringEntity);
                    }

                    break;
            }
        }
        
        return builder.build();
    }


    private LineIterator csvFileReader(String encode, String fileName) throws IOException {
        File file = new File(location + fileName);
        
        return FileUtils.lineIterator(file, encode);
    }


    private Float compareToFloat(Float data, List<JSONObject> funcs) {
        Float value = data;

        for (JSONObject func : funcs) {
            value = null;

            String compareSign = func.get("compare_sign").toString();
            Float compareValue = Float.parseFloat(func.get("compare_value").toString());
    
            switch (compareSign) {
                case "!=":
                    if(data != compareValue) {
                        value = data;
                    }
                    break;
                              
                case "==":
                    if(data == compareValue) {
                        value = data;
                    }
                    break;
                        
                case ">":
                    if(data > compareValue) {
                        value = data;
                    }
                    break;

                case "<":
                    if(data < compareValue) {
                        value = data;
                    }
                    break;

                default:
                    value = data;
                    break;
            }
        }

        return value;
    }

    
    private String compareToString(String data, List<JSONObject> funcs) {
        String value = data;

        for (JSONObject func : funcs) {
            value = null;

            String compareSign = func.get("compare_sign").toString();
            String compareValue = func.get("compare_value").toString();
    
            switch (compareSign) {
                case "!=":
                    if(!data.equals(compareValue)) {
                        value = data;
                    }
                    break;
                          
                case "==":
                    if(data.equals(compareValue)) {
                        value = data;
                    }
                    break;

                default:
                    value = data;
                    break;
            }
        }
    
        return value;
    }

}