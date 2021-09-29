package com.keti.collector.vo;

import lombok.Data;

@Data
public class GenerateVo {
    public FileVo file;
    public InfluxdbVo influxdb;
}
