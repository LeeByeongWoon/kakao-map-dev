package com.keti.launcher.repository;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.spring.influx.InfluxDB2Properties;

import com.keti.launcher.entity.FinanceEntity;


@Repository
@EnableConfigurationProperties(InfluxDB2Properties.class)
public class FinanceRepository {

    private final InfluxDBClient influxdb;
    private final InfluxDB2Properties properties;


    public FinanceRepository(InfluxDBClient influxdb, InfluxDB2Properties properties) {
        this.influxdb = influxdb;
        this.properties = properties;
    }


    public void save(final List<FinanceEntity> entities) {
        final WriteApi writeApi = influxdb.getWriteApi();
        writeApi.writeMeasurements(properties.getBucket(), properties.getOrg(), WritePrecision.NS, entities);
        writeApi.close();
    }

}
