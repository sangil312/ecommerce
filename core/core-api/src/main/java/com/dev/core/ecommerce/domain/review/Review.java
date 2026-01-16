package com.dev.core.ecommerce.domain.review;

import com.dev.core.ecommerce.common.BaseEntity;
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
        name = "reviews",
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
}
