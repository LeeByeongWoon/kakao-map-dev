package com.keti.kafka.consumer.weather.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.List;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.listener.MessageListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.keti.kafka.consumer.weather.entity.WeatherEntity;
import com.keti.kafka.consumer.weather.repository.WeatherRepository;


@Service
public class MessageListenerImpl implements MessageListener<String, String> {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WeatherRepository weatherRepository;

    
    @Override
	public void onMessage(ConsumerRecord<String, String> consumerRecord) {
        try {
            String message  = consumerRecord.value();
            JsonNode rootNode = objectMapper.readTree(message);

            String timestamp = rootNode.get("timestamp").asText();
            String statusCode = rootNode.get("statusCode").asText();
            Long statusCodeValue = rootNode.get("statusCodeValue").asLong();
            Map<String, Object> requestData = 
                    objectMapper.readValue(String.valueOf(rootNode.get("requestData")), new TypeReference<Map<String, Object>>(){});
            JsonNode responseData = rootNode.get("responseData");


            ArrayList<WeatherEntity> entities = new ArrayList<>();
            

            Long serviceCode = responseData.path("header").path("resultCode").asLong();
            String serviceMessage = responseData.path("header").path("resultMsg").asText();
            logger.info("[serviceCode: " + serviceCode + ", serviceMessage: " + serviceMessage + "]");

            if(serviceCode == 0) {
                List<Map<String, Object>> items = 
                    objectMapper.convertValue(responseData.path("body").path("items").path("item"), new TypeReference<List<Map<String, Object>>>(){});

                int cnt = 0;
                int itemsLength = items.size();
                while(cnt < itemsLength) {
                    Map<String, Object> item = items.get(cnt);


                    Map<String, Object> entity = new HashMap<>();

                    entity.put("timestamp", Instant.parse(timestamp.toString()));
                    entity.put("statusCode", statusCode);
                    entity.put("statusCodeValue", statusCodeValue);

                    Iterator requestDatakeys = requestData.keySet().iterator();
                    while(requestDatakeys.hasNext()) {
                        String requestDatakey = (String) requestDatakeys.next();
                        entity.put(requestDatakey, requestData.get(requestDatakey));
                    }

                    Iterator itemkeys = item.keySet().iterator();
                    while(itemkeys.hasNext()) {
                        String itemkey = (String) itemkeys.next();
                        entity.put(itemkey, item.get(itemkey));
                    }

                    WeatherEntity weatherEntity = objectMapper.convertValue(entity, new TypeReference<WeatherEntity>(){});

                    entities.add(weatherEntity);
                    cnt++;
                }
            }

            weatherRepository.save(entities);

        } catch (Exception e) {
            logger.info("[Exception: " + e + " ]");
        }   
    }

}