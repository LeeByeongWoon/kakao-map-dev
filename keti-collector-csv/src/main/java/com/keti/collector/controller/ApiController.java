package com.keti.collector.controller;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api")
public class ApiController {

    JSONParser parser = new JSONParser();
    

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public JSONObject test() {
        JSONObject jsonObject = null;
        try {
            String str = "{\"data\":\"test\"}";
            jsonObject = (JSONObject) parser.parse(str);
        } catch (Exception e) {
            //TODO: handle exception
        }

        return jsonObject;
    }
    
}
