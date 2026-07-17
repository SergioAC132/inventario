CREATE TABLE destinatarios (
    id_destinatario BIGINT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(150) NOT NULL
);

ALTER TABLE salidas
DROP CHECK chk_salida_folio_destinatario;

ALTER TABLE salidas
DROP COLUMN destinatario;

ALTER TABLE salidas
ADD COLUMN id_destinatario BIGINT NULL AFTER folio;

ALTER TABLE salidas
ADD CONSTRAINT fk_salida_destinatario
    FOREIGN KEY (id_destinatario) REFERENCES destinatarios(id_destinatario);

ALTER TABLE salidas
ADD CONSTRAINT chk_salida_folio_destinatario
    CHECK (folio IS NULL OR id_destinatario IS NULL);
