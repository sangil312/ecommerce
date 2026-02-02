package com.dev.core.ecommerce.repository.file;

import com.dev.core.ecommerce.support.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "image",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_id_image_url", columnNames = {"user_id", "image_url"})
        }
)
public class ImageEntity extends BaseEntity {
    private Long userId;
    private String imageUrl;
    private String originalFileName;

    public static ImageEntity create(Long userId, String imageUrl, String originalFileName) {
        ImageEntity imageEntity = new ImageEntity();
        imageEntity.userId = userId;
        imageEntity.imageUrl = imageUrl;
        imageEntity.originalFileName = originalFileName;
        return imageEntity;
    }
}
