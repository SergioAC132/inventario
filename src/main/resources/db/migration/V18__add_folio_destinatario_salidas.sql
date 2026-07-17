ALTER TABLE salidas
ADD COLUMN folio BIGINT NULL AFTER destino,
ADD COLUMN destinatario VARCHAR(150) NULL AFTER folio;

ALTER TABLE salidas
ADD CONSTRAINT chk_salida_folio_destinatario
    CHECK (folio IS NULL OR destinatario IS NULL);
