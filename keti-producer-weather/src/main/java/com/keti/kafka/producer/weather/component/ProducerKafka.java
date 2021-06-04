package com.keti.kafka.producer.weather.component;

import java.util.Map;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;

import com.keti.kafka.producer.weather.service.WeatherService;
import com.keti.kafka.producer.weather.service.KafkaProducerService;
import com.keti.kafka.producer.weather.service.VillageInfoService;
import com.keti.kafka.producer.weather.entity.VillageInfoEntity;


@Component
public class ProducerKafka {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	VillageInfoService villageInfoService;

	@Autowired
	WeatherService weatherService;

	@Autowired
	KafkaProducerService kafkaProducerService;


	@Scheduled(cron = "0 * * * * *")
	public void collect() {
		try {
			logger.info("###########################");
			logger.info("##### Start Scheduled #####");
			logger.info("###########################");

			List<VillageInfoEntity> enableVillageList = villageInfoService.getEnabledVillage();
			List<Map<String, Object>> weatherDataList = weatherService.getRequestPointData(enableVillageList);

			kafkaProducerService.sendMessage(weatherDataList);

		} catch (Exception e) {
			logger.info("##### " + e + " #####");
		} finally {

		}
	}
    
}