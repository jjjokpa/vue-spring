package com.orio.backend.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.orio.backend.dto.ResultDto;
import com.orio.backend.dto.TimeDto;
import com.orio.backend.service.FileUploadService;
import com.orio.backend.service.InsertDateService;
import com.orio.backend.util.DateCheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@CrossOrigin
@Slf4j
public class HomeController {
    Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    Environment env;

    @Autowired
    private DateCheck dateCheck;

    @Autowired
    private InsertDateService insertDateService;

    @Autowired
    private FileUploadService fileUploadService;

    @GetMapping("/")
    public String home() {
        return "Hello from Spring boot";
    }

    @PostMapping(path = "/insertTime", produces=MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> insertTime(@RequestBody TimeDto timeDto) {
        
        //saved file dir
        String dir = env.getProperty("file.save.dir");

        //logger.debug(timeDto.toString());
        Map<String, Object> json = new HashMap();

        // check date
        boolean result = dateCheck.checkDate(timeDto.getDay());
        String message = "";

        try {
            // check file
            File file = new File(dir + timeDto.getFile());
            if(!file.exists()){
                message = "File not Found";
            }
            
        } catch (Exception e) {

            // file not exist
            result = false;
        }

        ResultDto resultDto = null;

        if(result) {
            resultDto = insertDateService.insertDate(dir + timeDto.getFile(), timeDto);

            // if success set response
            if (resultDto!=null){
                json.put("work_time", resultDto.getWork_time());
                json.put("deduct_time", resultDto.getDeduct_time());
                json.put("evaluate_time", resultDto.getEvaluate_time());
            }
        }
        
        json.put("insert_message", message);

        return json;
    }

    @PostMapping("/upload")
    public Map<String, Object> uploadFile(MultipartFile file){
        Map<String, Object> json = new HashMap();

        String message = "file upload success";
        try {

            // file upload service
            fileUploadService.save(file);
        } catch (Exception e) {
            logger.debug(e.getMessage());
            message = "file upload failed";
        }
        json.put("upload_message", message);

        return json;
    }
    

    
}