-- Script para agregar campos de dirección a la tabla usuarios
-- Ejecutar este script antes de usar las nuevas funcionalidades

USE rasengan_comics_database;

-- Agregar columnas de dirección a la tabla usuarios
ALTER TABLE usuarios
ADD COLUMN IF NOT EXISTS telefono VARCHAR(20),
ADD COLUMN IF NOT EXISTS calle VARCHAR(255),
ADD COLUMN IF NOT EXISTS ciudad VARCHAR(100),
ADD COLUMN IF NOT EXISTS codigoPostal VARCHAR(20),
ADD COLUMN IF NOT EXISTS pais VARCHAR(100);

-- Verificar que las columnas se agregaron correctamente
DESCRIBE usuarios;
