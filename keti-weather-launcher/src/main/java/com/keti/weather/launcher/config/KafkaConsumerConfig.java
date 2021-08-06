package com.keti.weather.launcher.config;

import java.util.Map;
import java.util.HashMap;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;

import org.apache.kafka.clients.consumer.ConsumerConfig;


@Configuration
@EnableConfigurationProperties(KafkaConsumerProperties.class)
public class KafkaConsumerConfig {

	private final KafkaConsumerProperties properties;
    private final MessageListener<String, String> messageListener;


    public KafkaConsumerConfig(KafkaConsumerProperties properties, MessageListener<String, String> messageListener) {
        this.properties = properties;
        this.messageListener = messageListener;
    }


    @Bean
    public ConcurrentMessageListenerContainer<String, String> kafkaMessageListenerContainer() {
        ContainerProperties containerProperties = new ContainerProperties(properties.getTopics());
        containerProperties.setPollTimeout(properties.getPollTimeout());

        ConcurrentMessageListenerContainer<String, String> container =
                new ConcurrentMessageListenerContainer<>(new DefaultKafkaConsumerFactory<>(consumerConfigs()), containerProperties);
        container.setupMessageListener(messageListener);
        container.setConcurrency(properties.getListener());
		container.setAutoStartup(false);

        return container;
    }

    
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
		props.put(ConsumerConfig.GROUP_ID_CONFIG, properties.getGroupId());
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, properties.getAutoOffsetReset());
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, properties.isEnableAutoCommit());
		props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, properties.getAutoCommitIntervalMs()); 
		props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, properties.getSessionTimeoutMs());
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, properties.getKeyDeserializerClassConfig());
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, properties.getValueDeserializerClassConifg());

        return props;
    }

}