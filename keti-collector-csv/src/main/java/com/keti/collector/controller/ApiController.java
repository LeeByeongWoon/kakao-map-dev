package com.keti.collector.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.text.ParseException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.json.simple.JSONObject;
import org.apache.commons.fileupload.FileUploadException;

import com.keti.collector.service.GenerateMetaService;
import com.keti.collector.service.GenerateTimeSeriesService;
import com.keti.collector.service.MultipartService;
import com.keti.collector.vo.GenerateVo;
import com.mongodb.MongoException;


@RestController
@RequestMapping(value = "/api")
public class ApiController {

    private final MultipartService multipartService;
    private final GenerateMetaService generateMetaService;
    private final GenerateTimeSeriesService generateTimeSeriesService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public ApiController(
            MultipartService _multipartService,
            GenerateMetaService _generateMetaService,
            GenerateTimeSeriesService _generateTimeSeriesService) {
        this.multipartService = _multipartService;
        this.generateMetaService = _generateMetaService;
        this.generateTimeSeriesService = _generateTimeSeriesService;
    }


    @RequestMapping(value = "/generate/{type}", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> apiGenerateTimeSeries(@PathVariable("type") String type, @RequestBody GenerateVo generateVo) {
        ResponseEntity<JSONObject> responseEntity = null;

        try {
            Map<String, Object> apiResponse = new HashMap<>();
            JSONObject databasesJsonObject = null;
            JSONObject measurementsJsonObject = null;
            JSONObject metasJsonObject = null;

            switch (type) {
                case "input":
                    apiResponse.put("type", "generatedByInput");

                    databasesJsonObject = generateTimeSeriesService.generatedByDatabase(generateVo);
                    measurementsJsonObject = generateTimeSeriesService.generatedByInput(generateVo);
                    metasJsonObject = generateMetaService.generatedByMeta(databasesJsonObject, measurementsJsonObject);
                    break;

                case "columns":
                    apiResponse.put("type", "generatedByColumns");

                    databasesJsonObject = generateTimeSeriesService.generatedByDatabase(generateVo);
                    measurementsJsonObject = generateTimeSeriesService.generatedByColumns(generateVo);
                    metasJsonObject = generateMetaService.generatedByMeta(databasesJsonObject, measurementsJsonObject);
                    break;
            
                default:
                    apiResponse.put("type", "generatedByDefault");
                    databasesJsonObject = new JSONObject();
                    measurementsJsonObject = new JSONObject();
                    metasJsonObject = new JSONObject();
                    break;
            }

            apiResponse.put("databases", databasesJsonObject);
            apiResponse.put("measurements", measurementsJsonObject);
            apiResponse.put("metas", metasJsonObject);

            responseEntity = new ResponseEntity<JSONObject>(new JSONObject(apiResponse), HttpStatus.OK);
            
        } catch (ParseException ex) {
            responseEntity = responseExcetion("apiGenerateTimeSeries - ParseException", ex.toString());
        } catch (IOException ex) {
            responseEntity = responseExcetion("apiGenerateTimeSeries - IOException", ex.toString());
        } catch (NullPointerException ex) {
            responseEntity = responseExcetion("apiGenerateTimeSeries - NullPointerException", ex.toString());
        } catch (MongoException ex) {
            responseEntity = responseExcetion("apiGenerateMeta - MongoException", ex.toString());
        }

        return responseEntity;
    }


    @RequestMapping(value = "/generate/file", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> apiFileUpload(HttpServletRequest request) {
        ResponseEntity<JSONObject> responseEntity = null;

        try {
            responseEntity = new ResponseEntity<JSONObject>(multipartService.fileUpload(request), HttpStatus.OK);
            
        } catch (FileUploadException ex) {
            responseEntity = responseExcetion("apiFileUpload - FileUploadException", ex.toString());
        } catch (IOException ex) {
            responseEntity = responseExcetion("apiFileUpload - IOException", ex.toString());
        } catch (NullPointerException ex) {
            responseEntity = responseExcetion("apiGenerateSchema - NullPointerException", ex.toString());
        }

        return responseEntity;
    }


    @RequestMapping(value = "/validation/{type}", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> apiValidation(
            @PathVariable("type") String type,
            @RequestParam(required = true, value="main_domain") String mainDomain,
            @RequestParam(required = true, value="sub_domain") String subDomain,
            @RequestParam(required = false, value="measurement") String measurement) {
        String database = mainDomain + "__" + subDomain;

        ResponseEntity<JSONObject> responseEntity = null;
        Map<String, List<String>> apiResponse = new HashMap<>();

        try {
            if(type == "input") {
                apiResponse.put("databases", generateTimeSeriesService.databaseInValidation(database));
                apiResponse.put("measurements", generateTimeSeriesService.measurementInValidation(database, measurement));
            } else if(type == "columns") {
                apiResponse.put("databases", generateTimeSeriesService.databaseInValidation(database));
                apiResponse.put("measurements", new ArrayList<String>());
            } else {
                apiResponse.put("databases", new ArrayList<String>());
                apiResponse.put("measurements", new ArrayList<String>());
            }

            responseEntity = new ResponseEntity<JSONObject>(new JSONObject(apiResponse), HttpStatus.OK);

        } catch (Exception ex) {
            apiResponse.put("databases", new ArrayList<String>());
            apiResponse.put("measurements", new ArrayList<String>());
            responseEntity = new ResponseEntity<JSONObject>(new JSONObject(apiResponse), HttpStatus.OK);
        }

        return responseEntity;
    }


    public ResponseEntity<JSONObject> responseExcetion(String type, String message) {
        logger.info(type + ": " + message);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("type", type);
        responseBody.put("message", message);

        return new ResponseEntity<JSONObject>(new JSONObject(responseBody), HttpStatus.OK);
    }
    
}