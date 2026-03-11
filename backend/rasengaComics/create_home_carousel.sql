-- Crea la tabla para la configuracion del carrusel de la home
-- Ejecutar solo si no existe ya la tabla

CREATE TABLE IF NOT EXISTS home_carousel_config (
    id INT PRIMARY KEY,
    slide1 VARCHAR(1000),
    slide2 VARCHAR(1000),
    slide3 VARCHAR(1000)
);

-- Inserta la fila de configuracion por defecto (id=1) si no existe
INSERT IGNORE INTO home_carousel_config (id, slide1, slide2, slide3)
VALUES (1, '', '', '');
