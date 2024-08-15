package org.example.ecommercefashion.services;

import org.example.ecommercefashion.dtos.request.ImageRequest;
import org.example.ecommercefashion.dtos.response.ImageResponse;
import org.example.ecommercefashion.dtos.response.MessageResponse;
import org.example.ecommercefashion.entities.Image;

import java.util.List;

public interface ImageService {
    // createListImage
    List<ImageResponse> uploadImages(ImageRequest files);

    // getImage
    ImageResponse getImageById(Long idImage);

    // getImageBydUrl
    ImageResponse getImageBydUrl(String url);

    // deleteImage
    MessageResponse deleteImage(Long idImage);

    //deleteImageByUrl
    MessageResponse deleteImageByUrl(String url);

    Image getImage(Long idImage);
}
