-- TABLA DE MOVIMIENTOS BANCARIOS

CREATE TABLE IF NOT EXISTS movimiento (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    numero_cuenta INT NOT NULL,
    numero_cuenta_destino INT NULL,
    tipo_movimiento VARCHAR(20) NOT NULL,
    monto DECIMAL(12,2) NOT NULL,
    fecha_movimiento DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    descripcion VARCHAR(255),

    CONSTRAINT chk_movimiento_monto
        CHECK (monto > 0),

    CONSTRAINT chk_movimiento_tipo
        CHECK (tipo_movimiento IN (
            'DEPOSITO', 'RETIRO', 'TRANSFERENCIA'
        )),

    CONSTRAINT chk_movimiento_destino
        CHECK (
            (tipo_movimiento = 'TRANSFERENCIA'
             AND numero_cuenta_destino IS NOT NULL)
            OR
            (tipo_movimiento IN ('DEPOSITO', 'RETIRO')
             AND numero_cuenta_destino IS NULL)
        ),

    CONSTRAINT fk_movimiento_cuenta
        FOREIGN KEY (numero_cuenta)
        REFERENCES cuenta(numero_cuenta),

    CONSTRAINT fk_movimiento_destino
        FOREIGN KEY (numero_cuenta_destino)
        REFERENCES cuenta(numero_cuenta)
);