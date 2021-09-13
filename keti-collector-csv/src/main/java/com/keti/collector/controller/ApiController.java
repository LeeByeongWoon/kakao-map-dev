package com.keti.collector.controller;

import java.util.HashMap;
import java.util.Map;

import java.io.IOException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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


    @RequestMapping(value = "/files", method = RequestMethod.POST)
    public ResponseEntity<JSONObject> apiFileUpload(HttpServletRequest request) {
        ResponseEntity<JSONObject> responseEntity = null;

        try {
            responseEntity = new ResponseEntity<JSONObject>(multipartService.fileUpload(request), HttpStatus.OK);
            
        } catch (FileUploadException ex) {
            responseEntity = responseExcetion("FileUploadException", ex.getMessage());
        } catch (IOException ex) {
            responseEntity = responseExcetion("IOException", ex.getMessage());
        }

        return responseEntity;
    }


    @RequestMapping(value = "/generate/input", method = RequestMethod.PUT)
    public ResponseEntity<JSONObject> apiGenerateByInput(@RequestBody GenerateVo generateVo) {
        ResponseEntity<JSONObject> responseEntity = null;

        try {
            generateSchemaService.generateDatabase(generateVo);
            generateSchemaService.generateByInput(generateVo);

            responseEntity = new ResponseEntity<JSONObject>(new JSONObject(), HttpStatus.OK);

        } catch (ParseException ex) {
            responseEntity = responseExcetion("ParseException", ex.getMessage());
        } catch (IOException ex) {
            responseEntity = responseExcetion("IOException", ex.getMessage());
        }

        return responseEntity;
    }


    @RequestMapping(value = "/generate/columns", method = RequestMethod.PUT)
    public ResponseEntity<JSONObject> apiGenerateByColumns(@RequestBody GenerateVo generateVo) {
        ResponseEntity<JSONObject> responseEntity = null;

        try {
            generateSchemaService.generateDatabase(generateVo);
            generateSchemaService.generateByColumns(generateVo);

            responseEntity = new ResponseEntity<JSONObject>(new JSONObject(), HttpStatus.OK);

        } catch (ParseException ex) {
            responseEntity = responseExcetion("ParseException", ex.getMessage());
        } catch (IOException ex) {
            responseEntity = responseExcetion("IOException", ex.getMessage());
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
