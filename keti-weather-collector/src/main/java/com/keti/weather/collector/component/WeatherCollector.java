package com.keti.weather.collector.component;

import java.util.Map;

import javax.annotation.PostConstruct;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONObject;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;

import com.keti.weather.collector.service.CurrentWeatherService;
import com.keti.weather.collector.service.VillageInfoService;
import com.keti.weather.collector.entity.VillageInfoEntity;
import com.keti.weather.collector.service.KafkaProducerService;


@Component
public class WeatherCollector extends AbstractDynamicScheduled implements CommandLineRunner {

	@Value("${spring.collector.target}")
	private String target = null;
	@Value("${spring.collector.interval}")
	private int interval = 0;
	private String cron = null;

	private List<int[]> pointList;
	private Map<String, List<VillageInfoEntity>> groupPointMap;

	private final VillageInfoService villageInfoService;
	private final CurrentWeatherService weatherService;
	private final KafkaProducerService kafkaProducerService;

	private final Logger logger = LoggerFactory.getLogger(this.getClass());


	public WeatherCollector(VillageInfoService villageInfoService, CurrentWeatherService weatherService, KafkaProducerService kafkaProducerService) {
		this.villageInfoService = villageInfoService;
		this.weatherService = weatherService;
		this.kafkaProducerService = kafkaProducerService;
	}


	@PostConstruct
	public void init() {
		logger.info("##### Init Configuration");
		logger.info("##### target " + target);
		logger.info("##### interval " + interval);

		pointList = villageInfoService.getPoints(target);
		groupPointMap = villageInfoService.getGroupPointMap();
	}


	@Override
	public void runner() {
		try {
			logger.info("##### Scheduler Job");
			List<JSONObject> weatherDataList = weatherService.getWeatherDataList(pointList, groupPointMap);
			kafkaProducerService.sendMessage(weatherDataList);
		} catch (Exception e) {
			logger.info("[Exception: " + e + " ]");
		} finally {

		}
	}


	@Override
	public Trigger getTrigger() {
		switch (interval) {
			case -1:
				cron = "0 * * * * *";
				break;

			case 1:
				cron = "0 0 * * * *";
				break;
			
			case 24:
				cron = "0 0 0 * * *";
				break;
		
			default:
				if(interval > 1 && interval < 24) {
					cron = "0 0 0/" + interval  + " * * *";
				}
				break;
		}

		logger.info("##### cron " + cron);
		return new CronTrigger(cron);
	}


	@Override
	public void run(String... args) throws Exception {
		startScheduler();
	}
    
}