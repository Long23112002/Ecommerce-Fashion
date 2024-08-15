package org.example.ecommercefashion.services.impl;


import com.longnh.exceptions.ExceptionHandle;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.services.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;


    @Override
    public void putObject(InputStream inputStream, MultipartFile file, String objectName) {
        bucketExist();
        try {
            minioClient.putObject(
                    PutObjectArgs
                            .builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new ExceptionHandle(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.SOMETHING_WENT_WRONG.val() + "2");
        }
    }


    private void makeBucket() {
        try {
            minioClient
                    .makeBucket(MakeBucketArgs
                            .builder()
                            .bucket(bucketName)
                            .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new ExceptionHandle(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.SOMETHING_WENT_WRONG.val() + "1");
        }
    }


    @Override
    public boolean deleteObject(String objectName) {
        bucketExist();
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
            return true;

        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new ExceptionHandle(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.SOMETHING_WENT_WRONG.val() + "3");
        }
    }

    @Override
    public InputStream getObject(String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new ExceptionHandle(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.SOMETHING_WENT_WRONG.val() + "4");
        }
    }

    @Override
    public String getObjectUrl(String objectName) {
        bucketExist();
        String url = "";
        try {


            url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(30, TimeUnit.SECONDS)
                            .build()
            );
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | XmlParserException |
                 ServerException e) {
            throw new ExceptionHandle(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.SOMETHING_WENT_WRONG.val() + "5");
        }

        return url;
    }


    private void bucketExist() {
        boolean isExisted;
        try {
            isExisted = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new ExceptionHandle(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessage.SOMETHING_WENT_WRONG.val() + "6");
        }
        if (!isExisted) {
            throw new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.NOT_FOUND_BUCKET.val());
        }
    }
}
