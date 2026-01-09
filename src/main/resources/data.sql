INSERT INTO product(id ,name, description, short_description, price)
VALUES (1, '상품1', '상품 설명1', '상품 짧은 설명1', 1000.00);

INSERT INTO category(id ,name)
VALUES (1, '카테고리1');

INSERT INTO product_category(id, product_id, category_id)
VALUES (1, 1, 1);

INSERT INTO cart_item(id, user_id, product_id, quantity)
VALUES (1, 1, 1, 1);

INSERT INTO orders(id, user_id, order_key, total_price, status)
VALUES (1, 1, 'random-order-key', 1000.00, 'CREATED');

INSERT INTO order_item(id, order_id, product_id, quantity, product_name, unit_price, total_price)
VALUES (1, 1, 1, 1, '상품1', 1000.00, 1000.00);

INSERT INTO payment(id, user_id, order_id, amount, status)
VALUES (1, 1, 1, 1000.00, 'READY');