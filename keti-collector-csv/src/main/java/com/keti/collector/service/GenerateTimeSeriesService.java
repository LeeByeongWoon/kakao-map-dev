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
public class GenerateTimeSeriesService {

    @Value("${spring.multipart.location}")
    private String location = null;
    
    private final InfluxDBRepository influxDBRepository;
    private final ObjectMapper objectMapper;
    // private final Logger logger = LoggerFactory.getLogger(this.getClass());

    
    public GenerateTimeSeriesService(InfluxDBRepository _influxDBRepository, ObjectMapper _objectMapper) {
        this.influxDBRepository = _influxDBRepository;
        this.objectMapper = _objectMapper;
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


    public JSONObject generateByDatabase(GenerateVo generateVo) {
        Map<String, String> serviceResult = new HashMap<>();

        JSONObject ifxDatabase = generateVo.getTimeSeriesVo().getIfxDatabase();
        String mainDomain = ifxDatabase.get("db_main").toString();
        String subDomain = ifxDatabase.get("db_sub").toString();
        String database = mainDomain + "__" + subDomain;

        List<String> databases = validationByDatabase(mainDomain, subDomain);

        if(databases.size() != 0) {
            serviceResult.put("result", "already");
        } else {
            serviceResult.put("result", "generate");
            influxDBRepository.createDatabase(database);
        }

        influxDBRepository.swapDatabase(database);

        return new JSONObject(serviceResult);
    }


    public JSONObject generateByInput(GenerateVo generateVo) throws IOException, ParseException, NumberFormatException {
        Map<String, String> serviceResult = new HashMap<>();

        LineIterator it = csvFileReader(generateVo);
        String measurement = generateVo.getTimeSeriesVo().getIfxMeasurement().get("mt_value").toString();
        List<JSONObject> columns = generateVo.getTimeSeriesVo().getIfxColumns();

        int total = -1;
        int commit = -1;
        List<Point> entities = null;

        while(it.hasNext()) {
            total++;
            String line = it.nextLine();

            if(total == 0) {
                continue;
            }
            
            if(entities == null) {
                entities = new ArrayList<Point>();
            }

            String[] entity = line.split(",", -1);
            Point point = generateTimeSeries(measurement, columns, entity);

            if(point != null) {
                commit++;
                entities.add(point);
            }
    
            if(total % 1000 == 0) {
                influxDBRepository.save(entities);

                entities.clear();
                entities = null;
            }
        }

        if(total % 1000 != 0) {
            influxDBRepository.save(entities);

            entities.clear();
            entities = null;
        }

        serviceResult.put("total", Integer.toString(total));
        serviceResult.put("commit", Integer.toString(commit+1));

        return new JSONObject(serviceResult);
    }


    public JSONObject generateByColumns(GenerateVo generateVo) throws IOException, ParseException, NumberFormatException {
        Map<String, Object> serviceResult = new HashMap<>();

        LineIterator it = csvFileReader(generateVo);

        int measurementIndex = Integer.parseInt(generateVo.getTimeSeriesVo().getIfxMeasurement().get("mt_index").toString());
        List<JSONObject> columns = generateVo.getTimeSeriesVo().getIfxColumns();

        int total = -1;
        Map<String, Long> commit = new HashMap<>();

        List<Point> entities = null;

        while(it.hasNext()) {
            total++;

            String line = it.nextLine();

            if(total == 0) {
                continue;
            }
            
            if(entities == null) {
                entities = new ArrayList<Point>();
            }

            String[] entity = line.split(",", -1);
            String measurement = entity[measurementIndex];
            Point point = generateTimeSeries(measurement, columns, entity);

            if(point != null) {
                Long cnt = commit.get(measurement) != null ? commit.get(measurement) + 1 : 1;
                commit.put(measurement, cnt);

                entities.add(point);
            }

            if(total % 1000 == 0) {
                influxDBRepository.save(entities);

                entities.clear();
                entities = null;
            }
        }

        if(total % 1000 != 0) {
            influxDBRepository.save(entities);

            entities.clear();
            entities = null;
        }

        serviceResult.put("total", Integer.toString(total));
        serviceResult.put("commit", new JSONObject(commit));

        return new JSONObject(serviceResult);
    }


    private Point generateTimeSeries(String measurement, List<JSONObject> columns, String[] entity) throws ParseException {
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


    private Float compareToFloat(Float data, List<JSONObject> funcs) {
        Float value = data;

        for (JSONObject func : funcs) {
            value = null;

            String compareSign = func.get("compare_sign").toString();
            Float compareValue = Float.parseFloat(func.get("compare_value").toString());

            int compareResult = Float.compare(data, compareValue);
    
            switch (compareSign) {
                case "!=":
                    if(compareResult != 0) {
                        value = data;
                    }
                    break;
                              
                case "==":
                    if(compareResult == 0) {
                        value = data;
                    }
                    break;
                        
                case ">":
                    if(compareResult > 0) {
                        value = data;
                    }
                    break;

                case "<":
                    if(compareResult < 0) {
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

            int compareResult = data.compareTo(compareValue);
    
            switch (compareSign) {
                case "!=":
                    if(compareResult != 0) {
                        value = data;
                    }
                    break;
                          
                case "==":
                    if(compareResult == 0) {
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

    
    private LineIterator csvFileReader(GenerateVo generateVo) throws IOException {
        String encode = generateVo.getFileVo().getFlEncode();
        String fileName = generateVo.getFileVo().getFlName();

        File file = new File(location + fileName);
        
        return FileUtils.lineIterator(file, encode);
    }

}