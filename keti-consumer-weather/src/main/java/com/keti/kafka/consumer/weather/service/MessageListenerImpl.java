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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.keti.kafka.consumer.weather.entity.WeatherEntity;
import com.keti.kafka.consumer.weather.repository.WeatherRepository;


@Service
public class MessageListenerImpl implements MessageListener<String, String> {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    JSONParser parser = new JSONParser();

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WeatherRepository weatherRepository;

    
    @Override
	public void onMessage(ConsumerRecord<String, String> consumerRecord) {
        try {
            List<Map<String, Object>> weatherDatas = new ArrayList<>();

            JSONObject recordValue = (JSONObject) parser.parse(consumerRecord.value());
            List<JSONObject> messages = 
                    objectMapper.convertValue(recordValue.get("messages"), new TypeReference<List<JSONObject>>(){});
                    

            int messagesSize = messages.size();
            for(int cnt=0; cnt<messagesSize; cnt++) {
                JSONObject message = messages.get(cnt);

                Instant timestamp = Instant.parse(message.get("timestamp").toString());
                String statusCode = message.get("statusCodeValue").toString();
                Map<String, Object> requestData = 
                        objectMapper.convertValue(message.get("requestData"), new TypeReference<Map<String, Object>>(){});
                String resStringData =
                        objectMapper.writeValueAsString(message.get("responseData"));

                JsonNode rootNode = objectMapper.readTree(resStringData);
                JsonNode header = rootNode.path("header");
                JsonNode body = rootNode.path("body");

                int resultCode = header.path("resultCode").asInt();
                String resultMsg = header.path("resultMsg").asText();
                

                logger.info("[Consume(" + (cnt+1) + "/" + messagesSize + ") | resultCode, resultMsg=" + resultCode + ", " + resultMsg + "]");

                if(resultCode == 00) {
                    JsonNode itemsObject = body.path("items").path("item");
                    List<Map<String, Object>> items =
                            objectMapper.convertValue(itemsObject, new TypeReference<List<Map<String, Object>>>(){});


                    int itemsSize = items.size();
                    for(int itemsCnt=0; itemsCnt<itemsSize; itemsCnt++) {
                        Map<String, Object> weatherData = new HashMap<>();
                        JSONObject item = 
                                objectMapper.convertValue(items.get(itemsCnt), new TypeReference<JSONObject>(){});

                        Iterator<String> requestDataKeys = requestData.keySet().iterator();
                        while(requestDataKeys.hasNext()) {
                            String key = requestDataKeys.next();
                            weatherData.put(key, requestData.get(key));
                        }
                        
                        weatherData.put("timestamp", timestamp);
                        weatherData.put("statusCode", statusCode);
                        weatherData.put("resultCode", resultCode);
                        weatherData.put("baseDate", item.get("baseDate"));
                        weatherData.put("baseTime", item.get("baseTime"));
                        weatherData.put("category", item.get("category"));
                        weatherData.put("obsrValue", item.get("obsrValue"));

                        weatherDatas.add(weatherData);
                    }
                }
            }

            int weatherDatasSize = weatherDatas.size();
            if(weatherDatasSize > 0) {
                List<WeatherEntity> entities =
                        objectMapper.convertValue(weatherDatas, new TypeReference<List<WeatherEntity>>(){});

                weatherRepository.save(entities);
            }

        } catch (Exception e) {
            logger.info("[Exception: " + e + " ]");
        }   
    }

}