package com.dev.core.ecommerce.support.file;

import com.dev.core.ecommerce.repository.file.ImageEntity;
import com.dev.core.ecommerce.repository.file.ImageRepository;
import com.dev.core.ecommerce.support.auth.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ImageUploader {
    private final ImageRepository imageRepository;

    public ImageUploadResult uploadImage(User user, MultipartFile file) {
        var mockS3Url = uploadMockS3(file);

        ImageEntity imageEntity = ImageEntity.create(user.id(), file.getOriginalFilename(), mockS3Url);
        imageRepository.save(imageEntity);

        return new ImageUploadResult(imageEntity.getId(), mockS3Url);
    }

    /**
     * S3 버킷에 업로드 했다고 가정
     */
    private String uploadMockS3(MultipartFile file) {
        String imageId = UUID.randomUUID().toString();
        String fileName = StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename() : "unknown";
        return "https://mock-s3-buket.amazonaws.com/" + imageId + "_" + fileName;
    }
}
