package com.keti.weather.collector.component;

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

import com.keti.weather.collector.service.WeatherService;
import com.keti.weather.collector.entity.VillageInfoEntity;
import com.keti.weather.collector.service.VillageInfoService;
import com.keti.weather.collector.service.KafkaProducerService;


@Component
public class ProducerKafka {

	@Value("${spring.weatherApi.target-ids}")
	private String targetIds;

	@Value("${spring.weatherApi.scrap-interval}")
	private String scrapInterval = null;

	@Value("${spring.weatherApi.scheduled-cron}")
	private String scheduledCron = null;

	@Value("${spring.weatherApi.leap-time-collector}")
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
		logger.info("################## Initialization ##################");
		logger.info("##### --target-id: " + targetIds);
		logger.info("##### --scrap-interval: " + scrapInterval);
		logger.info("##### --scheduled-cron: " + scheduledCron);
		logger.info("##### --leap-time-collector: " + leapTimeCollector);
		logger.info("####################################################");


		setMappingPoint();

		if(leapTimeCollector) {
			getLeapTimeCollector();
			leapTimeCollector = false;
		}
	}


	public void setMappingPoint() {
		List<VillageInfoEntity> viAll = villageInfoService.getAllVi();
		List<int[]> viPointGrpCnts = villageInfoService.getViPointGrpCnts();

		int viAllSize = viAll.size();
		int viPointGrpCntsSize = viPointGrpCnts.size();

		if(viAllSize <= 0 && viPointGrpCntsSize <= 0) {
			viAll = null;
			viPointGrpCnts = null;
		} else {
			int start = 0;
			int end = 0;
			for(int i=0; i<viPointGrpCntsSize; i++) {
				String nx = Integer.toString(viPointGrpCnts.get(i)[0]);
				String ny = Integer.toString(viPointGrpCnts.get(i)[1]);
				int pointCnt = viPointGrpCnts.get(i)[2];

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
			List<int[]> points = villageInfoService.getPoints(targetIds);
			List<List<JSONObject>> leapDataList = weatherService.getLeapTimeData(points);

			kafkaProducerService.sendLeapTimeData(leapDataList);

		} catch (Exception e) {
			logger.info("[Exception: " + e + " ]");
		} finally {

		}
	}


	@Scheduled(cron = "${spring.weatherApi.scheduled-cron}")
	public void getRealTimeCollector() {
		try {
			logger.info("##### Scheduled " + scheduledCron + " Start #####");
			
			List<int[]> points = villageInfoService.getPoints(targetIds);
			List<JSONObject> weatherDatas = weatherService.getRealTimeData(points);

			kafkaProducerService.sendRealTimeData(weatherDatas);
			
		} catch (Exception e) {
			logger.info("[Exception: " + e + " ]");
		} finally {

		}
	}
    
}