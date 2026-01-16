package com.dev.core.ecommerce.domain.review;

import com.dev.core.ecommerce.support.BaseEntity;
import com.dev.core.enums.review.ReviewTargetType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "review",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_id_review_key", columnNames = {"user_id", "review_key"})
        }
)
public class Review extends BaseEntity {
    private Long userId;
    private String reviewKey;
    @Enumerated(EnumType.STRING)
    private ReviewTargetType targetType;
    private Long targetId;
    private BigDecimal rate;
    private String content;

    public static Review create(
            Long userId,
            String reviewKey,
            ReviewTargetType targetType,
            Long targetId,
            BigDecimal rate,
            String content
    ) {
        Review review = new Review();
        review.userId = userId;
        review.reviewKey = reviewKey;
        review.targetType = targetType;
        review.targetId = targetId;
        review.rate = rate;
        review.content = content;
        return review;
    }
}
