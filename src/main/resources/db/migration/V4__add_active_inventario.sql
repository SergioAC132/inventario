ALTER TABLE `inventario`.`inventario` 
RENAME TO  `inventario`.`inventarios`;

ALTER TABLE inventarios
ADD COLUMN active TINYINT(1) NOT NULL DEFAULT 1;
