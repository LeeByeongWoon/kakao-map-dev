package com.keti.kafka.producer.weather.service;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.keti.kafka.producer.weather.entity.VillageInfoEntity;


@Service
public class WeatherService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.api.weather.url}")
    private String url;

    @Value("${spring.api.weather.service-key}")
    private String serviceKey;


    public List<Map<String, Object>> getRequestPointData(List<VillageInfoEntity> enableVillageList) {
        List<Map<String, Object>> weatherDataList = new ArrayList<>();

        
        long enableVillageLength = enableVillageList.size();
        String today = new SimpleDateFormat("yyyyMMdd").format(new Date());

        for(int cnt=0; cnt<enableVillageLength; cnt++) {
            VillageInfoEntity villageInfo = enableVillageList.get(cnt);

            String pageNo = "1";
            String numOfRows = "255";
            String dataType = "JSON";
            String baseDate = today;
            String baseTime = "0500";
            int nx = villageInfo.getViNx();
            int ny = villageInfo.getViNy();

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

            long timestamp = System.currentTimeMillis();
            HttpStatus statusCode = response.getStatusCode();
            long statusCodeValue = response.getStatusCodeValue();
            Object requestBody = villageInfo;
            Object responseBody = response.getBody();

            logger.info("[Collect(" + (cnt+1) + "/" + enableVillageLength + ") | HttpStatusCode=" + statusCodeValue + "]");

            if(statusCodeValue >= 200 && statusCodeValue <= 300) {
                WeatherVo w_vo = new WeatherVo();
                w_vo.setTimestamp(timestamp);
                w_vo.setStatusCode(statusCode);
                w_vo.setStatusCodeValue(statusCodeValue);
                w_vo.setRequestBody(requestBody);
                w_vo.setResponseBody(responseBody);

                Map<String, Object> w_map = objectMapper.convertValue(w_vo, new TypeReference<Map<String, Object>>(){});
                weatherDataList.add(w_map);
            }
        }

        return weatherDataList;
    }
    

    @Data
    private class WeatherVo {

        private long timestamp;
        private HttpStatus statusCode;
        private long statusCodeValue;
        private Object requestBody;
        private Object responseBody;
    
    }
    
}