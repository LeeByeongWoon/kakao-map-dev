package com.keti.weather.launcher.config;

import java.util.Map;
import java.util.HashMap;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;
// import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;


@Configuration
@EnableConfigurationProperties(KafkaConsumerProperties.class)
public class KafkaConsumerConfig {

    @Autowired
	private KafkaConsumerProperties properties = null;

    @Autowired
    private MessageListener messageListener = null;


    @Bean
    public ConcurrentMessageListenerContainer<String, String> kafkaMessageListenerContainer() {
        ContainerProperties containerProperties = new ContainerProperties(properties.getTopics());
        containerProperties.setPollTimeout(properties.getPollTimeout());

        ConcurrentMessageListenerContainer<String, String> container = new ConcurrentMessageListenerContainer<>(consumerFactory(), containerProperties);
        container.setupMessageListener(messageListener);
        container.setConcurrency(properties.getListener());
		container.setAutoStartup(false);

        return container;
    }


    // @Bean
    // public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
    //     ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
    //     factory.setConsumerFactory(consumerFactory());
    //     factory.setConcurrency(properties.getTask());
    //     factory.setAutoStartup(false);
    //     factory.getContainerProperties().setPollTimeout(properties.getPollTimeout());

    //     return factory;
    // }

    
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
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