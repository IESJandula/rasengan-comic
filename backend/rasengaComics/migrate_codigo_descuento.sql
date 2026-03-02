-- Script de migración para actualizar la tabla codigo_descuento
-- Solo ejecutar si hay datos existentes que quieras preservar
-- Si no hay datos, deja que Hibernate recree la tabla automáticamente

-- Opción 1: Eliminar la tabla existente y dejar que Hibernate la recree
-- DROP TABLE IF EXISTS codigo_descuento;

-- Opción 2: Migrar datos existentes (si los hay)
-- Nota: Este script asume que tienes datos en formato antiguo que quieres convertir

-- Primero, hacer backup de la tabla
-- CREATE TABLE codigo_descuento_backup AS SELECT * FROM codigo_descuento;

-- Renombrar y agregar columnas
ALTER TABLE codigo_descuento 
    CHANGE COLUMN codigo code VARCHAR(255) NOT NULL,
    ADD COLUMN type VARCHAR(255) NOT NULL DEFAULT 'percentage' AFTER code,
    CHANGE COLUMN porcentaje value DOUBLE NOT NULL,
    ADD COLUMN scope VARCHAR(255) NOT NULL DEFAULT 'global' AFTER value,
    ADD COLUMN scope_value VARCHAR(255) AFTER scope,
    ADD COLUMN start_date DATETIME NOT NULL DEFAULT NOW() AFTER scope_value,
    CHANGE COLUMN fecha_vencimiento end_date DATETIME NOT NULL,
    DROP COLUMN cantidad_usos,
    DROP COLUMN usos_restantes;

-- Agregar constraint de unique para code
ALTER TABLE codigo_descuento 
    ADD CONSTRAINT uk_codigo_descuento_code UNIQUE (code);

-- Verificar la estructura
DESCRIBE codigo_descuento;
