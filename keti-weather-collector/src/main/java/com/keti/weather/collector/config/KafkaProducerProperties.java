package com.keti.weather.collector.config;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "spring.kafka.producer")
public class KafkaProducerProperties {

	private String bootstrapServers;
	private String acks;
	private int retries;
	private int batchSize;
	private long lingerMs;
	private long requestTimeoutMs;
	private long deliveryTimeoutMs;
	private long bufferMemory;
	private long maxBlockMs;
	private long maxRequestSize;
	private String keySerializer;
	private String valueSerializer;
	private String compresstionType;

}
