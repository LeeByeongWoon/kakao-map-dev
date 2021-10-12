package com.keti.collector.vo;

import lombok.Data;

import java.util.List;


@Data
public class MetaVo {
    private String mainDomain;
    private String subDomain;
    private List<String> measurements;
}
