ALTER TABLE usuarios ADD COLUMN id_rol BIGINT NOT NULL;
ALTER TABLE usuarios ADD CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES roles(id_rol);

DROP TABLE usuario_roles;