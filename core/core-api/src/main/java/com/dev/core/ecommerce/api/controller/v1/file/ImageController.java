package com.dev.core.ecommerce.api.controller.v1.file;

import com.dev.core.ecommerce.api.controller.v1.response.ApiResponse;
import com.dev.core.ecommerce.support.auth.User;
import com.dev.core.ecommerce.support.file.ImageUploadResult;
import com.dev.core.ecommerce.support.file.ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageUploader imageUploader;

    @PostMapping("/v1/images/upload")
    public ApiResponse<ImageUploadResult> uploadImage(
            User user,
            @RequestParam MultipartFile file
    ) {
        return ApiResponse.success(imageUploader.uploadImage(user, file));
    }
}
