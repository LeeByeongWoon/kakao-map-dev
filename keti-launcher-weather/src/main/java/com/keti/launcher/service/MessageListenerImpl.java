package com.keti.launcher.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.stereotype.Service;
import org.springframework.kafka.listener.MessageListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import com.keti.launcher.entity.WeatherEntity;
import com.keti.launcher.repository.WeatherRepository;


@Service
public class MessageListenerImpl implements MessageListener<String, String> {

    private final ObjectMapper objectMapper;
    private final WeatherRepository weatherRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public MessageListenerImpl(ObjectMapper objectMapper, WeatherRepository weatherRepository) {
        this.objectMapper = objectMapper;
        this.weatherRepository = weatherRepository;
    }

    
    @Override
	public void onMessage(ConsumerRecord<String, String> consumerRecord) {
        try {
            List<Map<String, Object>> weatherDatas = new ArrayList<>();


            Map<String, Object> recordValue =
                    objectMapper.readValue(consumerRecord.value(), new TypeReference<Map<String, Object>>(){});
            List<Map<String, Object>> messages = 
                    objectMapper.convertValue(recordValue.get("messages"), new TypeReference<List<Map<String, Object>>>(){});


            int messagesSize = messages.size();
            for(int cnt=0; cnt<messagesSize; cnt++) {
                Map<String, Object> message = messages.get(cnt);

                String sdt = message.get("timestamp").toString();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'");
                LocalDateTime ldt = LocalDateTime.parse(sdt, formatter).minusHours(1);
                ZonedDateTime zdt = ldt.atZone(ZoneId.of("UTC"));

                String date = zdt.format(DateTimeFormatter.ISO_LOCAL_DATE);
                String time = zdt.format(DateTimeFormatter.ISO_LOCAL_TIME);

                Instant timestamp = Instant.parse(date + "T" + time.split(":")[0] + ":00:00.000000Z");
                // Instant timestamp = Instant.parse(message.get("timestamp").toString());

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

                if(resultCode == 00) {
                    JsonNode itemsObject = body.path("items").path("item");
                    List<Map<String, Object>> items =
                            objectMapper.convertValue(itemsObject, new TypeReference<List<Map<String, Object>>>(){});

                    String baseDate = items.get(0).get("baseDate").toString();
                    String baseTime = items.get(0).get("baseTime").toString();

                    Map<String, Object> weatherData = new HashMap<>();
                    weatherData.put("timestamp", timestamp);
                    weatherData.put("statusCode", statusCode);
                    weatherData.put("resultCode", resultCode);
                    weatherData.put("baseDate", baseDate);
                    weatherData.put("baseTime", baseTime);

                    Set<String> keys = requestData.keySet();
                    for (String key : keys) {
                        switch (key) {
                            case "vi02Phase":
                                String vi02Phase = requestData.get(key).toString();
                                String chk_vi02Phase = vi02Phase != "" ? vi02Phase : "전체";

                                weatherData.put(key, chk_vi02Phase);
                                break;
                            
                            case "vi03Phase":
                                String vi03Phase = requestData.get(key).toString();
                                String chk_vi03Phase = vi03Phase != "" ? vi03Phase : "전체";

                                weatherData.put(key, chk_vi03Phase);
                                break;
                        
                            default:
                                weatherData.put(key, requestData.get(key));
                                break;
                        }
                    }

                    for (Map<String, Object> item : items) {
                        String category = item.get("category").toString();

                        weatherData.put(category.toLowerCase() + "Value", item.get("obsrValue"));
                    }

                    weatherDatas.add(weatherData);
                }

                logger.info("[Consume(" + (cnt+1) + "/" + messagesSize + ") | resultCode=" + resultCode + ", resultMsg=" + resultMsg + "]");
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