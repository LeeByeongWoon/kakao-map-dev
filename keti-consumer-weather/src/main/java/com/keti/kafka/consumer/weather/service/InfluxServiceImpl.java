package com.keti.kafka.consumer.weather.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.influxdb.dto.Point;

import org.springframework.data.influxdb.InfluxDBTemplate;

import com.keti.consumer.kafka.vo.PartitionsMeasurementVo;


@Service
public class InfluxServiceImpl {

    @Autowired
    InfluxDBTemplate<Point> influxDBTemplate;

    public void write(PartitionsMeasurementVo vo) {
        // System.out.println(vo);
        influxDBTemplate.write(Point.measurementByPOJO(PartitionsMeasurementVo.class)
            .addFieldsFromPOJO(vo)
            .build());
    }

}