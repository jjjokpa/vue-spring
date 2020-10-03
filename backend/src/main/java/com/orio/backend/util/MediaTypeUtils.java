package com.orio.backend.util;

import java.nio.charset.StandardCharsets;

import javax.servlet.ServletContext;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
public class MediaTypeUtils {
    public static MediaType getMediaTypeForFileName(ServletContext servletContext, String fileName){
        String mineType = servletContext.getMimeType(fileName);
        try {
            // MediaType mediaType = MediaType.parseMediaType(mineType);
            MediaType mediaType = new MediaType("application", "plain");
            return mediaType;
        } catch (Exception e) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}