ALTER TABLE inventarios
ADD COLUMN costo_unitario DECIMAL(12, 2) NOT NULL AFTER cantidad_disponible;