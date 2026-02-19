CREATE TABLE kardex (

    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,

    tipo_movimiento VARCHAR(30) NOT NULL,

    tabla_origen VARCHAR(30) NOT NULL,
    id_origen BIGINT NOT NULL,
    referencia_id BIGINT NULL,

    id_inventario BIGINT NOT NULL,

    cantidad_anterior INT NOT NULL,
    cantidad_nueva INT NOT NULL,
    diferencia INT NOT NULL,

    stock_resultante INT NOT NULL,

    usuario_id BIGINT NOT NULL

);

CREATE TRIGGER trg_compra_producto_insert
AFTER INSERT ON compra_productos
FOR EACH ROW
BEGIN

    DECLARE stock_actual INT;

    SELECT cantidad_disponible
    INTO stock_actual
    FROM inventarios
    WHERE id_inventario = NEW.id_inventario;

    INSERT INTO kardex (
        tipo_movimiento,
        tabla_origen,
        id_origen,
        referencia_id,
        id_inventario,
        cantidad_anterior,
        cantidad_nueva,
        diferencia,
        stock_resultante,
        usuario_id
    )
    VALUES (
        'COMPRA',
        'compra_producto',
        NEW.id_compra_producto,
        NEW.id_compra,
        NEW.id_inventario,
        0,
        NEW.cantidad,
        NEW.cantidad,
        stock_actual,
        IFNULL(@usuario_id, 0)
    );

END;
