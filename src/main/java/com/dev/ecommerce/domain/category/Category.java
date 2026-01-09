package com.dev.ecommerce.domain.category;

import com.dev.ecommerce.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category")
public class Category extends BaseEntity {
    private String name;

    public static Category create(String name){
        Category category = new Category();
        category.name = name;
        return category;
    }
}
