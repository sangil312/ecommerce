package com.dev.core.ecommerce.repository.category;

import com.dev.core.ecommerce.domain.category.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
