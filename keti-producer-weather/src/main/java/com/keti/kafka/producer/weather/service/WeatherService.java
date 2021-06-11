package com.keti.kafka.producer.weather.service;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.keti.kafka.producer.weather.component.ProducerKafka;
import com.keti.kafka.producer.weather.entity.VillageInfoEntity;


@Service
public class WeatherService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ObjectMapper objectMapper;

    JSONParser parser = new JSONParser();

    @Value("${spring.api.weather.url}")
    private String url;

    @Value("${spring.api.weather.service-key}")
    private String serviceKey;


    public List<JSONObject> getRequestPointData(List<int[]> enableVillageList) throws Exception {
        List<JSONObject> weatherDataList = new ArrayList<>();


        int enableVillageSize = enableVillageList.size();
        
        String now = new SimpleDateFormat("yyyyMMdd HHmm").format(new Date());
        String[] nowArr = now.split(" ");
        String date = nowArr[0];
        String time = nowArr[1];

        for(int cnt=0; cnt<enableVillageSize; cnt++) {
            int[] point = enableVillageList.get(cnt);

            String pageNo = "1";
            String numOfRows = "32";
            String dataType = "JSON";
            String baseDate = date;
            String baseTime = time;
            int nx = point[0];
            int ny = point[1];

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            RestTemplate restTemplate = new RestTemplate();
            UriComponents uri = UriComponentsBuilder.fromHttpUrl(url)
                                    .queryParam("serviceKey", serviceKey)
                                    .queryParam("pageNo", pageNo)
                                    .queryParam("numOfRows", numOfRows)
                                    .queryParam("dataType", dataType)
                                    .queryParam("base_date", baseDate)
                                    .queryParam("base_time", baseTime)
                                    .queryParam("nx", nx)
                                    .queryParam("ny", ny)
                                    .build(true);

            ResponseEntity<String> response = restTemplate.exchange(uri.toUri(), HttpMethod.GET, new HttpEntity<String>(headers), String.class);
            
            Instant timestamp = Instant.now();
            int statusCodeValue = response.getStatusCodeValue();

            logger.info("[Collect(" + (cnt+1) + "/" + enableVillageSize + ") | HttpStatusCode=" + statusCodeValue + "]");

            if(statusCodeValue >= 200 && statusCodeValue <= 300) { 
                String key = nx + "." + ny;

                Map<String, List<VillageInfoEntity>> pointGroupData = ProducerKafka._pointGroupData;
                List<VillageInfoEntity> pointDataList = pointGroupData.get(key);
                JSONObject responseData = (JSONObject) parser.parse(response.getBody());

                int pointDataSize = pointDataList.size();
                List<JSONObject> messages = new ArrayList<>();
                
                for(int i=0; i<pointDataSize; i++) {
                    JSONObject requestData =
                            objectMapper.convertValue(pointDataList.get(i), new TypeReference<JSONObject>(){});

                    HashMap<String, Object> messageData = new HashMap<>();
                    messageData.put("timestamp", String.valueOf(timestamp));
                    messageData.put("statusCodeValue", statusCodeValue);
                    messageData.put("requestData", requestData);
                    messageData.put("responseData", responseData.get("response"));

                    messages.add(new JSONObject(messageData));
                }

                HashMap<String, Object> weatherData = new HashMap<>();
                weatherData.put("messages", messages);
                
                weatherDataList.add(new JSONObject(weatherData));
            }
        }

        return weatherDataList;
    }
    
}