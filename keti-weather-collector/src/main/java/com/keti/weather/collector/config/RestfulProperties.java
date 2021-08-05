package com.keti.weather.collector.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;


@Data
@ConfigurationProperties(prefix = "spring.rest-template")
public class RestfulProperties {
    private Factory factory;
    private HttpClient httpClient;
}

@Data
class Factory {
    private int readTimeout;
    private int connectTimeout;
}

@Data
class HttpClient {
    private int maxConnTotal;
    private int maxConnPerRoute;
}
