package com.keti.collector.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
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

    private final KafkaTemplate<String, String> kafkaTemplate;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}


	public void sendMessage(List<JSONObject> weatherDatas) {
		try {
			Map<String, List<JSONObject>> dataSet = new HashMap<>();
			dataSet.put("messages", weatherDatas);

			String messages = new JSONObject(dataSet).toString();
			ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(topic, messages);
			ListenableFuture<SendResult<String, String>> listenableFuture = kafkaTemplate.send(producerRecord);

			listenableFuture.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

				@Override
				public void onSuccess(SendResult<String, String> result) {
					logger.info("[onSuccess - offset=" + result.getRecordMetadata().offset() + "]");
				}
		
				@Override
				public void onFailure(Throwable ex) {
					logger.info("[onFailure | " + ex.getMessage() + "]");
				}

			});
			
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException ex) {
			logger.info("InterruptedException: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

    
    public void sendMessages(List<JSONObject> weatherDataList) throws Exception {
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
