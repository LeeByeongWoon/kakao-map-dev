package com.keti.kafka.producer.weather.component;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.keti.kafka.producer.weather.service.WeatherService;
import com.keti.kafka.producer.weather.entity.VillageInfoEntity;
import com.keti.kafka.producer.weather.service.VillageInfoService;
import com.keti.kafka.producer.weather.service.KafkaProducerService;


@Component
public class ProducerKafka {

	@Value("${spring.target-id}")
	private int targetId;

	@Value("${spring.scheduled-cron}")
	private String scheduledCron = null;

	@Value("${spring.leap-time-collector}")
	private Boolean leapTimeCollector = null;

	@Autowired
    ObjectMapper objectMapper;

	@Autowired
	VillageInfoService villageInfoService;

	@Autowired
	WeatherService weatherService;

	@Autowired
	KafkaProducerService kafkaProducerService;

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final Map<String, List<VillageInfoEntity>> _pointGroupData = new HashMap<>();


	@PostConstruct
	public void init() {
		logger.info("##### Initialization #####");
		logger.info("--target-id: " + targetId);
		logger.info("--scheduled-cron: " + scheduledCron);
		logger.info("--leap-time-collector: " + leapTimeCollector);


		setMappingPoint();

		if(leapTimeCollector) {
			getLeapTimeCollector();
			leapTimeCollector = false;
		}
	}


	public void setMappingPoint() {
		List<VillageInfoEntity> viAll = villageInfoService.getAllVi();
		List<int[]> viPointGrpCnt = villageInfoService.getViPointGrpCnt();

		int viAllSize = viAll.size();
		int viPointGrpCntListSize = viPointGrpCnt.size();

		if(viAllSize <= 0 && viPointGrpCntListSize <= 0) {
			viAll = null;
			viPointGrpCnt = null;
		} else {
			int start = 0;
			int end = 0;
			for(int i=0; i<viPointGrpCntListSize; i++) {
				String nx = Integer.toString(viPointGrpCnt.get(i)[0]);
				String ny = Integer.toString(viPointGrpCnt.get(i)[1]);
				int pointCnt = viPointGrpCnt.get(i)[2];

				String key = nx + "." + ny;
				
				end += pointCnt;

				List<VillageInfoEntity> pointList = new ArrayList<>();

				for(int j=start; j<end; j++) {
					pointList.add(viAll.get(j));
				}
				_pointGroupData.put(key, pointList);

				start += pointCnt;
			}
		}
	}

	
	public void getLeapTimeCollector() {
		try {
			List<int[]> enablePointList = villageInfoService.getViTarget(targetId);
			List<List<JSONObject>> leapDataList = weatherService.getLeapTimeData(enablePointList);

			kafkaProducerService.sendLeapTimeData(leapDataList);

		} catch (Exception e) {
			logger.info("[Exception: " + e + " ]");
		} finally {

		}
	}


	@Scheduled(cron = "${spring.scheduled-cron}")
	public void getRealTimeCollector() {
		try {
			logger.info("##### Scheduled " + scheduledCron + " Start #####");
			
			List<int[]> enablePointList = villageInfoService.getViTarget(targetId);
			List<JSONObject> weatherDataList = weatherService.getRealTimeData(enablePointList);

			kafkaProducerService.sendRealTimeData(weatherDataList);
			
		} catch (Exception e) {
			logger.info("[Exception: " + e + " ]");
		} finally {

		}
	}
    
}