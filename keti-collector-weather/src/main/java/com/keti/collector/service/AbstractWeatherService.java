package com.keti.collector.service;

import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.json.simple.JSONObject;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.keti.collector.entity.VillageInfoEntity;


public abstract class AbstractWeatherService {

    public UriComponents createRequestUri(String endPoint, String endPointService, String endPointServiceKey, LocalDateTime dt, int[] point) {
        String serviceUrl =  endPoint + endPointService;
        String serviceKey = endPointServiceKey;
        String pageNo = "1";
        String numOfRows = "12";
        String dataType = "JSON";
        String baseDate = dt.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String baseTime = dt.format(DateTimeFormatter.ofPattern("HH")) + "00";
        String nx = Integer.toString(point[0]);
        String ny = Integer.toString(point[1]);

        UriComponents uri = UriComponentsBuilder.fromHttpUrl(serviceUrl)
                                                .queryParam("serviceKey", serviceKey)
                                                .queryParam("pageNo", pageNo)
                                                .queryParam("numOfRows", numOfRows)
                                                .queryParam("dataType", dataType)
                                                .queryParam("base_date", baseDate)
                                                .queryParam("base_time", baseTime)
                                                .queryParam("nx", nx)
                                                .queryParam("ny", ny)
                                                .build(true);

        return uri;
    }

    
    public ResponseEntity<String> requestApi(UriComponents uri) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = 
                restTemplate.exchange(uri.toUri(), HttpMethod.GET, new HttpEntity<String>(headers), String.class);

        return responseEntity;
    }


    public abstract List<JSONObject> getWeatherDataList(List<int[]> pointList, Map<String, List<VillageInfoEntity>> groupPointMap) throws Exception;
    public abstract JSONObject getWeatherData(int[] point);
    public abstract List<JSONObject> getJoinData(JSONObject weatherData, List<VillageInfoEntity> entityByPoints);

}
