package com.keti.launcher.service;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.kafka.listener.MessageListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import com.keti.launcher.entity.NewsEntity;
import com.keti.launcher.repository.NewsRepository;


@Service
public class MessageListenerImpl implements MessageListener<String, String> {

    private final ObjectMapper objectMapper;
    private final NewsRepository newsRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public MessageListenerImpl(ObjectMapper objectMapper, NewsRepository newsRepository) {
        this.objectMapper = objectMapper;
        this.newsRepository = newsRepository;
    }


    @Override
    public void onMessage(ConsumerRecord<String, String> data) {
        String dataValue = data.value();
        
        try {
            Map<String, JSONObject> newsMessage = 
                    objectMapper.readValue(dataValue, new TypeReference<Map<String, JSONObject>>(){});
            JSONObject news = newsMessage.get("messages");

            NewsEntity newsEntity = objectMapper.convertValue(news, NewsEntity.class);
            newsRepository.save(newsEntity);
        } catch (Exception e) {
            logger.info("[Exception: " + e + " ]");
        }
    }

}