package com.keti.launcher.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;


@Component
public class DataSourceComponent implements CommandLineRunner {

	private final ConcurrentMessageListenerContainer<String, String> kafkaMessageListenerContainer;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());


	public DataSourceComponent(ConcurrentMessageListenerContainer<String, String> kafkaMessageListenerContainer) {
		this.kafkaMessageListenerContainer = kafkaMessageListenerContainer;
	}


	@Override
	public void run(String... args) throws Exception {
		logger.info("#################################");
		logger.info("##### KETI YOUTUBE CONSUMER #####");
		logger.info("#################################");

		kafkaMessageListenerContainer.start();
	}
    
}