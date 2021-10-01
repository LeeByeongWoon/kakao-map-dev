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
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.json.simple.JSONObject;
import org.apache.commons.fileupload.FileUploadException;

import com.keti.collector.service.GenerateSchemaService;
import com.keti.collector.service.MultipartService;
import com.keti.collector.vo.GenerateVo;


@RestController
@RequestMapping(value = "/api")
public class ApiController {

    private final MultipartService multipartService;
    private final GenerateSchemaService generateSchemaService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    public ApiController(MultipartService multipartService, GenerateSchemaService generateSchemaService) {
        this.multipartService = multipartService;
        this.generateSchemaService = generateSchemaService;
    }


    @RequestMapping(value = "/validation/{type}", method = RequestMethod.GET)
    public ResponseEntity<JSONObject> apiValidation(
            @PathVariable("type") String type,
            @RequestParam(required = true, value="main_domain") String mainDomain,
            @RequestParam(required = true, value="sub_domain") String subDomain,
            @RequestParam(required = false, value="measurement") String measurement) {
        ResponseEntity<JSONObject> responseEntity = null;
        Map<String, List<String>> apiResponse = new HashMap<>();

        logger.info("type: " + type);

        try {            
            switch (type) {
                case "input":
                    apiResponse.put("databases", generateSchemaService.validationByDatabase(mainDomain, subDomain));
                    apiResponse.put("measurements", generateSchemaService.validationByMeasurement(mainDomain, subDomain, measurement));
                    responseEntity = new ResponseEntity<JSONObject>(new JSONObject(apiResponse), HttpStatus.OK);
                    break;
    
                case "columns":
                    apiResponse.put("databases", generateSchemaService.validationByDatabase(mainDomain, subDomain));
                    apiResponse.put("measurements", new ArrayList<String>());
                    responseEntity = new ResponseEntity<JSONObject>(new JSONObject(apiResponse), HttpStatus.OK);
                    break;
            
                default:
                    break;
            }
        } catch (Exception e) {
            apiResponse.put("databases", new ArrayList<String>());
            apiResponse.put("measurements", new ArrayList<String>());
            responseEntity = new ResponseEntity<JSONObject>(new JSONObject(apiResponse), HttpStatus.OK);
        }

        return responseEntity;
    }


    @RequestMapping(value = "/files", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> apiFileUpload(HttpServletRequest request) {
        ResponseEntity<JSONObject> responseEntity = null;

        try {
            responseEntity = new ResponseEntity<JSONObject>(multipartService.fileUpload(request), HttpStatus.OK);
            
        } catch (FileUploadException ex) {
            responseEntity = responseExcetion("FileUploadException", ex.toString());
        } catch (IOException ex) {
            responseEntity = responseExcetion("IOException", ex.toString());
        }

        return responseEntity;
    }


    @RequestMapping(value = "/generate/{type}", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> apiGenerateSchema(@PathVariable("type") String type, @RequestBody GenerateVo generateVo) {
        ResponseEntity<JSONObject> responseEntity = null;

        try {
            Map<String, String> apiResponse = new HashMap<>();

            switch (type) {
                case "input":
                    apiResponse.put("generateDatabase", generateSchemaService.generateByDatabase(generateVo));
                    apiResponse.put("useDatabase", generateSchemaService.useDatabase(generateVo));
                    apiResponse.put("generateByInput", generateSchemaService.generateByInput(generateVo));
        
                    responseEntity = new ResponseEntity<JSONObject>(new JSONObject(apiResponse), HttpStatus.OK);
                    break;
                
                case "columns":
                    apiResponse.put("generateDatabase", generateSchemaService.generateByDatabase(generateVo));
                    apiResponse.put("useDatabase", generateSchemaService.useDatabase(generateVo));
                    apiResponse.put("generateByColumns", generateSchemaService.generateByColumns(generateVo));
        
                    responseEntity = new ResponseEntity<JSONObject>(new JSONObject(apiResponse), HttpStatus.OK);
                    break;
            }
            
        } catch (ParseException ex) {
            responseEntity = responseExcetion("ParseException", ex.toString());
        } catch (IOException ex) {
            responseEntity = responseExcetion("IOException", ex.toString());
        }

        return responseEntity;
    }


    public ResponseEntity<JSONObject> responseExcetion(String type, String message) {
        logger.info(type + ": " + message);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("type", type);
        responseBody.put("message", message);

        return new ResponseEntity<JSONObject>(new JSONObject(responseBody), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
}
