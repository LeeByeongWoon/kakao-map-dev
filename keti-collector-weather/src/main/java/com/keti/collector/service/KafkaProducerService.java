package com.keti.collector.service;

import java.util.List;

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
import org.json.simple.JSONObject;


@Service
public class KafkaProducerService {

	@Value("${spring.kafka.producer.topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

	final Logger logger = LoggerFactory.getLogger(this.getClass());

    
    public void sendMessage(List<JSONObject> weatherDataList) throws Exception {
		int weatherDataSize = weatherDataList.size();

		for(int cnt=0; cnt<weatherDataSize; cnt++) {
			JSONObject json = weatherDataList.get(cnt);
			String data = json.toString();

			ProducerRecord<String, String> message = new ProducerRecord<String, String>(topic, data);
			ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(message);
	
			int i = cnt + 1;
			future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

				@Override
				public void onSuccess(SendResult<String, String> result) {
					logger.info("[onSuccess(" + i + "/" + weatherDataSize + ") | offset=" + result.getRecordMetadata().offset() + "]");
				}
	
				@Override
				public void onFailure(Throwable ex) {
					logger.info("[onFailure(" + i + "/" + weatherDataSize + ") | " + ex.getMessage() + "]");
				}

			});
		}
		
    }

}
