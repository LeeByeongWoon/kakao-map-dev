package com.keti.collector.service;

import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class MultipartService {

    @Value("${spring.multipart.location}")
    private String location;

    
    public JSONObject fileUpload(HttpServletRequest request) throws FileUploadException, IOException {
        Map<String, String> fileMap = new HashMap<>();

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            fileMap.put("type", "isMultipart");
            fileMap.put("message", "request is not multipart");

            return new JSONObject(fileMap); 
        }

        ServletFileUpload upload = new ServletFileUpload();
        FileItemIterator iter = upload.getItemIterator(request);

        UUID uuid = UUID.randomUUID();
        String uuidFileName = uuid.toString() + ".csv";

        while (iter.hasNext()) {
            FileItemStream item = iter.next();
            String name = item.getFieldName();

            if (!item.isFormField()) {
                InputStream is = item.openStream();
                String fileName = item.getName();
                String filePath = location + uuidFileName;

                File files = new File(filePath);
                OutputStream os = new FileOutputStream(files);
                IOUtils.copy(is, os);

                fileMap.put("file_name", fileName);
                fileMap.put("uuid_file_name", uuidFileName);

                os.close();
                is.close(); 
            }
        }
            
        return new JSONObject(fileMap);
    }

}