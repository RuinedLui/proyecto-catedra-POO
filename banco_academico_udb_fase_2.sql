CREATE DATABASE IF NOT EXISTS banco_academico_db;
USE banco_academico_db;

CREATE TABLE cliente (
    codigo_cliente INT AUTO_INCREMENT PRIMARY KEY,
    dui VARCHAR(10) UNIQUE NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    telefono VARCHAR(15),
    correo_electronico VARCHAR(100),
    direccion TEXT,
    estado VARCHAR(20) NOT NULL DEFAULT 'Activo',
    -- Validación: El estado del cliente solo puede ser uno de estos tres
    CONSTRAINT chk_estado_cliente CHECK (estado IN ('Activo', 'Inactivo', 'Bloqueado'))
);

CREATE TABLE cuenta (
    numero_cuenta INT AUTO_INCREMENT PRIMARY KEY,
    codigo_cliente INT NOT NULL,
    tipo_cuenta VARCHAR(30) NOT NULL,
    saldo DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    estado VARCHAR(20) NOT NULL DEFAULT 'Activa',
    
    -- Validación: El saldo no puede ser negativo por defecto
    CONSTRAINT chk_saldo_positivo CHECK (saldo >= 0.00),
    
    -- Validación: El estado de la cuenta solo puede ser válido
    CONSTRAINT chk_estado_cuenta CHECK (estado IN ('Activa', 'Inactiva', 'Bloqueada')),
    
    -- Integridad referencial (Llave foránea)
    CONSTRAINT fk_cuenta_cliente FOREIGN KEY (codigo_cliente) 
        REFERENCES cliente(codigo_cliente) ON DELETE CASCADE ON UPDATE CASCADE
);

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
        CHECK (tipo_movimiento IN ('DEPOSITO', 'RETIRO', 'TRANSFERENCIA')),

    CONSTRAINT chk_movimiento_destino
        CHECK ((tipo_movimiento = 'TRANSFERENCIA' AND numero_cuenta_destino IS NOT NULL)
            OR
            (tipo_movimiento IN ('DEPOSITO', 'RETIRO') AND numero_cuenta_destino IS NULL)),

    CONSTRAINT fk_movimiento_cuenta FOREIGN KEY (numero_cuenta)
        REFERENCES cuenta(numero_cuenta),

    CONSTRAINT fk_movimiento_destino FOREIGN KEY (numero_cuenta_destino)
        REFERENCES cuenta(numero_cuenta)
);