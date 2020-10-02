package com.orio.backend.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileUploadService{
    Logger logger = LoggerFactory.getLogger(FileUploadService.class);

    @Autowired
    Environment env;

    public void save(MultipartFile mfile) throws Exception{

        //saved file dir
        String dir = env.getProperty("file.save.dir");

        OutputStream os = null;
        try {

            // save to file
            File file = new File(dir + mfile.getOriginalFilename());
            os = new FileOutputStream(file);
            os.write(mfile.getBytes());

        } catch (Exception e) {
            throw new RuntimeException("File save failed"+ e.getMessage());
        } finally {
            os.close();
        }
    }


}