package com.keti.launcher.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.influxdb.dto.Point;



@Service
public class InfluxService {

    private final InfluxDBTemplate<Point> influxDBTemplate;

    public InfluxService(InfluxDBTemplate<Point> influxDBTemplate) {
        this.influxDBTemplate = influxDBTemplate;
    }
    

    public void save(List<Point> entities) {
        influxDBTemplate.write(entities);
    }
    
}
