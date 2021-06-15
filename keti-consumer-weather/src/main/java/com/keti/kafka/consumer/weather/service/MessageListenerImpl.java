package com.keti.kafka.consumer.weather.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.List;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.kafka.listener.MessageListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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

            Map<String, Object> recordValue = objectMapper.readValue(consumerRecord.value(), new TypeReference<Map<String, Object>>(){});
            List<Map<String, Object>> messages = 
                    objectMapper.convertValue(recordValue.get("messages"), new TypeReference<List<Map<String, Object>>>(){});
                    

            int messagesSize = messages.size();
            for(int cnt=0; cnt<messagesSize; cnt++) {
                Map<String, Object> message = messages.get(cnt);

                Instant timestamp = Instant.parse(message.get("timestamp").toString());
                logger.info("timestamp: "+ timestamp);

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

                    List<String> keys = new ArrayList<>(requestData.keySet());
                    int keysSize = keys.size();
                    int itemsSize = items.size();

                    Map<String, Object> weatherData = new HashMap<>();
                    weatherData.put("timestamp", timestamp);
                    weatherData.put("statusCode", statusCode);
                    weatherData.put("resultCode", resultCode);

                    for(int keysCnt=0; keysCnt<keysSize; keysCnt++) {
                        String key = keys.get(keysCnt);
                        weatherData.put(key, requestData.get(key));
                    }

                    weatherData.put("baseDate", items.get(0).get("baseDate"));
                    weatherData.put("baseTime", items.get(0).get("baseTime"));
                    
                    for(int itemsCnt=0; itemsCnt<itemsSize; itemsCnt++) {
                        Map<String, Object> item = items.get(itemsCnt);
                        String category = item.get("category").toString();

                        weatherData.put(category.toLowerCase() + "Value", item.get("obsrValue"));

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