package com.dev.core.ecommerce.domain.review;

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
        name = "review_image",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_review_id_image_id", columnNames = {"review_id", "image_id"})
        }
)
public class ReviewImage extends BaseEntity {
    private Long userId;
    private Long reviewId;
    private Long imageId;
    private String imageUrl;
}
