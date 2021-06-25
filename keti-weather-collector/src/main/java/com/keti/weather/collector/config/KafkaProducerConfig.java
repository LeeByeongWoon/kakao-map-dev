package com.keti.weather.collector.config;

import java.util.Map;
import java.util.HashMap;

import org.springframework.context.annotation.Configuration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;


@Configuration
@EnableConfigurationProperties(KafkaProducerProperties.class)
public class KafkaProducerConfig {

    @Autowired
    private KafkaProducerProperties properties;
    

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        props.put(ProducerConfig.ACKS_CONFIG, properties.getAcks());
        props.put(ProducerConfig.RETRIES_CONFIG, properties.getRetries());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, properties.getBatchSize());
        props.put(ProducerConfig.LINGER_MS_CONFIG, properties.getLingerMs());
        // props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, properties.getRequestTimeoutMs());
        // props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, properties.getDeliveryTimeoutMs());
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, properties.getBufferMemory());
        // props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, properties.getMaxBlockMs());
        // props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, properties.getMaxRequestSize());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, properties.getKeySerializer());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, properties.getValueSerializer());
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, properties.getCompresstionType());
        
        return props;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate(ProducerFactory<String, String> producerFactory) {
        return new KafkaTemplate<String, String>(producerFactory);
    }

}