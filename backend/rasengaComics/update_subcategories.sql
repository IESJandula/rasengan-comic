-- Actualizar subcategorías basadas en el nombre del producto y categoría

-- TCG Products
UPDATE products SET subcategory = 'Yu-Gi-Oh' WHERE category = 'TCG' AND LOWER(name) LIKE '%yu-gi-oh%';
UPDATE products SET subcategory = 'Magic' WHERE category = 'TCG' AND (LOWER(name) LIKE '%magic%' OR LOWER(name) LIKE '%mtg%' OR LOWER(name) LIKE '%commander%');
UPDATE products SET subcategory = 'Pokemon' WHERE category = 'TCG' AND LOWER(name) LIKE '%pokémon%';
UPDATE products SET subcategory = 'One Piece' WHERE category = 'TCG' AND LOWER(name) LIKE '%one piece%';

-- Manga Products
UPDATE products SET subcategory = 'Shonen' WHERE category = 'Manga' AND (LOWER(name) LIKE '%attack on titan%' OR LOWER(name) LIKE '%chainsaw man%' OR LOWER(name) LIKE '%jujutsu kaisen%' OR LOWER(name) LIKE '%one piece%');
UPDATE products SET subcategory = 'Seinen' WHERE category = 'Manga' AND (LOWER(name) LIKE '%berserk%' OR LOWER(name) LIKE '%tokyo ghoul%');
UPDATE products SET subcategory = 'Shojo' WHERE category = 'Manga' AND LOWER(name) LIKE '%pandora%';

-- Comics Products
UPDATE products SET subcategory = 'DC' WHERE category = 'Comics' AND (LOWER(name) LIKE '%batman%' OR LOWER(name) LIKE '%superman%');
UPDATE products SET subcategory = 'Marvel' WHERE category = 'Comics' AND LOWER(name) LIKE '%spider-man%';
UPDATE products SET subcategory = 'Image' WHERE category = 'Comics' AND LOWER(name) LIKE '%walking dead%';

-- Figuras Products
UPDATE products SET subcategory = 'Funko Pop' WHERE category = 'Figuras' AND LOWER(name) LIKE '%funko%';
UPDATE products SET subcategory = 'Nendoroid' WHERE category = 'Figuras' AND LOWER(name) LIKE '%nendoroid%';
UPDATE products SET subcategory = 'Estatuas' WHERE category = 'Figuras' AND LOWER(name) LIKE '%statue%';
UPDATE products SET subcategory = 'Bustos' WHERE category = 'Figuras' AND LOWER(name) LIKE '%bust%';
UPDATE products SET subcategory = 'Scale Figures' WHERE category = 'Figuras' AND (LOWER(name) LIKE '%figure%' OR LOWER(name) LIKE '%gear%') AND subcategory IS NULL;

-- Juegos de mesa Products
UPDATE products SET subcategory = 'Estrategia' WHERE category = 'Juegos de mesa' AND (LOWER(name) LIKE '%catan%' OR LOWER(name) LIKE '%ticket to ride%' OR LOWER(name) LIKE '%wingspan%');
UPDATE products SET subcategory = 'Cooperativos' WHERE category = 'Juegos de mesa' AND LOWER(name) LIKE '%pandemic%';

-- Accesorios Products
UPDATE products SET subcategory = 'Fundas' WHERE category = 'Accesorios' AND (LOWER(name) LIKE '%sleeve%' OR LOWER(name) LIKE '%funda%');
UPDATE products SET subcategory = 'Deck Box' WHERE category = 'Accesorios' AND LOWER(name) LIKE '%deck box%';
UPDATE products SET subcategory = 'Playmat' WHERE category = 'Accesorios' AND LOWER(name) LIKE '%playmat%';
UPDATE products SET subcategory = 'Dados' WHERE category = 'Accesorios' AND LOWER(name) LIKE '%dado%';

-- Verificar resultados
SELECT id, name, category, subcategory FROM products WHERE subcategory IS NOT NULL LIMIT 10;
