package com.dev.core.ecommerce.service.product;

import com.dev.core.ecommerce.IntegrationTestSupport;
import com.dev.core.ecommerce.common.response.Page;
import com.dev.core.ecommerce.domain.category.Category;
import com.dev.core.ecommerce.domain.product.Product;
import com.dev.core.ecommerce.domain.product.ProductCategory;
import com.dev.core.ecommerce.repository.category.CategoryRepository;
import com.dev.core.ecommerce.repository.product.ProductCategoryRepository;
import com.dev.core.ecommerce.repository.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class ProductServiceTest extends IntegrationTestSupport {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    Product saveProduct;
    Category saveCategory;

    @BeforeEach
    void setUp() {
        saveProduct = productRepository.save(
                Product.create(
                        "상품1",
                        "https://~",
                        "상품 본문 설명1",
                        "상품설명1",
                        BigDecimal.valueOf(1_000L)
                )
        );
        saveCategory = categoryRepository.save(Category.create("카테고리1"));
        productCategoryRepository.save(ProductCategory.create(saveProduct.getId(), saveCategory.getId()));
    }

    @Test
    @DisplayName("카테고리 ID 로 상품 조회")
    void findProductsByCategory() {
        Page<Product> products = productService.findProductsByCategory(
                saveCategory.getId(), PageRequest.of(0, 20));

        assertThat(products.contents()).hasSize(1);
        assertThat(products.hasNext()).isFalse();
    }

    @Test
    @DisplayName("상품 ID 로 상품 조회")
    void findProduct() {
        Product product = productService.findProduct(saveProduct.getId());

        assertThat(product).isNotNull();
        assertThat(product.getId()).isEqualTo(saveProduct.getId());
        assertThat(product.getName()).isEqualTo(saveProduct.getName());
        assertThat(product.getPrice()).isEqualTo(saveProduct.getPrice());
    }
}