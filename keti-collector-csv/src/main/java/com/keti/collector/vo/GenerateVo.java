package com.keti.collector.vo;

import lombok.Data;

import java.util.List;
import org.json.simple.JSONObject;


@Data
public class GenerateVo {
    private String uuidFileName;
    private String domain;
    private JSONObject measurement;
    private JSONObject timeIndex;
    private String encode;
    private List<JSONObject> columns;    
}
