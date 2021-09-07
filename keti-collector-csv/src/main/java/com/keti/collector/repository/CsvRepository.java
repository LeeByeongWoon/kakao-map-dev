package com.keti.collector.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.influxdb.dto.Point;


@Repository
public class CsvRepository {
    
    private final InfluxDBTemplate<Point> influxDBTemplate;


    public CsvRepository(InfluxDBTemplate<Point> influxDBTemplate) {
        this.influxDBTemplate = influxDBTemplate;
    }

    public void save(List<Point> entities) {
        influxDBTemplate.write(entities);
    }

}
