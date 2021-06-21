package com.keti.kafka.producer.weather.component;

import java.util.Map;
import java.util.Set;
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
import org.springframework.boot.ApplicationArguments;

import com.keti.kafka.producer.weather.service.WeatherService;
import com.keti.kafka.producer.weather.entity.VillageInfoEntity;
import com.keti.kafka.producer.weather.service.VillageInfoService;
import com.keti.kafka.producer.weather.service.KafkaProducerService;


@Component
public class ProducerKafka {

	@Autowired
	ApplicationArguments applicationArguments;

	@Autowired
    ObjectMapper objectMapper;

	@Autowired
	VillageInfoService villageInfoService;

	@Autowired
	WeatherService weatherService;

	@Autowired
	KafkaProducerService kafkaProducerService;

	final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final Map<String, Boolean> _args = new HashMap<>();
	public static final Map<String, List<VillageInfoEntity>> _pointGroupData = new HashMap<>();


	@PostConstruct
	public void init() {
		logger.info("##########################");
		logger.info("##### Initialization #####");
		logger.info("##########################");

		setArgs();
		setMappingPoint();
		
		Set<String> keys = _args.keySet();
		
		for (String key : keys) {
			if(_args.get(key)) {
				switch (key) {
					case "LeapTimeCollector":
						getLeapTimeCollector();
						_args.put("LeapTimeCollector", false);
						break;
				
					default:
						break;
				}
			}
		}
	}


	public void setArgs() {
		Set<String> optionNames = applicationArguments.getOptionNames();

		for (String optionName : optionNames) {
			List<String> optionValues = applicationArguments.getOptionValues(optionName);

			for (String optionValue : optionValues) {
				Boolean convertOptionValue = Boolean.parseBoolean(optionValue);
				_args.put(optionName, convertOptionValue);
			}
		}
	}


	public void setMappingPoint() {
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

	
	public void getLeapTimeCollector() {
		try {
			logger.info("[Leap system setup status - " + _args.get("LeapTimeCollector") + "]");
			
			List<int[]> enablePointList = villageInfoService.getEnablePoint(true);
			List<List<JSONObject>> leapDataList = weatherService.getLeapTimeData(enablePointList);

			kafkaProducerService.sendLeapTimeData(leapDataList);

		} catch (Exception e) {
			logger.info("[Exception: " + e + " ]");
		} finally {

		}
	}


	@Scheduled(cron = "0 0 * * * *")
	public void getRealTimeCollector() {
		try {
			logger.info("#########################################");
			logger.info("##### Scheduled 0 0 * * * * Start #####");
			logger.info("#########################################");
			
			List<int[]> enablePointList = villageInfoService.getEnablePoint(true);
			List<JSONObject> weatherDataList = weatherService.getRealTimeData(enablePointList);

			kafkaProducerService.sendRealTimeData(weatherDataList);
			
		} catch (Exception e) {
			logger.info("[Exception: " + e + " ]");
		} finally {

		}
	}
    
}