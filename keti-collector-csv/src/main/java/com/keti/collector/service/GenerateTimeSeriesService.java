package com.keti.collector.service;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    
    public GenerateTimeSeriesService(InfluxDBRepository _influxDBRepository, ObjectMapper _objectMapper) {
        this.influxDBRepository = _influxDBRepository;
        this.objectMapper = _objectMapper;
    }


    public List<String> databaseInValidation(String _database) {
        List<String> serviceResult = new ArrayList<>();

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

                    if(database.equals(_database)) {
                        serviceResult.add(database);
                    }
                }
            }
        }

        return serviceResult;
    }

    public List<String> measurementInValidation(String _database, String _measurement) {
        List<String> serviceResult = new ArrayList<>();

        QueryResult qr_measurements = influxDBRepository.getMeasurements(_database);
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
                        serviceResult.add(measurement);
                    }
                }
            }
        }

        return serviceResult;
    }


    public JSONObject generatedByDatabase(GenerateVo _generateVo) {
        Map<String, Object> serviceResult = new HashMap<>();

        JSONObject ifxDatabase = _generateVo.getTimeSeriesVo().getIfxDatabase();
        String mainDomain = ifxDatabase.get("db_main").toString();
        String subDomain = ifxDatabase.get("db_sub").toString();
        String database = mainDomain + "_" + subDomain;

        List<String> databases = databaseInValidation(database);

        Map<String, Long> commits = new HashMap<>();
        if(databases.size() != 0) {
            commits.put(database, 0L);
        } else {
            influxDBRepository.createDatabase(database);
            commits.put(database, 1L);
        }

        serviceResult.put("rows", 1);
        serviceResult.put("commits", new JSONObject(commits));

        influxDBRepository.swapDatabase(database);

        return new JSONObject(serviceResult);
    }


    public JSONObject generatedByInput(GenerateVo _generateVo) throws IOException, ParseException, NumberFormatException {
        Map<String, Object> serviceResult = new HashMap<>();

        LineIterator it = csvFileReader(_generateVo);
        String measurement = _generateVo.getTimeSeriesVo().getIfxMeasurement().get("mt_value").toString();
        List<JSONObject> columns = _generateVo.getTimeSeriesVo().getIfxColumns();

        int rows = -1;
        Map<String, Long> commit = new HashMap<>();
        List<Point> entities = null;

        while(it.hasNext()) {
            rows++;
            String line = it.nextLine();

            if(rows == 0) {
                continue;
            }
            
            if(entities == null) {
                entities = new ArrayList<Point>();
            }

            String[] entity = line.split(",", -1);
            Point point = generatedByTimeSeries(measurement, columns, entity);

            if(point != null) {
                Long cnt = commit.get(measurement) != null ? commit.get(measurement) + 1 : 1;
                commit.put(measurement, cnt);
                entities.add(point);
            }
    
            if(rows % 1000 == 0) {
                influxDBRepository.save(entities);

                entities.clear();
                entities = null;
            }
        }

        if(rows % 1000 != 0) {
            influxDBRepository.save(entities);

            entities.clear();
            entities = null;
        }

        serviceResult.put("rows", Integer.toString(rows));
        serviceResult.put("commits", new JSONObject(commit));

        return new JSONObject(serviceResult);
    }


    public JSONObject generatedByColumns(GenerateVo _generateVo) throws IOException, ParseException, NumberFormatException {
        Map<String, Object> serviceResult = new HashMap<>();

        LineIterator it = csvFileReader(_generateVo);
        int measurementIndex = Integer.parseInt(_generateVo.getTimeSeriesVo().getIfxMeasurement().get("mt_index").toString());
        List<JSONObject> columns = _generateVo.getTimeSeriesVo().getIfxColumns();

        int rows = -1;
        Map<String, Long> commit = new HashMap<>();
        List<Point> entities = null;

        while(it.hasNext()) {
            rows++;

            String line = it.nextLine();

            if(rows == 0) {
                continue;
            }
            
            if(entities == null) {
                entities = new ArrayList<Point>();
            }

            String[] entity = line.split(",", -1);
            String measurement = entity[measurementIndex];
            Point point = generatedByTimeSeries(measurement, columns, entity);

            if(point != null) {
                Long cnt = commit.get(measurement) != null ? commit.get(measurement) + 1 : 1;
                commit.put(measurement, cnt);

                entities.add(point);
            }

            if(rows % 1000 == 0) {
                influxDBRepository.save(entities);

                entities.clear();
                entities = null;
            }
        }

        if(rows % 1000 != 0) {
            influxDBRepository.save(entities);

            entities.clear();
            entities = null;
        }

        serviceResult.put("rows", Integer.toString(rows));
        serviceResult.put("commits", new JSONObject(commit));

        return new JSONObject(serviceResult);
    }


    private Point generatedByTimeSeries(String _measurement, List<JSONObject> _columns, String[] _entity) throws ParseException {
        Builder builder = Point.measurement(_measurement);

        for (JSONObject column : _columns) {
            int dataIndex = Integer.parseInt(column.get("data_index").toString());
            String dataSet = column.get("data_set").toString();
            String dataType = column.get("data_type").toString();
            String dataFormat = column.get("data_format").toString();
            String dataValue = column.get("data_value").toString();
            List<JSONObject> dataFunc = objectMapper.convertValue(column.get("data_func"), new TypeReference<List<JSONObject>>(){});

            String compareToStringEntity = "";
            Float compareToFloatEntity = 0.00f;

            if(dataType.equals("Char")) {
                compareToStringEntity = compareToString(_entity[dataIndex], dataFunc);

                if(compareToStringEntity == null) {
                    return null;
                }
            } else if(dataType.equals("Float")) {
                compareToFloatEntity = 
                    !_entity[dataIndex].isEmpty() ? compareToFloat(Float.parseFloat(_entity[dataIndex]), dataFunc) : compareToFloat(0.00f, dataFunc);

                if(compareToFloatEntity == null) {
                    return null;
                }
            }

            switch (dataSet) {
                case "time":
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dataFormat);
                    Date dt = simpleDateFormat.parse(_entity[dataIndex]);

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
                    if(dataType.equals("Float")) {
                        builder.tag(dataValue, compareToFloatEntity.toString());
                        builder.addField(dataValue, compareToFloatEntity);
                    } else {
                        builder.tag(dataValue, compareToStringEntity);
                        builder.addField(dataValue, compareToStringEntity);
                    }

                    break;
            }
        }
        
        return builder.build();
    }


    private Float compareToFloat(Float _data, List<JSONObject> _funcs) {
        Float value = _data;

        int size = _funcs.size();
        boolean[] validations = new boolean[size];

        for (int i = 0; i < size; i++) {
            validations[i] = false;

            JSONObject func = _funcs.get(i);
            String compareSign = func.get("compare_sign").toString();
            Float compareValue = Float.parseFloat(func.get("compare_value").toString());

            int compareResult = Float.compare(value, compareValue);
    
            switch (compareSign) {
                case "!=":
                    if(compareResult != 0) {
                        validations[i] = true;
                    }
                    break;
                              
                case "==":
                    if(compareResult == 0) {
                        validations[i] = true;
                    }
                    break;
                        
                case ">":
                    if(compareResult > 0) {
                        validations[i] = true;
                    }
                    break;

                case "<":
                    if(compareResult < 0) {
                        validations[i] = true;
                    }
                    break;

                default:
                    validations[i] = false;
                    break;
            }
        }

        for (boolean validation : validations) {
            if(!validation) {
                value = null;
                break;
            }
        }

        return value;
    }

    
    private String compareToString(String _data, List<JSONObject> _funcs) {
        String value = _data;

        int size = _funcs.size();
        boolean[] validations = new boolean[size];

        for (int i = 0; i < size; i++) {
            validations[i] = false;

            JSONObject func = _funcs.get(i);
            String compareSign = func.get("compare_sign").toString();
            String compareValue = func.get("compare_value").toString();

            int compareResult = value.compareTo(compareValue);
    
            switch (compareSign) {
                case "!=":
                    if(compareResult != 0) {
                        validations[i] = true;
                    }
                    break;
                          
                case "==":
                    if(compareResult == 0) {
                        validations[i] = true;
                    }
                    break;

                default:
                    validations[i] = false;
                    break;
            }
        }

        for (boolean validation : validations) {
            if(!validation) {
                value = null;
                break;
            }
        }
    
        return value;
    }

    
    private LineIterator csvFileReader(GenerateVo _generateVo) throws IOException {
        String encode = _generateVo.getFileVo().getFlEncode();
        String fileName = _generateVo.getFileVo().getFlName();

        File file = new File(location + fileName);
        
        return FileUtils.lineIterator(file, encode);
    }

}