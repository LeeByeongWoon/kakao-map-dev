package com.keti.launcher.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "spring.kafka.consumer")
public class KafkaConsumerProperties {

	private String bootstrapServers;
	private String[] topics;
	private String groupId;
	private String autoOffsetReset;
	private int listener;
	private boolean enableAutoCommit;
	private int pollTimeout;
	private int autoCommitIntervalMs;
	private int sessionTimeoutMs;
	private String keyDeserializerClassConfig;
	private String valueDeserializerClassConifg;

}
