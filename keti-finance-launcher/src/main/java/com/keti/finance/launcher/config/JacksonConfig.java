package com.keti.finance.launcher.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JacksonConfig {
    
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                        .registerModule(new JavaTimeModule())
                        .configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true)
                        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

}
