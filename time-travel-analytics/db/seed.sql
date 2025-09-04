USE time_travel;

INSERT INTO users (username, email, role, address) VALUES
('alice', 'alice@example.com', 'buyer', '10 Downing St'),
('bob', 'bob@example.com', 'seller', '42 Galaxy Rd');

INSERT INTO inventory (sku, name, qty, price) VALUES
('SKU-001','Widget A', 100, 9.99),
('SKU-002','Widget B', 50, 19.99);

INSERT INTO orders (user_id, inventory_id, quantity, total, status) VALUES
(1,1,2,19.98,'placed');

-- simulate updates to create versions
UPDATE inventory SET qty = 90 WHERE id = 1;
UPDATE inventory SET price = 11.99 WHERE id = 1;
UPDATE users SET address = '11 Downing St' WHERE id = 1;

-- more changes
INSERT INTO orders (user_id, inventory_id, quantity, total, status) VALUES
(2,2,1,19.99,'placed');
UPDATE orders SET status='shipped' WHERE id=2;
