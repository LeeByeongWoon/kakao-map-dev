package com.keti.collector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import com.keti.collector.repository.CsvRepository;
import com.opencsv.CSVReader;

import java.text.ParseException;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyStore.Entry;

import org.apache.commons.io.LineIterator;
import org.apache.commons.io.FileUtils;
import org.influxdb.dto.Point;
import org.influxdb.dto.Point.Builder;


@Service
public class CsvReaderService {

    private final CsvRepository csvRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    
    public CsvReaderService(CsvRepository csvRepository) {
        this.csvRepository = csvRepository;
    }

    
    public void fileUtilsReader(File file) throws IOException, ParseException {
        LineIterator it = FileUtils.lineIterator(file, "euc-kr");

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
                

                // Point point = Point.measurement("batch_test_01")
                //     .time(dt.getTime(), TimeUnit.MILLISECONDS)
                //     .tag("serial", entity[2])
                //     .addField("pm25_보정전", entity[3])
                //     .addField("온도", entity[4])
                //     .addField("습도", entity[5])
                //     .addField("소음", entity[6])
                //     .addField("pm10", entity[7])
                //     .addField("pm25", entity[8])
                //     .addField("자외선", entity[9])
                //     .addField("조도", entity[10])
                //     .addField("흑구온도", entity[11])
                //     .addField("coci_pm10", entity[13])
                //     .addField("coci_pm25", entity[14])
                //     .addField("coci_temp", entity[15])
                //     .addField("coci_humi", entity[16])
                //     .addField("coci", entity[17])
                //     .addField("coai", entity[18])
                //     .addField("nh3", entity[19])
                //     .addField("h2s", entity[20])
                //     .addField("o3", entity[21])
                //     .addField("co", entity[22])
                //     .addField("no2", entity[23])
                //     .addField("so2", entity[24])
                //     .addField("진동x", entity[25])
                //     .addField("진동y", entity[26])
                //     .addField("진동z", entity[27])
                //     .addField("진동x최대", entity[28])
                //     .addField("진동y최대,", entity[29])
                //     .addField("진동z최대", entity[30])
                //     .build();

                entities.add(point);

                if(cnt % 100 == 0) {
                    csvRepository.save(entities);

                    entities.clear();
                    entities = null;
                }
            }
        };

        csvRepository.save(entities);

        entities.clear();
        entities = null;
    }
    

    public void openCsvReader(FileReader fileReader) throws Exception {
        CSVReader reader = new CSVReader(fileReader);

        int cnt = -1;
        List<Point> entities = null;

        String[] nextLine;
        while((nextLine = reader.readNext()) != null) {
            cnt++;

            if(cnt == 0) {
                continue;
            }
            
            if(entities == null) {
                entities = new ArrayList<Point>();
            } else {
                String sdt = nextLine[1];

                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd-hh:mm:ss");
                Date dt = format.parse(sdt);

                logger.info("openCSV: " + cnt);

                Point point = Point.measurement("batch_test_02")
                    .time(dt.getTime(), TimeUnit.MILLISECONDS)
                    .tag("serial", nextLine[2])
                    .addField("pm25_보정전", nextLine[3])
                    .addField("온도", nextLine[4])
                    .addField("습도", nextLine[5])
                    .addField("소음", nextLine[6])
                    .addField("pm10", nextLine[7])
                    .addField("pm25", nextLine[8])
                    .addField("자외선", nextLine[9])
                    .addField("조도", nextLine[10])
                    .addField("흑구온도", nextLine[11])
                    .addField("coci_pm10", nextLine[13])
                    .addField("coci_pm25", nextLine[14])
                    .addField("coci_temp", nextLine[15])
                    .addField("coci_humi", nextLine[16])
                    .addField("coci", nextLine[17])
                    .addField("coai", nextLine[18])
                    .addField("nh3", nextLine[19])
                    .addField("h2s", nextLine[20])
                    .addField("o3", nextLine[21])
                    .addField("co", nextLine[22])
                    .addField("no2", nextLine[23])
                    .addField("so2", nextLine[24])
                    .addField("진동x", nextLine[25])
                    .addField("진동y", nextLine[26])
                    .addField("진동z", nextLine[27])
                    .addField("진동x최대", nextLine[28])
                    .addField("진동y최대,", nextLine[29])
                    .addField("진동z최대", nextLine[30])
                    .build();

                entities.add(point);

                if(cnt % 1000 == 0) {
                    csvRepository.save(entities);

                    entities.clear();
                    entities = null;
                }
            }
        };

        csvRepository.save(entities);

        entities.clear();
        entities = null;
    }
}
