package org.example.ecommercefashion.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface StorageService {
    // putObject
    void putObject(InputStream inputStream, MultipartFile file, String objectName);

    // deleteObject
    boolean deleteObject(String objectName);


    // getObject
    InputStream getObject(String objectName);

    // getObjectUrl
    String getObjectUrl(String objectName);
}
