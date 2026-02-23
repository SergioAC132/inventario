INSERT IGNORE INTO usuarios (username, password, enabled, id_rol)

/*Starting pass = admin123*/
VALUES (
    'admin',
    '$2a$12$2a7JtaoNKMQM587beRHLUeGDOtnsy3YWNBRM3IXbEYABnQfk/kWdW',
    true,
    (SELECT id_rol FROM roles WHERE nombre = 'ADMIN')
);