INSERT INTO product(id ,name, description, short_description, price)VALUES (1, '상품1', '상품 설명1', '상품 짧은 설명1', 1000.00);

INSERT INTO category(id ,name) VALUES (1, '카테고리1');

INSERT INTO product_category(id, product_id, category_id) VALUES (1, 1, 1);