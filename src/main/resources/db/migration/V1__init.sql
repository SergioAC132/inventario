CREATE TABLE marcas (
    id_marca BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(70) NOT NULL
);

CREATE TABLE productos (
    ID_PRODUCTO BIGINT PRIMARY KEY AUTO_INCREMENT,
    codigo VARCHAR(10) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    id_marca BIGINT NOT NULL, 
    CONSTRAINT uq_codigo_marca UNIQUE (codigo, id_marca),
        FOREIGN KEY (id_marca) REFERENCES marcas(id_marca)
);

CREATE TABLE proveedores (
    id_proveedor BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    rfc VARCHAR(13) NOT NULL,
    tipo_persona TINYINT NOT NULL,
    telefono VARCHAR(15) NOT NULL,
    correo VARCHAR(100),
    codigo_postal INTEGER
);

CREATE TABLE inventario (
    id_inventario BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_producto BIGINT NOT NULL,
    lote VARCHAR(50) NOT NULL,
    fecha_caducidad DATE,
    cantidad_disponible INT NOT NULL DEFAULT 0,
    CONSTRAINT uq_producto_lote UNIQUE (id_producto, lote),
    CONSTRAINT fk_inventario_producto
        FOREIGN KEY (id_producto) REFERENCES productos(id_producto)
);

CREATE TABLE compras (
    id_compra BIGINT PRIMARY KEY AUTO_INCREMENT,
    fecha DATETIME NOT NULL,
    id_proveedor BIGINT NOT NULL,
    numero_factura VARCHAR(50) NOT NULL,
    estatus ENUM('REGISTRADA', 'FACTURADA', 'CANCELADA') NOT NULL,
    CONSTRAINT fk_compra_proveedor
        FOREIGN KEY (id_proveedor) REFERENCES proveedores(id_proveedor),
    CONSTRAINT uq_factura_proveedor UNIQUE (id_proveedor, numero_factura)
);

CREATE TABLE compra_productos (
    id_compra_producto BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_compra BIGINT NOT NULL,
    id_inventario BIGINT NOT NULL,
    cantidad INT NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_compra_producto_compra
        FOREIGN KEY (id_compra) REFERENCES compras(id_compra),
    CONSTRAINT fk_compra_producto_inventario
        FOREIGN KEY (id_inventario) REFERENCES inventario(id_inventario)
);

CREATE TABLE salidas (
    id_salida BIGINT PRIMARY KEY AUTO_INCREMENT,
    fecha DATETIME NOT NULL,
    tipo ENUM('CIRUGIA', 'ESTUDIO', 'VENTA'),
    destino ENUM('PARTICULAR', 'ASEGURADORA')
);

CREATE TABLE salida_productos (
    id_salida_producto BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_salida BIGINT NOT NULL,
    id_inventario BIGINT NOT NULL,
    cantidad INT NOT NULL,
    subtotal DECIMAL(10,2),
    CONSTRAINT fk_salida_producto_salida
        FOREIGN KEY (id_salida) REFERENCES salidas(id_salida),
    CONSTRAINT fk_salida_producto_inventario
        FOREIGN KEY (id_inventario) REFERENCES inventario(id_inventario)
);

CREATE TABLE movimientos_inventario (
    id_movimiento BIGINT PRIMARY KEY AUTO_INCREMENT,
    id_inventario BIGINT NOT NULL,
    tipo_movimiento VARCHAR(20) NOT NULL,
    cantidad INT NOT NULL,
    fecha DATETIME NOT NULL,
    referencia_tipo VARCHAR(20) NOT NULL,
    referencia_id BIGINT NOT NULL,
    CONSTRAINT fk_movimiento_inventario
        FOREIGN KEY (id_inventario) REFERENCES inventario(id_inventario)
);

CREATE TABLE roles (
    id_rol BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE usuarios (
    id_usuario BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE usuario_roles (
    id_usuario BIGINT NOT NULL,
    id_rol BIGINT NOT NULL,
    PRIMARY KEY (id_usuario, id_rol),
    CONSTRAINT fk_usuario_roles_usuario
        FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario),
    CONSTRAINT fk_usuario_roles_rol
        FOREIGN KEY (id_rol) REFERENCES roles(id_rol)
);