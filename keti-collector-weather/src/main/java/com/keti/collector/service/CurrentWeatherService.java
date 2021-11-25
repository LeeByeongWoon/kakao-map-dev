package com.keti.collector.service;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponents;

import com.keti.collector.entity.VillageInfoEntity;


@Service
public class CurrentWeatherService extends AbstractWeatherService {

    @Value("${spring.weatherApi.end-point}")
    private String endPoint;
    @Value("${spring.weatherApi.end-point-service}")
    private String endPointService;
    @Value("${spring.weatherApi.end-point-service-key}")
    private String endPointServiceKey;

    private JSONParser parser = new JSONParser();

    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CurrentWeatherService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    

    @Override
    public List<JSONObject> getWeatherDataList(List<int[]> pointList, Map<String, List<VillageInfoEntity>> groupPointMap) throws Exception {
        List<JSONObject> weatherDataList = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dt = now.minusHours(1);

        String kst = now.toInstant(ZoneOffset.UTC).toString();
        String utc = now.minusHours(9).toInstant(ZoneOffset.UTC).toString();

        int pointListSize = pointList.size();
        for(int cnt=0; cnt<pointListSize; cnt++) {
            int[] point = pointList.get(cnt);
            String nx = Integer.toString(point[0]);
            String ny = Integer.toString(point[1]);

            UriComponents uri = createRequestUri(endPoint, endPointService, endPointServiceKey, dt, point);
            ResponseEntity<String> responseEntity = requestApi(uri);

            int statusCodeValue = responseEntity.getStatusCodeValue();
            logger.info(kst + " - [Collect(" + (cnt+1) + "/" + pointListSize + ") | HttpStatusCode=" + statusCodeValue + "]");

            if(statusCodeValue >= 200 && statusCodeValue <= 300) { 
                String key = nx + "." + ny;

                List<VillageInfoEntity> pointDataList = groupPointMap.get(key);
                JSONObject responseData = (JSONObject) parser.parse(responseEntity.getBody());

                int pointDataSize = pointDataList.size();
                List<JSONObject> messages = new ArrayList<>();
                
                for(int i=0; i<pointDataSize; i++) {
                    JSONObject requestData =
                            objectMapper.convertValue(pointDataList.get(i), new TypeReference<JSONObject>(){});

                    HashMap<String, Object> messageData = new HashMap<>();
                    messageData.put("timestamp", utc);
                    messageData.put("statusCodeValue", statusCodeValue);
                    messageData.put("requestData", requestData);
                    messageData.put("responseData", responseData.get("response"));

                    messages.add(new JSONObject(messageData));
                }

                Map<String, Object> weatherData = new HashMap<>();
                weatherData.put("messages", messages);
                
                weatherDataList.add(new JSONObject(weatherData));
            }
        }

        return weatherDataList;
    }


    @Override
    public JSONObject getWeatherData(int[] point) {
        JSONObject weatherData = null;

        try {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime dt = now.minusHours(1);
            String kst = now.toInstant(ZoneOffset.UTC).toString();

            UriComponents uri = createRequestUri(endPoint, endPointService, endPointServiceKey, dt, point);
            ResponseEntity<String> responseEntity = requestApi(uri);

            int statusCodeValue = responseEntity.getStatusCodeValue();
            logger.info(kst + " - [Collect - HttpStatusCode=" + statusCodeValue + "]");

            if(statusCodeValue >= 200 && statusCodeValue <= 300) {
                weatherData = (JSONObject) parser.parse(responseEntity.getBody());
                weatherData.put("statusCodeValue", statusCodeValue);
            } else {
                throw new Exception(Integer.toString(statusCodeValue) + "Error");
            }
        } catch (ParseException ex) {
            logger.info("ParseException: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            logger.info("Exception: " + ex.getMessage());
            ex.printStackTrace();
        }

        return weatherData;
    }


    @Override
    public List<JSONObject> getJoinData(JSONObject weatherData, List<VillageInfoEntity> entityByPoints) {
        List<JSONObject> weatherDatas = new ArrayList<>();

        LocalDateTime now = LocalDateTime.now();
        String utc = now.minusHours(9).toInstant(ZoneOffset.UTC).toString();

        for (VillageInfoEntity entityByPoint : entityByPoints) {
            HashMap<String, Object> messageData = new HashMap<>();
            messageData.put("timestamp", utc);
            messageData.put("statusCodeValue", weatherData.get("statusCodeValue"));
            messageData.put("requestData", objectMapper.convertValue(entityByPoint, new TypeReference<JSONObject>(){}));
            messageData.put("responseData", weatherData.get("response"));

            weatherDatas.add(new JSONObject(messageData));
        }

        return weatherDatas;
    }
}