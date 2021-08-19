package com.keti.launcher.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.kafka.listener.MessageListener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.influxdb.dto.Point;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import com.keti.launcher.entity.FinanceEntity;
import com.keti.launcher.repository.FinanceRepository;


@Service
public class MessageListenerImpl implements MessageListener<String, String> {

    private final ObjectMapper objectMapper;
    private final FinanceRepository repository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public MessageListenerImpl(ObjectMapper objectMapper, FinanceRepository repository) {
        this.objectMapper = objectMapper;
        this.repository = repository;
    }

    
    @Override
	public void onMessage(ConsumerRecord<String, String> consumerRecord) {
        try {
            Map<String, Object> records = 
                    objectMapper.readValue(consumerRecord.value(), new TypeReference<Map<String, Object>>(){});
            Map<String, Object> messages = 
                    objectMapper.convertValue(records.get("messages"), new TypeReference<Map<String, Object>>(){});

            Set<String> keys = messages.keySet();
            for (Object key : keys) {
                List<Point> pointsEntities = new ArrayList<>();

                List<FinanceEntity> financeEntities = 
                        objectMapper.convertValue(messages.get(key), new TypeReference<List<FinanceEntity>>(){});
                
                for (FinanceEntity financeEntity : financeEntities) {
                    Point pointEntity = Point.measurementByPOJO(FinanceEntity.class).addFieldsFromPOJO(financeEntity).build();
                    pointsEntities.add(pointEntity);
                }

                repository.save(pointsEntities);
            }
        } catch (Exception e) {
            logger.info("[Exception: " + e + " ]");
        }   
    }

}