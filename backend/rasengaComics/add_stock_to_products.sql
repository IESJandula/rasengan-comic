-- Agregar columna stock a la tabla products si no existe
ALTER TABLE products ADD COLUMN stock INT NOT NULL DEFAULT 0;

-- Actualizar los productos existentes con stock variado
UPDATE products SET stock = 50 WHERE name LIKE '%One Piece%';
UPDATE products SET stock = 45 WHERE name LIKE '%Naruto%';
UPDATE products SET stock = 60 WHERE name LIKE '%Dragon Ball%';
UPDATE products SET stock = 40 WHERE name LIKE '%Attack on Titan%';
UPDATE products SET stock = 55 WHERE name LIKE '%My Hero Academia%';
UPDATE products SET stock = 50 WHERE name LIKE '%Demon Slayer%';
UPDATE products SET stock = 48 WHERE name LIKE '%Jujutsu Kaisen%';
UPDATE products SET stock = 42 WHERE name LIKE '%Tokyo Ghoul%';
UPDATE products SET stock = 38 WHERE name LIKE '%Death Note%';
UPDATE products SET stock = 35 WHERE name LIKE '%Fullmetal Alchemist%';
UPDATE products SET stock = 45 WHERE name LIKE '%Chainsaw Man%';
UPDATE products SET stock = 52 WHERE name LIKE '%Spy x Family%';
UPDATE products SET stock = 30 WHERE name LIKE '%Berserk%';
UPDATE products SET stock = 28 WHERE name LIKE '%Vagabond%';
UPDATE products SET stock = 40 WHERE name LIKE '%Bleach%';

-- Asignar stock por defecto a productos que no tengan valor específico
UPDATE products SET stock = 25 WHERE stock = 0;
