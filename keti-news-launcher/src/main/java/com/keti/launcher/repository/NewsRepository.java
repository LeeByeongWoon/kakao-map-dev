package com.keti.launcher.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;

import com.keti.launcher.entity.NewsEntity;


@Repository
public class NewsRepository {

    @Value("${spring.influx2.bucket}")
    private String bucket;

    @Value("${spring.influx2.org}")
    private String org;

    @Autowired
    private InfluxDBClient influxdb;


    public void save(final NewsEntity entity) {
        final WriteApi writeApi = influxdb.getWriteApi();
        writeApi.writeMeasurement(bucket, org, WritePrecision.NS, entity);
        writeApi.close();
    }

}
