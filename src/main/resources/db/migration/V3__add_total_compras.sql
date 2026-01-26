ALTER TABLE compra_productos
ADD COLUMN total DECIMAL(12,2) NOT NULL
AFTER estatus;
