package com.keti.collector.vo;

import lombok.Data;

import java.util.List;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InfluxdbVo {
    private JSONObject ifxDatabase;
    private JSONObject ifxMeasurement;
    private List<JSONObject> ifxColumns;
}
