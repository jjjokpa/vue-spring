package com.orio.backend.controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioFormat.Encoding;

import com.orio.backend.dto.ResultDto;
import com.orio.backend.dto.TimeDto;
import com.orio.backend.service.FileDownloadService;
import com.orio.backend.service.FileUploadService;
import com.orio.backend.service.InsertDateService;
import com.orio.backend.util.DateCheck;
import com.orio.backend.util.MediaTypeUtils;

import org.apache.commons.io.FileUtils;
import org.apache.poi.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @Autowired
    private FileDownloadService fileDownloadService;

    @Autowired
    private ServletContext servletContext;


    @GetMapping("/")
    public String home() {
        return "Hello from Spring boot";
    }

    // return saved file list
    @GetMapping("/getFileList")
    public Map<String, Object> getFileList(){
        Map<String, Object> json = new HashMap();

        // get file list
        List<String> fileList = fileDownloadService.getFileList();

        json.put("file_list", fileList);

        return json;
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

    // save file
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
    
    // download file
    @GetMapping(value = "/downloadFile", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void download(@RequestParam String fileName, HttpServletResponse response) throws Exception {
    
        //saved file dir
        String dir = env.getProperty("file.save.dir");

        System.out.println("fileName: " + fileName);

        File file = new File(dir + fileName);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=customers.xlsx");
        ByteArrayInputStream stream = new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
        IOUtils.copy(stream, response.getOutputStream());
    }
 
    
}