package com.keti.collector.config;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


@Configuration
@EnableConfigurationProperties(RestfulProperties.class)
public class RestfulConfig {

    private final RestfulProperties properties;

    public RestfulConfig(RestfulProperties properties) {
        this.properties = properties;
    }

    
    @Bean
    public RestTemplate restTemplate() {
        int readTimeout = properties.getFactory().getReadTimeout();
        int connectTimeout = properties.getFactory().getConnectTimeout();
        int maxConnTotal = properties.getHttpClient().getMaxConnTotal();
        int maxConnPerRoute = properties.getHttpClient().getMaxConnPerRoute();

        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(readTimeout);
        factory.setConnectTimeout(connectTimeout);

        HttpClient httpClient = HttpClientBuilder.create()
                .setMaxConnTotal(maxConnTotal)
                .setMaxConnPerRoute(maxConnPerRoute)
                .build();

        factory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(factory);

        return restTemplate;
    }
}