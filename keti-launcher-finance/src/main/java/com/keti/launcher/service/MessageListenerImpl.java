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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import com.keti.launcher.entity.FinanceEntity;
import com.keti.launcher.repository.FinanceRepository;


@Service
public class MessageListenerImpl implements MessageListener<String, String> {

    private final ObjectMapper objectMapper;
    private final FinanceRepository financeRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public MessageListenerImpl(ObjectMapper objectMapper, FinanceRepository financeRepository) {
        this.objectMapper = objectMapper;
        this.financeRepository = financeRepository;
    }

    
    @Override
	public void onMessage(ConsumerRecord<String, String> consumerRecord) {
        try {
            List<List<FinanceEntity>> datas = new ArrayList<>();

            Map<String, Object> records = 
                    objectMapper.readValue(consumerRecord.value(), new TypeReference<Map<String, Object>>(){});
            Map<String, Object> messages = 
                    objectMapper.convertValue(records.get("messages"), new TypeReference<Map<String, Object>>(){});

            Set<String> keys = messages.keySet();
            for (Object key : keys) {
                List<FinanceEntity> data = 
                        objectMapper.convertValue(messages.get(key), new TypeReference<List<FinanceEntity>>(){});

                datas.add(data);
            }

            for (List<FinanceEntity> data : datas) {
                financeRepository.save(data);
            }

        } catch (Exception e) {
            logger.info("[Exception: " + e + " ]");
        }   
    }

}