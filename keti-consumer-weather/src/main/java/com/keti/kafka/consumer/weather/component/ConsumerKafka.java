package com.keti.kafka.consumer.weather.component;

import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;


@Component
public class ConsumerKafka implements CommandLineRunner {

	@Autowired
	private ConcurrentMessageListenerContainer kafkaMessageListenerContainer = null;


	@Override
	public void run(String... args) throws Exception {
		kafkaMessageListenerContainer.start();
	}
    
}