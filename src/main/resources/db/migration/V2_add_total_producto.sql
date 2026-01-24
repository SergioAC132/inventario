ALTER TABLE compra_productos
MODIFY COLUMN subtotal DECIMAL(12,2) NULL;

ALTER TABLE compra_productos
ADD COLUMN costo_total DECIMAL(12,2) NOT NULL
AFTER costo_unitario;
