package com.keti.collector.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.FileUploadException;

import com.opencsv.CSVReader;
import com.keti.collector.repository.CsvRepository;
import com.keti.collector.service.CsvReaderService;
import com.keti.collector.service.MultipartService;


@RestController
@RequestMapping(value = "/api")
public class ApiController {

    private final MultipartService multipartService;
    private final CsvReaderService csvReaderService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ApiController(MultipartService multipartService, CsvReaderService csvReaderService) {
        this.multipartService = multipartService;
        this.csvReaderService = csvReaderService;
    }


    @RequestMapping(value = "/files", method = RequestMethod.POST)
    public ResponseEntity<String> apiFilesUpload(HttpServletRequest request) {
        try {
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            if (!isMultipart) { return new ResponseEntity<>("isMultipart: " + isMultipart, HttpStatus.OK); }

            Map formData = multipartService.fileUpload(request);

            logger.info("formData: " + formData);

            ResponseEntity<String> responseEntity = new ResponseEntity<>("s", HttpStatus.OK);

            return responseEntity;
        } catch (FileUploadException ex) {
            return new ResponseEntity<String>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IOException ex) {
            return new ResponseEntity<String>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/reader", method = RequestMethod.GET)
    public ResponseEntity<String> apiFilesReader() {
        try {
            // File file = new File("/Users/gangbinpark/gorotis11/ketiClust/keti-collector-csv/KWeather_Outdoor_202012.csv");
            File file = new File("/Users/gangbinpark/gorotis11/ketiClust/keti-collector-csv/KETI_3M.csv");
            csvReaderService.fileUtilsReader(file);

            // FileReader fileReader = new FileReader("/Users/gangbinpark/gorotis11/ketiClust/keti-collector-csv/KETI_3M.csv");
            // csvReaderService.openCsvReader(fileReader);

            return new ResponseEntity<String>("s", HttpStatus.OK);
        } catch (Exception ex) {
            logger.info("Exception: " + ex.getMessage());
            return new ResponseEntity<String>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
}
