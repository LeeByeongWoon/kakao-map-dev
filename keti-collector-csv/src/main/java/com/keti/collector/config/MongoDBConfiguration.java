package com.keti.collector.config;

import java.net.UnknownHostException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;


@Configuration
@EnableConfigurationProperties(MongoDBProperties.class)
public class MongoDBConfiguration {

    private final MongoDBProperties mongoDBProperties;

    public MongoDBConfiguration(MongoDBProperties _mongoDBProperties) {
        this.mongoDBProperties = _mongoDBProperties;
    }

    
    @Bean
    public MongoClient getMongoConn () throws UnknownHostException {
        String uri = mongoDBProperties.getUrl();
        
        return MongoClients.create(uri);
    }

}
