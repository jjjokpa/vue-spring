package com.orio.backend.service;

import java.io.File;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileDownloadService {
    Logger logger = LoggerFactory.getLogger(FileDownloadService.class);

    @Autowired
    Environment env;

    public ArrayList<String> getFileList(){

        ArrayList<String> fileList = new ArrayList<String>();
        
        // save file dir
        String dir = env.getProperty("file.save.dir");

        // directory        
        File f = new File(dir);
        logger.debug(""+f.exists());
        
        
        // get file list
        String[] pathnames;
        pathnames = f.list();
        
        // put to return List
		for (String path : pathnames) {
			fileList.add(path);
        }

        return fileList;
    }
}