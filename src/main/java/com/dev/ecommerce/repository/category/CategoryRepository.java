package com.dev.ecommerce.repository.category;

import com.dev.ecommerce.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
