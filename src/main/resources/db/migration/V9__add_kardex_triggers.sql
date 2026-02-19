CREATE TRIGGER trg_salida_producto_insert
AFTER INSERT ON salida_productos
FOR EACH ROW
BEGIN

    DECLARE stock_actual INT;
    DECLARE stock_anterior INT;

    SELECT cantidad_disponible
    INTO stock_actual
    FROM inventarios
    WHERE id_inventario = NEW.id_inventario;

    SET stock_anterior = stock_actual + NEW.cantidad;

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
        'SALIDA',
        'salida_producto',
        NEW.id_salida_producto,
        NEW.id_salida,
        NEW.id_inventario,
        stock_anterior,
        stock_actual,
        -NEW.cantidad,
        stock_actual,
        IFNULL(@usuario_id, 0)
    );
END

CREATE TRIGGER trg_salida_producto_update
AFTER UPDATE ON salida_productos
FOR EACH ROW
BEGIN

    DECLARE stock_actual INT;
    DECLARE stock_anterior INT;
    DECLARE diferencia_mov INT;

    -- Calcular diferencia real del movimiento
    SET diferencia_mov = OLD.cantidad - NEW.cantidad;
    -- Si aumenta cantidad de salida → diferencia negativa
    -- Si disminuye cantidad de salida → diferencia positiva

    -- Obtener stock actual (ya actualizado)
    SELECT cantidad_disponible
    INTO stock_actual
    FROM inventarios
    WHERE id_inventario = NEW.id_inventario;

    -- Stock antes del movimiento
    SET stock_anterior = stock_actual - diferencia_mov;

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
        'SALIDA_EDICION',
        'salida_producto',
        NEW.id_salida_producto,
        NEW.id_salida,
        NEW.id_inventario,
        stock_anterior,
        stock_actual,
        diferencia_mov,
        stock_actual,
        IFNULL(@usuario_id, 0)
    );

END;

CREATE TRIGGER trg_salida_producto_delete
AFTER DELETE ON salida_productos
FOR EACH ROW
BEGIN

    DECLARE stock_actual INT;
    DECLARE stock_anterior INT;

    -- Obtener stock después de devolver cantidad
    SELECT cantidad_disponible
    INTO stock_actual
    FROM inventarios
    WHERE id_inventario = OLD.id_inventario;

    -- Stock antes de eliminar
    SET stock_anterior = stock_actual - OLD.cantidad;

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
        'SALIDA_ELIMINACION',
        'salida_producto',
        OLD.id_salida_producto,
        OLD.id_salida,
        OLD.id_inventario,
        stock_anterior,
        stock_actual,
        OLD.cantidad,
        stock_actual,
        IFNULL(@usuario_id, 0)
    );

END;

CREATE TRIGGER trg_compra_producto_update
AFTER UPDATE ON compra_productos
FOR EACH ROW
BEGIN

    DECLARE stock_actual INT;
    DECLARE stock_anterior INT;
    DECLARE diferencia_mov INT;

    SET diferencia_mov = NEW.cantidad - OLD.cantidad;

    SELECT cantidad_disponible
    INTO stock_actual
    FROM inventarios
    WHERE id_inventario = NEW.id_inventario;

    SET stock_anterior = stock_actual - diferencia_mov;

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
        'COMPRA_EDICION',
        'compra_producto',
        NEW.id_compra_producto,
        NEW.id_compra,
        NEW.id_inventario,
        stock_anterior,
        stock_actual,
        diferencia_mov,
        stock_actual,
        IFNULL(@usuario_id, 0)
    );

END;

CREATE TRIGGER trg_compra_producto_delete
AFTER DELETE ON compra_productos
FOR EACH ROW
BEGIN

    DECLARE stock_actual INT;
    DECLARE stock_anterior INT;

    SELECT cantidad_disponible
    INTO stock_actual
    FROM inventarios
    WHERE id_inventario = OLD.id_inventario;

    SET stock_anterior = stock_actual + OLD.cantidad;

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
        'COMPRA_ELIMINACION',
        'compra_producto',
        OLD.id_compra_producto,
        OLD.id_compra,
        OLD.id_inventario,
        stock_anterior,
        stock_actual,
        -OLD.cantidad,
        stock_actual,
        IFNULL(@usuario_id, 0)
    );

END;
