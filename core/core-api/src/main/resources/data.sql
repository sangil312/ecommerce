INSERT INTO product(id ,name, thumbnail_url, description, short_description, price)
VALUES (1, '상품1', 'https://~', '상품 설명1', '상품 짧은 설명1', 1000);

INSERT INTO category(id ,name)
VALUES (1, '카테고리1');

INSERT INTO product_category(id, product_id, category_id)
VALUES (1, 1, 1);

INSERT INTO cart_item(id, user_id, product_id, quantity)
VALUES (1, 1, 1, 1);

INSERT INTO orders(id, user_id, order_key, total_price, status)
VALUES
    (1, 1, 'random-order-key1', 1000, 'CREATED'),
    (2, 1, 'random-order-key2', 1000, 'PAID');

INSERT INTO order_item(id, order_id, product_id, quantity, product_name, thumbnail_url, unit_price, total_price)
VALUES
    (1, 1, 1, 1, '상품1', 'https://test.com/image_1', 1000, 1000),
    (2, 2, 1, 1, '상품1', 'https://test.com/image_2', 1000, 1000);

INSERT INTO payment(id, user_id, order_id, amount, status)
VALUES (1, 1, 1, 1000, 'READY');

INSERT INTO review(id, user_id, review_key, target_type, target_id, rate, content)
VALUES
    (1, 1, 'ORDER_ITEM_1', 'PRODUCT', 1L, 5.0, '좋아요'),
    (2, 1, 'ORDER_ITEM_2', 'PRODUCT', 1L, 4.0, '보통입니다');

INSERT INTO review_image(id, user_id, review_id, image_id, image_url)
VALUES
    (1, 1, 1, 101, 'https://example.com/reviews/1.jpg'),
    (2, 1, 2, 102, 'https://example.com/reviews/2.jpg');
