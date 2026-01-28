package com.dev.core.ecommerce.domain.category;

import com.dev.core.ecommerce.support.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "category",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_name", columnNames = "name")
        }
)
public class Category extends BaseEntity {
    private String name;

    public static Category create(String name){
        Category category = new Category();
        category.name = name;
        return category;
    }
}
