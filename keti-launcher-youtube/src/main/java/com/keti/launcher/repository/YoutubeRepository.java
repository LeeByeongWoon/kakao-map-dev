package com.keti.launcher.repository;

import org.springframework.stereotype.Repository;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.spring.influx.InfluxDB2Properties;

import com.keti.launcher.entity.YoutubeEntity;


@Repository
@EnableConfigurationProperties(InfluxDB2Properties.class)
public class YoutubeRepository {

    private final InfluxDBClient client;
    private final InfluxDB2Properties properties;


    public YoutubeRepository(InfluxDBClient client, InfluxDB2Properties properties) {
        this.client = client;
        this.properties = properties;
    }


    public void save(final YoutubeEntity entity) {
        final WriteApi writeApi = client.getWriteApi();
        writeApi.writeMeasurement(
                properties.getBucket(),
                properties.getOrg(),
                WritePrecision.NS,
                entity
        );
        writeApi.close();
    }

}
