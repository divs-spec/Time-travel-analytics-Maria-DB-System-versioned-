USE time_travel;

-- 1) What did inventory look like at a point in time?
SELECT * FROM inventory FOR SYSTEM_TIME AS OF TIMESTAMP '2025-09-01 12:00:00';

-- 2) Who changed the price and when? (We can check versions by ordering history)
SELECT id, sku, price, row_start, row_end
FROM inventory FOR SYSTEM_TIME BETWEEN TIMESTAMP '2000-01-01' AND TIMESTAMP '2030-01-01'
WHERE id = 1
ORDER BY row_start;

-- 3) Diff between two times (simple approach: get snapshots and LEFT JOIN)
-- snapshot A
CREATE TEMPORARY TABLE snap_a AS
SELECT id AS id_a, sku AS sku_a, qty AS qty_a, price AS price_a
FROM inventory FOR SYSTEM_TIME AS OF TIMESTAMP '2025-09-01 12:00:00';

-- snapshot B
CREATE TEMPORARY TABLE snap_b AS
SELECT id AS id_b, sku AS sku_b, qty AS qty_b, price AS price_b
FROM inventory FOR SYSTEM_TIME AS OF TIMESTAMP '2025-09-15 12:00:00';

SELECT COALESCE(b.id_b, a.id_a) AS id,
       a.qty_a, b.qty_b, a.price_a, b.price_b
FROM snap_a a
FULL OUTER JOIN snap_b b ON a.id_a = b.id_b;

-- 4) Audit who changed a row and when
-- Because MariaDB doesn't automatically store the DB user who changed rows, you can implement triggers to log user and app metadata to an audit table.

CREATE TABLE inventory_audit (
  audit_id INT AUTO_INCREMENT PRIMARY KEY,
  inventory_id INT,
  operation VARCHAR(10),
  changed_by VARCHAR(200),
  changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  old_price DECIMAL(10,2),
  new_price DECIMAL(10,2)
);

-- trigger example (the application should set @current_user context variable before running writes so trigger can capture it)
DELIMITER $$
CREATE TRIGGER trg_inventory_price_update
BEFORE UPDATE ON inventory
FOR EACH ROW
BEGIN
  IF OLD.price <> NEW.price THEN
    INSERT INTO inventory_audit(inventory_id, operation, changed_by, old_price, new_price)
    VALUES (OLD.id, 'UPDATE', COALESCE(@current_user, 'unknown'), OLD.price, NEW.price);
  END IF;
END$$
DELIMITER ;

-- After this, application sets: SET @current_user = CONCAT(:app_user, ' | ', USER());
