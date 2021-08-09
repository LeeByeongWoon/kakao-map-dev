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

import com.keti.launcher.entity.YoutubeEntity;
import com.keti.launcher.repository.YoutubeRepository;


@Service
public class MessageListenerImpl implements MessageListener<String, String> {

    private final ObjectMapper objectMapper;
    private final YoutubeRepository repository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public MessageListenerImpl(ObjectMapper objectMapper, YoutubeRepository repository) {
        this.objectMapper = objectMapper;
        this.repository = repository;
    }


    @Override
    public void onMessage(ConsumerRecord<String, String> data) {
        String dataValue = data.value();
        
        try {
            Map<String, JSONObject> youtubeMessage = 
                    objectMapper.readValue(dataValue, new TypeReference<Map<String, JSONObject>>(){});
            JSONObject youtube = youtubeMessage.get("messages");

            YoutubeEntity youtubeEntity = objectMapper.convertValue(youtube, YoutubeEntity.class);
            repository.save(youtubeEntity);
        } catch (Exception e) {
            logger.info("[Exception: " + e + " ]");
        }
    }

}