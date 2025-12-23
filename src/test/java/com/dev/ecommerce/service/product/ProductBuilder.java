package com.dev.ecommerce.service.product;

import com.dev.ecommerce.domain.product.Product;

import java.math.BigDecimal;

public class ProductBuilder {
    private String name = "상품1";
    private String thumbnailUrl = "https://~";
    private String description = "상품 본문 설명1";
    private String shortDescription = "상품 설명1";
    private BigDecimal price = BigDecimal.valueOf(1_000);

    public ProductBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder thumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
        return this;
    }

    public ProductBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ProductBuilder shortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
        return this;
    }

    public ProductBuilder price(BigDecimal price) {
        this.price = price;
        return this;
    }

    public Product build() {
        return Product.create(name, thumbnailUrl, description, shortDescription, price);
    }
}
