package com.keti.collector.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "spring.mongodb")
public class MongoDBProperties {
    
    private String url;

}
