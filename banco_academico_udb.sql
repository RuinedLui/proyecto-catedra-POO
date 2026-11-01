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