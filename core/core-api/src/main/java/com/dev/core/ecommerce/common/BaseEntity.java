package com.dev.core.ecommerce.common;

import com.dev.core.enums.EntityState;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private EntityState state = EntityState.ACTIVE;

    public void active() {
        state = EntityState.ACTIVE;
    }

    public void delete() {
        state = EntityState.DELETED;
    }
}
