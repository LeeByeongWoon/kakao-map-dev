package com.keti.launcher.repository;

import org.springframework.stereotype.Repository;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.spring.influx.InfluxDB2Properties;

import com.keti.launcher.entity.NewsEntity;


@Repository
@EnableConfigurationProperties(InfluxDB2Properties.class)
public class NewsRepository {

    private final InfluxDBClient influxdb;
    private final InfluxDB2Properties properties;


    public NewsRepository(InfluxDBClient influxdb, InfluxDB2Properties influxDB2Properties) {
        this.influxdb = influxdb;
        this.properties = influxDB2Properties;
    }


    public void save(final NewsEntity entity) {
        final WriteApi writeApi = influxdb.getWriteApi();
        writeApi.writeMeasurement(
                properties.getBucket(),
                properties.getOrg(),
                WritePrecision.NS,
                entity
        );
        writeApi.close();
    }

}
