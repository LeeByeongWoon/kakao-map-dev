package com.keti.collector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.influxdb.dto.BoundParameterQuery;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.Point.Builder;

import com.keti.collector.repository.InfluxDBRepository;
import com.keti.collector.vo.GenerateVo;


@Service
public class GenerateSchemaService {
    
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


    public void generateSeries(GenerateVo generateVo) throws IOException, ParseException {
        String uuidFileName = generateVo.getUuidFileName();
        String encode = generateVo.getEncode();
        File file = new File("/Users/gangbinpark/gorotis11/ketiClust/keti-collector-csv/" + uuidFileName);
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
                String sdt = entity[1];

                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss");
                Date dt = format.parse(sdt);

                logger.info("commonIO: " + cnt);

                Builder builder = Point.measurement("batch_test_01");
                                       
                builder.time(dt.getTime(), TimeUnit.MILLISECONDS);
                builder.tag("serial", entity[2]);
                builder.addField("index", entity[0]);
                builder.addField("pm25", entity[8]);

                Point point = builder.build();

                entities.add(point);

                if(cnt % 100 == 0) {
                    influxDBRepository.save(entities);

                    entities.clear();
                    entities = null;
                }
            }
        };

        influxDBRepository.save(entities);

        entities.clear();
        entities = null;
    }

}
