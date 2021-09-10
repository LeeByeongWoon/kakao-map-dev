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

import com.keti.collector.repository.InfluxDBRepository;
import com.keti.collector.vo.GenerateVo;


@Service
public class GenerateSchemaService {

    @Value("${spring.multipart.location}")
    private String location = null;
    
    private final InfluxDBRepository influxDBRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    
    public GenerateSchemaService(InfluxDBRepository influxDBRepository) {
        this.influxDBRepository = influxDBRepository;
    }


    public void generateDatabase(GenerateVo generateVo) {
        String dbName = generateVo.getDomain();

        influxDBRepository.createDatabase(dbName);
        influxDBRepository.swapDatabase(dbName);
    }


    public void generateSeries(GenerateVo generateVo) throws IOException, ParseException, NumberFormatException {
        String uuidFileName = generateVo.getUuidFileName();
        String encode = generateVo.getEncode();
        String measurement = generateVo.getMeasurement().get("value").toString();
        List<JSONObject> columns = generateVo.getColumns();

        File file = new File(location + uuidFileName);
        LineIterator it = FileUtils.lineIterator(file, encode);

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
            } else {
                String[] entity = line.split(",", -1);

                Builder builder = Point.measurement(measurement);

                for (JSONObject column : columns) {
                    int index = Integer.parseInt(column.get("index").toString());
                    String dataSet = column.get("data_set").toString();
                    String dataType = column.get("data_type").toString();
                    String dataFormat = column.get("data_format").toString();
                    String value = column.get("value").toString();

                    switch (dataSet) {
                        case "time":
                            String sdt = entity[index];
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dataFormat);
                            Date dt = simpleDateFormat.parse(sdt);

                            builder.time(dt.getTime(), TimeUnit.MILLISECONDS);

                            break;

                        case "tag":
                            builder.tag(value, entity[index]);
                            
                            break;

                        case "field":
                            logger.info("entity[index]: " + entity[index]);
                            builder.addField(value, entity[index]);

                            break;
                    }
                }
                // String sdt = entity[1];
                
                // Date dt = simpleDateFormat.parse(sdt);

                logger.info("commonIO: " + cnt);

                // Builder builder = Point.measurement(measurement);
                                       
                // builder.time(dt.getTime(), TimeUnit.MILLISECONDS);
                // builder.tag("serial", entity[2]);
                // builder.addField("index", entity[0]);
                // builder.addField("pm25", entity[8]);

                Point point = builder.build();

                entities.add(point);

                if(cnt % 100 == 0) {
                    influxDBRepository.save(entities);

                    entities.clear();
                    entities = null;
                }
            }
        };

        if(cnt % 100 != 0) {
            influxDBRepository.save(entities);

            entities.clear();
            entities = null;
        }
    }

}