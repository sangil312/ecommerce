package com.dev.ecommerce.domain.product;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


class ProductTest {

    @Test
    void create() {
        Product product = Product.create(
                "상품1",
                "https://~",
                "설명",
                "짧은 설명",
                BigDecimal.valueOf(1_000L)
        );
        assertThat(product.getName()).isEqualTo("상품1");
        assertThat(product.getThumbnailUrl()).isEqualTo("https://~");
        assertThat(product.getDescription()).isEqualTo("설명");
        assertThat(product.getShortDescription()).isEqualTo("짧은 설명");
        assertThat(product.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(1_000L));
    }
}