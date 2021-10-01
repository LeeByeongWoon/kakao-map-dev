package com.keti.collector.repository;

import java.util.List;

import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.BoundParameterQuery;
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

    public QueryResult getDatabases() {
        Query query = BoundParameterQuery.QueryBuilder.newQuery("show databases").forDatabase("_internal").create();
        QueryResult queryResult = influxDBTemplate.query(query);

        return queryResult;
    }

    public QueryResult getMeasurements(String database) {
        Query query = BoundParameterQuery.QueryBuilder.newQuery("show measurements").forDatabase(database).create();
        QueryResult queryResult = influxDBTemplate.query(query);

        return queryResult;
    }

    public QueryResult getRetentionPolicies(String database) {
        Query query = BoundParameterQuery.QueryBuilder.newQuery("show retention policies").forDatabase(database).create();
        QueryResult queryResult = influxDBTemplate.query(query);

        return queryResult;
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
