package com.dev.core.ecommerce.service.product;

import com.dev.core.ecommerce.common.response.Page;
import com.dev.core.ecommerce.domain.product.Product;
import com.dev.core.ecommerce.domain.product.ProductCategory;
import com.dev.core.ecommerce.repository.product.ProductCategoryRepository;
import com.dev.core.ecommerce.repository.product.ProductRepository;
import com.dev.core.enums.EntityState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductReaderUnitTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @InjectMocks
    private ProductReader productReader;

    @Test
    @DisplayName("상품 조회: 카테고리 ID로 상품 조회")
    void findProductsByCategory() {
        // given
        Long categoryId = 10L;
        PageRequest pageable = PageRequest.of(0, 2);
        ProductCategory productCategory1 = ProductCategory.create(1L, categoryId);
        ProductCategory productCategory2 = ProductCategory.create(2L, categoryId);
        Slice<ProductCategory> slice = new SliceImpl<>(
                List.of(productCategory1, productCategory2),
                pageable,
                true
        );

        List<Long> productIds = List.of(1L, 2L);
        Product product1 = Product.create("상품1", "https://~", "설명1", "요약1", BigDecimal.valueOf(1_000L));
        Product product2 = Product.create("상품2", "https://~", "설명2", "요약2", BigDecimal.valueOf(2_000L));
        List<Product> products = List.of(product1, product2);

        when(productCategoryRepository.findByCategoryIdAndState(categoryId, EntityState.ACTIVE, pageable))
                .thenReturn(slice);
        when(productRepository.findAllById(productIds)).thenReturn(products);

        // when
        Page<Product> result = productReader.findProductsByCategory(categoryId, pageable);

        // then
        assertThat(result.contents()).containsExactly(product1, product2);
        assertThat(result.hasNext()).isTrue();
        verify(productCategoryRepository).findByCategoryIdAndState(categoryId, EntityState.ACTIVE, pageable);
        verify(productRepository).findAllById(productIds);
    }
}
