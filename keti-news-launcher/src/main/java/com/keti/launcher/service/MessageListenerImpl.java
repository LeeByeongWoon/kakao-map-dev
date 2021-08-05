package com.keti.launcher.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.listener.MessageListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.keti.launcher.component.AbstractMessageHandler;
import com.keti.launcher.entity.NewsEntity;
import com.keti.launcher.repository.NewsRepository;


@Service
public class MessageListenerImpl extends AbstractMessageHandler implements MessageListener<String, String> {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    NewsRepository newsRepository;

    
    @Override
	public void onMessage(ConsumerRecord<String, String> consumerRecord) {
        try {
            String recordValue = consumerRecord.value();
            Map<String, Object> messages = extractMessage(recordValue);


            


            // Set keys = messages.keySet();
            // for (Object key : keys) {
            //     List<NewsEntity> data = 
            //             objectMapper.convertValue(messages.get(key), new TypeReference<List<NewsEntity>>(){});

            //     datas.add(data);
            // }


        } catch (Exception e) {
            logger.info("[Exception: " + e + " ]");
        }

    }


    @Override
    public List<Map<String, Object>> transformMessage(Map<String, Object> message) {
        // TODO Auto-generated method stub
        return null;
    }

}