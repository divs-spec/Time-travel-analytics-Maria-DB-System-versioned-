USE time_travel;

CREATE TABLE IF NOT EXISTS inventory_audit (
  audit_id INT AUTO_INCREMENT PRIMARY KEY,
  inventory_id INT,
  operation VARCHAR(10),
  changed_by VARCHAR(200),
  changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  old_price DECIMAL(10,2),
  new_price DECIMAL(10,2)
);

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

