package com.keti.collector.repository;

import java.util.List;

import org.influxdb.dto.Point;
import org.springframework.data.influxdb.InfluxDBConnectionFactory;
import org.springframework.data.influxdb.InfluxDBTemplate;
import org.springframework.data.influxdb.InfluxDBProperties;

import org.springframework.stereotype.Repository;


@Repository
public class InfluxDBRepository {

    private final InfluxDBConnectionFactory influxDBConnectionFactory;
    private final InfluxDBTemplate<Point> influxDBTemplate;


    public InfluxDBRepository(InfluxDBConnectionFactory influxDBConnectionFactory, InfluxDBTemplate<Point> influxDBTemplate) {
        this.influxDBConnectionFactory = influxDBConnectionFactory;
        this.influxDBTemplate = influxDBTemplate;
    }

    public void save(List<Point> entities) {
        influxDBTemplate.write(entities);
    }

    public void createDatabase(String database) {
        influxDBTemplate.getConnection().createDatabase(database);
    }

    public void swapDatabase(String database) {
        InfluxDBProperties influxDBProperties = influxDBTemplate.getConnectionFactory().getProperties();
        influxDBProperties.setDatabase(database);

        influxDBConnectionFactory.setProperties(influxDBProperties);
    }
    
}
