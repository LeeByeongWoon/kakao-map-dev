package com.keti.weather.launcher.repository;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.keti.weather.launcher.entity.WeatherEntity;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class WeatherRepository {

    @Value("${spring.influx2.bucket}")
    private String bucket;

    @Value("${spring.influx2.org}")
    private String org;

    @Autowired
    private InfluxDBClient influxdb;


    public void save(final List<WeatherEntity> entities) {
        final WriteApi writeApi = influxdb.getWriteApi();
        writeApi.writeMeasurements(bucket, org, WritePrecision.NS, entities);

        writeApi.close();
    }

}
