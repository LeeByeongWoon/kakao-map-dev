package com.keti.kafka.producer.weather.service;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

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

    @Value("${spring.weather-api.url}")
    private String url;

    @Value("${spring.weather-api.service-key}")
    private String serviceKey;


    public List<JSONObject> getRequestPointData(List<Map<String, Object>> queryParams) throws Exception {
        List<JSONObject> weatherDataList = new ArrayList<>();


        int queryParamsSize = queryParams.size();
        for(int cnt=0; cnt<queryParamsSize; cnt++) {
            Map<String, Object> queryParam = queryParams.get(cnt);

            String pageNo = queryParam.get("pageNo").toString();
            String numOfRows = queryParam.get("numOfRows").toString();
            String dataType = queryParam.get("dataType").toString();
            Instant kst = Instant.parse(queryParam.get("kst").toString());
            Instant utc = Instant.parse(queryParam.get("utc").toString());
            String baseDate = queryParam.get("baseDate").toString();
            String baseTime = queryParam.get("baseTime").toString();
            int nx = Integer.parseInt(queryParam.get("nx").toString());
            int ny = Integer.parseInt(queryParam.get("ny").toString());

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

            ResponseEntity<String> response = 
                    restTemplate.exchange(uri.toUri(), HttpMethod.GET, new HttpEntity<String>(headers), String.class);
            
            
            String convertKst = kst.toString();
            String convertUtc = utc.toString();

            int statusCodeValue = response.getStatusCodeValue();
            logger.info(convertKst + " - [Collect(" + (cnt+1) + "/" + queryParamsSize + ") | HttpStatusCode=" + statusCodeValue + "]");

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
                    messageData.put("timestamp", convertUtc);
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


    public List<JSONObject> getRealTimeData(List<int[]> enableVillageList) throws Exception {
        List<JSONObject> weatherDataList = null;

        
        List<Map<String, Object>> queryParams = new ArrayList<>();
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime baseDt = now.minusHours(1);

        String date = baseDt.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String time = baseDt.format(DateTimeFormatter.ofPattern("HH")) + "00";

        for (int[] enableVillage : enableVillageList) {
            Map<String, Object> queryParam = new HashMap<>();
            queryParam.put("pageNo", "1");
            queryParam.put("numOfRows", "12");
            queryParam.put("dataType", "JSON");
            queryParam.put("kst", now.toInstant(ZoneOffset.UTC));
            queryParam.put("utc", now.minusHours(9).toInstant(ZoneOffset.UTC));
            queryParam.put("baseDate", date);
            queryParam.put("baseTime", time);
            queryParam.put("nx", enableVillage[0]);
            queryParam.put("ny", enableVillage[1]);

            queryParams.add(queryParam);
        }

        weatherDataList = getRequestPointData(queryParams);

        return weatherDataList;
    }


    public List<List<JSONObject>> getLeapTimeData(List<int[]> enableVillageList) throws Exception {
        List<List<JSONObject>> leapDataList = new ArrayList<>();


        List<List<Map<String, Object>>> minusQueryParams = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        int min = 0;
        int max = 23;
        for(int cnt=max; cnt>min; cnt--) {
            List<Map<String, Object>> queryParams = new ArrayList<>();

            
            LocalDateTime baseDt = now.minusHours(cnt);

            String date = baseDt.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String time = baseDt.format(DateTimeFormatter.ofPattern("HH")) + "00";

            for (int[] enableVillage : enableVillageList) {
                Map<String, Object> queryParam = new HashMap<>();
                queryParam.put("pageNo", "1");
                queryParam.put("numOfRows", "12");
                queryParam.put("dataType", "JSON");
                queryParam.put("kst", baseDt.toInstant(ZoneOffset.UTC));
                queryParam.put("utc", baseDt.minusHours(9).toInstant(ZoneOffset.UTC));
                queryParam.put("baseDate", date);
                queryParam.put("baseTime", time);
                queryParam.put("nx", enableVillage[0]);
                queryParam.put("ny", enableVillage[1]);

                queryParams.add(queryParam);
            }

            minusQueryParams.add(queryParams);
        }

        for(List<Map<String, Object>> minusQueryParam : minusQueryParams) {
            List<JSONObject> weatherDataList = getRequestPointData(minusQueryParam);

            leapDataList.add(weatherDataList);
        }

        return leapDataList;
    }

}