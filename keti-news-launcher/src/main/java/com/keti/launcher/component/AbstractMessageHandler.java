package com.keti.launcher.component;

import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import com.keti.launcher.entity.NewsEntity;
import com.keti.launcher.repository.NewsRepository;


@Component
public abstract class AbstractMessageHandler {
    
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    NewsRepository newsRepository;


    public Map<String, Object> extractMessage(String recordValue) {
        Map<String, Object> messages = null;

        try {
            Map<String, Object> map = 
                        objectMapper.readValue(recordValue, new TypeReference<Map<String, Object>>(){});
            messages = objectMapper.convertValue(map.get("messages"), new TypeReference<Map<String, Object>>(){});
        } catch (Exception e) {
            
        } finally {

        }

        return messages;
    }


    public abstract List<Map<String, Object>> transformMessage(Map<String, Object> message);


    public void loadMessage(List<List<NewsEntity>> datas) {
        for (List<NewsEntity> data : datas) {
            newsRepository.save(data);
        }

    }
    
}
