package com.orio.backend.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.orio.backend.dto.ResultDto;
import com.orio.backend.dto.TimeDto;
import com.orio.backend.service.InsertDateService;
import com.orio.backend.util.DateCheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@CrossOrigin
@Slf4j
public class HomeController {
    Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    private DateCheck dateCheck;

    @Autowired
    private InsertDateService insertDateService;

    @GetMapping("/")
    public String home() {
        return "Hello from Spring boot";
    }

    @PostMapping(path = "/insertTime", produces=MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> insertTime(@RequestBody TimeDto timeDto) {
        
        //logger.debug(timeDto.toString());
        Map<String, Object> json = new HashMap();

        // check date
        boolean result = dateCheck.checkDate(timeDto.getDay());

        try {
            // check file
            new File("C:/myfile/" + timeDto.getFile());
            
        } catch (Exception e) {

            // file not exist
            result = false;
        }

        ResultDto resultDto = null;

        if(result) {
            resultDto = insertDateService.insertDate("C:/myfile/" + timeDto.getFile(), timeDto);

            // if success set response
            if (resultDto!=null){
                json.put("work_time", resultDto.getWork_time());
                json.put("deduct_time", resultDto.getDeduct_time());
                json.put("evaluate_time", resultDto.getEvaluate_time());
            }
        }
        
        return json;
    }
    

    
}