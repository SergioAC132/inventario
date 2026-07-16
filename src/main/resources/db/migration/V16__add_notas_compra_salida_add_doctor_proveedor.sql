CREATE TABLE doctores (
    id_doctor BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(15)
);

CREATE TABLE notas (
    id_nota BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_compra BIGINT,
    id_salida BIGINT,
    fecha DATETIME NOT NULL,
    texto VARCHAR(255) NOT NULL,
    id_usuario BIGINT NOT NULL,
    CONSTRAINT fk_nota_compra_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_nota_compra_compra
        FOREIGN KEY (id_compra) REFERENCES compras(id_compra),
    CONSTRAINT fk_nota_salida
        FOREIGN KEY (id_salida) REFERENCES salidas(id_salida)
);

ALTER TABLE compras 
DROP FOREIGN KEY `fk_compra_proveedor`;
ALTER TABLE compras 
CHANGE COLUMN `id_proveedor` `id_proveedor` BIGINT NULL ;
ALTER TABLE `inventario`.`compras` 
ADD CONSTRAINT `fk_compra_proveedor`
  FOREIGN KEY (`id_proveedor`)
  REFERENCES `inventario`.`proveedores` (`id_proveedor`);

ALTER TABLE compras
ADD COLUMN id_doctor BIGINT AFTER id_proveedor;

ALTER TABLE compras
ADD CONSTRAINT fk_compra_doctor
    FOREIGN KEY (id_doctor) REFERENCES doctores(id_doctor);