package com.keti.collector.component;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONObject;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;

import com.keti.collector.service.CurrentWeatherService;
import com.keti.collector.service.VillageInfoService;
import com.keti.collector.entity.VillageInfoEntity;
import com.keti.collector.service.KafkaProducerService;


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
		logger.info("##### Scheduler Job");

		for (int[] point : pointList) {
			JSONObject weatherData = weatherService.getWeatherData(point);

			if(weatherData != null) {
				String key = Integer.toString(point[0]) + "." + Integer.toString(point[1]);
				List<VillageInfoEntity> entityByPoints = groupPointMap.get(key);
				List<JSONObject> weatherDatas = weatherService.getJoinData(weatherData, entityByPoints);

				kafkaProducerService.sendMessage(weatherDatas);
			}
		}

		// List<JSONObject> weatherDataList = weatherService.getWeatherDataList(pointList, groupPointMap);
		// kafkaProducerService.sendMessages(weatherDataList);
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