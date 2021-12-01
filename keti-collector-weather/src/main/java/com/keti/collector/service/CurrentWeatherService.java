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