package com.keti.finance.launcher.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;


@Component
public class ConsumerKafka implements CommandLineRunner {

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ConcurrentMessageListenerContainer kafkaMessageListenerContainer;


	@Override
	public void run(String... args) throws Exception {
		logger.info("#################################");
		logger.info("##### KETI FINANCE CONSUMER #####");
		logger.info("#################################");

		kafkaMessageListenerContainer.start();
	}
    
}