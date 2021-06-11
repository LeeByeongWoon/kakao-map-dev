package com.keti.kafka.producer.weather.component;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.simple.JSONObject;

import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Autowired;

import com.keti.kafka.producer.weather.service.WeatherService;
import com.keti.kafka.producer.weather.entity.VillageInfoEntity;
import com.keti.kafka.producer.weather.service.VillageInfoService;
import com.keti.kafka.producer.weather.service.KafkaProducerService;


@Component
public class ProducerKafka {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	VillageInfoService villageInfoService;

	@Autowired
	WeatherService weatherService;

	@Autowired
	KafkaProducerService kafkaProducerService;

	public static final Map<String, List<VillageInfoEntity>> _pointGroupData = new HashMap<>();


	@PostConstruct
	public void init() {
		logger.info("##########################");
		logger.info("##### Initialization #####");
		logger.info("##########################");

		List<VillageInfoEntity> viAll = villageInfoService.getViAll();
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
				String key = nx + "." + ny;
				
				end += viPointGrpCnt.get(i)[2];


				List<VillageInfoEntity> pointList = new ArrayList<>();

				for(int j=start; j<end; j++) {
					pointList.add(viAll.get(j));
				}
				_pointGroupData.put(key, pointList);


				start += viPointGrpCnt.get(i)[2];
			}
		}
		
	}


	@Scheduled(cron = "0 45 * * * *")
	public void collect() {
		try {
			logger.info("#########################################");
			logger.info("##### Scheduled 0 45 * * * * Start #####");
			logger.info("#########################################");
			
			List<int[]> enablePointList = villageInfoService.getEnablePoint(true);
			List<JSONObject> weatherDataList = weatherService.getRequestPointData(enablePointList);

			kafkaProducerService.sendMessage(weatherDataList);

		} catch (Exception e) {
			logger.info("[Exception: " + e + " ]");
		} finally {

		}
	}
    
}