package com.keti.launcher.service;

import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.influxdb.dto.Point;

import com.keti.launcher.entity.WeatherEntity;


@Service
public class InfluxService {

    private final InfluxDBTemplate<Point> influxDBTemplate;

    
    public InfluxService(InfluxDBTemplate<Point> influxDBTemplate) {
        this.influxDBTemplate = influxDBTemplate;
    }

    public void save(List<WeatherEntity> entities) {
        List<Point> pointEntities = new ArrayList<Point>();

        for (WeatherEntity entity : entities) {
            Point pointEntity = Point.measurementByPOJO(WeatherEntity.class).addFieldsFromPOJO(entity).build();
            pointEntities.add(pointEntity);
        }

        influxDBTemplate.write(pointEntities);
    }
    
}
