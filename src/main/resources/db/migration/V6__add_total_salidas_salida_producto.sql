ALTER TABLE salida_productos
MODIFY COLUMN subtotal DECIMAL(12,2) NULL;

ALTER TABLE salida_productos
ADD COLUMN costo_total DECIMAL(12,2) NOT NULL
AFTER subtotal;

ALTER TABLE salidas
ADD COLUMN total DECIMAL(12,2) NOT NULL
AFTER destino;
