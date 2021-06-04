package com.keti.kafka.producer.weather.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class KafkaProducerService {

	final Logger logger = LoggerFactory.getLogger(this.getClass());

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

	@Value("${spring.kafka.producer.topic}")
    private String topic;

    
    public void sendMessage(List<Map<String, Object>> weatherDataList) throws Exception {
		int cnt = 0;
		int weatherDataLength = weatherDataList.size();

		while(cnt < weatherDataLength) {
			Map<String, Object> map = weatherDataList.get(cnt);
			String data = objectMapper.writeValueAsString(map);
			ProducerRecord<String, String> message = new ProducerRecord<String, String>(topic, data);
			ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(message);
	
			int i = cnt + 1;
			future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

				@Override
				public void onSuccess(SendResult<String, String> result) {
					logger.info("[onSuccess(" + i + "/" + weatherDataLength + ") | offset=" + result.getRecordMetadata().offset() + "]");
				}
	
				@Override
				public void onFailure(Throwable ex) {
					logger.info("[onFailure(" + i + "/" + weatherDataLength + ") | " + ex.getMessage() + "]");
				}

			});

			cnt++;
		}
		
    }

}
