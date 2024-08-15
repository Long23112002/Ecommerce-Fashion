package org.example.ecommercefashion.services.impl;

import com.longnh.exceptions.ExceptionHandle;
import lombok.RequiredArgsConstructor;
import org.example.ecommercefashion.dtos.request.ImageRequest;
import org.example.ecommercefashion.dtos.response.ImageResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.entities.Image;
import org.example.ecommercefashion.enums.StatusImage;
import org.example.ecommercefashion.enums.TypeImage;
import org.example.ecommercefashion.exceptions.ErrorMessage;
import org.example.ecommercefashion.repositories.ImageRepository;
import org.example.ecommercefashion.services.ImageService;
import org.example.ecommercefashion.services.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {
    private final StorageService storageService;

    private final ImageRepository imageRepository;


    @Override
    public Image getImage(Long idImage) {
        return imageRepository.findById(idImage)
                .orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.IMAGE_NOT_FOUND.val()));
    }

    @Override
    public List<ImageResponse> uploadImages(ImageRequest request) {
        return request.getFiles()
                .stream()
                .map(this::processFile)
                .toList();
    }

    private ImageResponse processFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            Image image = imageRepository.save(mapMultipartFileToImage(file));
            storageService.putObject(inputStream, file, image.getUrl());

            return mapEntityToResponse(image);

        } catch (IOException e) {
            return ImageResponse.builder()
                    .status(StatusImage.FAILED)
                    .fileName(file.getOriginalFilename())
                    .build();
        }
    }

    @Override
    public ImageResponse getImageById(Long idImage) {
        Image image = getImage(idImage);
        return mapEntityToResponse(image);
    }

    @Override
    public ImageResponse getImageBydUrl(String url) {
        Image image = imageRepository.findByUrl(url).orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.IMAGE_NOT_FOUND.val()));
        return mapEntityToResponse(image);
    }

    @Override
    public MessageResponse deleteImage(Long idImage) {
        Image image = getImage(idImage);
        image.setDeleted(true);
        image.setDeletedAt(new Timestamp(System.currentTimeMillis()));
        imageRepository.save(image);
        return MessageResponse.builder()
                .message("Delete image with id " + idImage + " successfully")
                .build();
    }

    @Override
    public MessageResponse deleteImageByUrl(String url) {
        Image image = imageRepository.findByUrl(url).orElseThrow(() -> new ExceptionHandle(HttpStatus.NOT_FOUND, ErrorMessage.IMAGE_NOT_FOUND.val()));
        image.setDeleted(true);
        image.setDeletedAt(new Timestamp(System.currentTimeMillis()));
        imageRepository.save(image);

        return MessageResponse.builder()
                .message("Delete image with url " + url + " successfully")
                .build();
    }


    private Image mapMultipartFileToImage(MultipartFile file) {
        return Image.builder()
                .size(file.getSize())
                .title(file.getOriginalFilename())
                .type(TypeImage.fromMimeType(file.getContentType()))
                .url(generateObjectName(file))
                .build();
    }

    private String generateObjectName(MultipartFile file) {
        return LocalDateTime.now() + "_" + file.getOriginalFilename();
    }


    private ImageResponse mapEntityToResponse(Image image) {
        String urlStorage = storageService.getObjectUrl(image.getUrl());

        return ImageResponse.builder()
                .contentType(image.getType())
                .size(image.getSize())
                .fileName(image.getTitle())
                .createdAt(image.getCreatedAt())
                .status(StatusImage.SUCCESS)
                .imageUrl(urlStorage)
                .idImage(image.getId())
                .build();
    }
}
