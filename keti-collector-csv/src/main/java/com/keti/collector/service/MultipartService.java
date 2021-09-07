package com.keti.collector.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class MultipartService {

    @Value("${spring.multipart.location}")
    private String location;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    
    public Map<String, String> fileUpload(HttpServletRequest request) throws FileUploadException, IOException {
        ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator iter = upload.getItemIterator(request);
            
        Map<String, String> save = new HashMap<>();

        while (iter.hasNext()) {
            FileItemStream item = iter.next();
            String name = item.getFieldName();

            switch (name) {
                case "files":
                    if (!item.isFormField()) {
                        InputStream filesInputStream = item.openStream();
                        String path = location + item.getName();
                        File files = new File(path);
                        OutputStream out = new FileOutputStream(files);
                        IOUtils.copy(filesInputStream, out);

                        save.put("files", path);

                        out.close();
                        filesInputStream.close(); 
                    }

                    break;
                        
                case "params":
                    InputStream paramsInputStream = item.openStream();
                    StringWriter writer = new StringWriter();
                    String rules = IOUtils.toString(paramsInputStream, "UTF-8");

                    save.put("rules", rules);
                    
                    paramsInputStream.close();
                            
                    break;
                
                default:
                    break;
            }
        }
            
        return save;
    }

}