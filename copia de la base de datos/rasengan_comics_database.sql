-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1:3307
-- Tiempo de generación: 16-03-2026 a las 20:02:44
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `rasengan_comics_database`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `carritos`
--

CREATE TABLE `carritos` (
  `id` bigint(20) NOT NULL,
  `fecha_actualizacion` datetime(6) DEFAULT NULL,
  `fecha_creacion` datetime(6) DEFAULT NULL,
  `total` double DEFAULT NULL,
  `usuario_id` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `carritos`
--

INSERT INTO `carritos` (`id`, `fecha_actualizacion`, `fecha_creacion`, `total`, `usuario_id`) VALUES
(1, '2026-03-16 19:39:00.680752', '2026-02-16 21:17:28.289446', 0, 'CZ4qYn6poycoOTyQg1UCzkjIvoZ2'),
(2, '2026-03-09 19:45:43.489432', '2026-03-09 18:36:07.938894', 0, 'JrG7DYyFu6PbAzS2jFZcEKznYJC2');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categorias`
--

CREATE TABLE `categorias` (
  `id` bigint(20) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `imagen_url` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `codigo_descuento`
--

CREATE TABLE `codigo_descuento` (
  `id` bigint(20) NOT NULL,
  `activo` bit(1) DEFAULT NULL,
  `cantidad_usos` int(11) DEFAULT NULL,
  `codigo` varchar(255) DEFAULT NULL,
  `fecha_vencimiento` datetime(6) DEFAULT NULL,
  `porcentaje` double DEFAULT NULL,
  `usos_restantes` int(11) DEFAULT NULL,
  `code` varchar(255) NOT NULL,
  `end_date` datetime(6) NOT NULL,
  `scope` varchar(255) NOT NULL,
  `scope_value` varchar(255) DEFAULT NULL,
  `start_date` datetime(6) NOT NULL,
  `type` varchar(255) NOT NULL,
  `value` double NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `codigo_descuento`
--

INSERT INTO `codigo_descuento` (`id`, `activo`, `cantidad_usos`, `codigo`, `fecha_vencimiento`, `porcentaje`, `usos_restantes`, `code`, `end_date`, `scope`, `scope_value`, `start_date`, `type`, `value`) VALUES
(2, b'1', NULL, NULL, NULL, NULL, NULL, 'UNEURO', '2026-03-31 21:59:59.999000', 'global', '', '2026-03-10 23:00:00.000000', 'fixed', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalle_pedido`
--

CREATE TABLE `detalle_pedido` (
  `id` bigint(20) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precio_unitario` double NOT NULL,
  `pedido_id` bigint(20) NOT NULL,
  `producto_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `detalle_pedido`
--

INSERT INTO `detalle_pedido` (`id`, `cantidad`, `precio_unitario`, `pedido_id`, `producto_id`) VALUES
(2, 1, 12.99, 3, 23),
(3, 1, 11.99, 4, 6),
(4, 1, 10, 9, 31),
(5, 1, 11.99, 9, 6),
(6, 1, 10, 10, 31),
(7, 1, 10, 11, 31),
(8, 1, 159.99, 12, 18),
(9, 1, 10, 13, 31),
(10, 1, 49.99, 13, 5),
(11, 1, 11.99, 13, 6),
(12, 1, 10, 14, 31),
(13, 1, 29.99, 14, 32),
(14, 1, 49.99, 14, 33),
(15, 1, 14, 15, 34),
(16, 1, 8.99, 16, 35),
(17, 1, 5.99, 16, 36);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `evento`
--

CREATE TABLE `evento` (
  `id` bigint(20) NOT NULL,
  `descripcion` varchar(1000) NOT NULL,
  `fecha_hora` datetime(6) NOT NULL,
  `imagen_url` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) NOT NULL,
  `ubicacion` varchar(255) NOT NULL,
  `tipo` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `evento`
--

INSERT INTO `evento` (`id`, `descripcion`, `fecha_hora`, `imagen_url`, `nombre`, `ubicacion`, `tipo`) VALUES
(36, 'Competición oficial de Magic con premios para los 3 primeros puestos. Formato Standard. Inscripción limitada a 32 jugadores. Yugi es mejor', '2026-02-15 18:00:00.000000', 'https://images.unsplash.com/photo-1614680376573-df3480f0c6ff?w=400', 'Torneo TCG Magic: The Gathering', 'Sala Principal', NULL),
(37, 'Aprende las técnicas básicas del dibujo manga con un ilustrador profesional. Materiales incluidos. Todos los niveles bienvenidos.', '2026-02-22 17:00:00.000000', 'https://images.unsplash.com/photo-1612036782180-6f0b6cd846fe?w=400', 'Taller de Dibujo Manga', 'Aula de Arte', NULL),
(38, 'Velada de juegos de mesa con nuevos lanzamientos. Trae a tus amigos y descubre nuevos juegos. Torneos de Catan y Carcassonne.', '2026-02-18 19:00:00.000000', 'https://images.unsplash.com/photo-1610890716171-6b1bb98ffd09?w=400', 'Noche de Board Games', 'Zona Gaming', NULL),
(39, 'Conoce a autores locales de manga, firma de ejemplares y sesión de preguntas. Edición limitada disponible para compra.', '2026-02-28 16:00:00.000000', 'https://images.unsplash.com/photo-1618519764620-7403abdbdfe9?w=400', 'Meet & Greet con Autores de Manga', 'Entrada Principal', NULL),
(40, 'Torneo oficial de Pokémon Trading Card Game. Formato Expanded. Premios: sobres, cartas promocionales y trofeos.', '2026-03-02 15:00:00.000000', 'https://images.unsplash.com/photo-1606503153255-59d7a5e5a1b5?w=400', 'Campeonato Pokémon TCG', 'Sala Principal', NULL),
(41, 'Proyección de los últimos episodios de animes populares en pantalla grande. Palomitas y bebidas incluidas.', '2026-02-20 20:00:00.000000', 'https://images.unsplash.com/photo-1578632767115-351597cf2477?w=400', 'Screening Anime: Nuevos Estrenos', 'Auditorio', NULL),
(42, 'Competición de Yu-Gi-Oh con formato avanzado. Inscripción el mismo día. Premios para los 8 mejores.', '2026-03-08 17:00:00.000000', 'https://images.unsplash.com/photo-1628868445294-d0f4e2dd0ff5?w=400', 'Torneo Yu-Gi-Oh! Duel Masters', 'Sala de Torneos', NULL),
(43, 'Taller de diseño y creación de personajes para cómics y manga. Técnicas de personalidad, diseño visual y storytelling.', '2026-03-05 18:30:00.000000', 'https://images.unsplash.com/photo-1609743522653-52354461eb27?w=400', 'Workshop: Creación de Personajes', 'Aula Creativa', NULL),
(44, 'Trae tus cartas duplicadas e intercámbialas con otros coleccionistas. Magic, Pokémon, Yu-Gi-Oh y más.', '2026-02-25 16:00:00.000000', 'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=400', 'Feria de Intercambio de Cartas', 'Zona de Intercambios', NULL),
(45, 'Maratón de episodios especiales de One Piece. 6 horas de aventura con Luffy y la tripulación. Merchandising exclusivo.', '2026-03-10 14:00:00.000000', 'https://images.unsplash.com/photo-1635863138275-d9b33299680b?w=400', 'Maratón de One Piece', 'Auditorio', NULL),
(46, 'Competición oficial de Dragon Ball Super TCG. Formato estándar. Premios exclusivos de la serie.', '2026-03-12 16:00:00.000000', 'https://images.unsplash.com/photo-1643714332478-89616af39d39?w=400', 'Torneo Dragon Ball Super Card Game', 'Sala Principal', NULL),
(47, 'Concurso de cosplay con premios en múltiples categorías. Jurado profesional. Inscripciones hasta el día del evento.', '2026-03-15 18:00:00.000000', 'https://images.unsplash.com/photo-1609743522653-52354461eb27?w=400', 'Cosplay Contest 2026', 'Escenario Principal', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `favoritos`
--

CREATE TABLE `favoritos` (
  `id` bigint(20) NOT NULL,
  `fecha_agregado` datetime(6) DEFAULT NULL,
  `producto_id` bigint(20) NOT NULL,
  `usuario_id` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `home_carousel_config`
--

CREATE TABLE `home_carousel_config` (
  `id` int(11) NOT NULL,
  `slide1` varchar(1000) DEFAULT NULL,
  `slide2` varchar(1000) DEFAULT NULL,
  `slide3` varchar(1000) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `home_carousel_config`
--

INSERT INTO `home_carousel_config` (`id`, `slide1`, `slide2`, `slide3`) VALUES
(1, '', '', '');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `item_carrito`
--

CREATE TABLE `item_carrito` (
  `id` bigint(20) NOT NULL,
  `cantidad` int(11) DEFAULT NULL,
  `precio_unitario` double DEFAULT NULL,
  `carrito_id` bigint(20) NOT NULL,
  `producto_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `notificaciones`
--

CREATE TABLE `notificaciones` (
  `id` bigint(20) NOT NULL,
  `contenido` varchar(255) DEFAULT NULL,
  `fecha` datetime(6) DEFAULT NULL,
  `leida` bit(1) DEFAULT NULL,
  `titulo` varchar(255) DEFAULT NULL,
  `usuario_id` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pedidos`
--

CREATE TABLE `pedidos` (
  `id` bigint(20) NOT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `fecha_pedido` datetime(6) DEFAULT NULL,
  `usuario_id` varchar(255) NOT NULL,
  `stripe_payment_intent_id` varchar(255) DEFAULT NULL,
  `stripe_session_id` varchar(255) DEFAULT NULL,
  `total` double DEFAULT NULL,
  `metodo_entrega` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `pedidos`
--

INSERT INTO `pedidos` (`id`, `estado`, `fecha_pedido`, `usuario_id`, `stripe_payment_intent_id`, `stripe_session_id`, `total`, `metodo_entrega`) VALUES
(3, 'PAGADO', '2026-03-03 20:29:42.223556', 'CZ4qYn6poycoOTyQg1UCzkjIvoZ2', 'pi_3T6ydVRys0KojuGP1cTtCdYC', 'cs_test_a18RTul2ZSPOlz7jnKptgua42tXuBpjVapenKZCCfeIq1V7WpYhtg5PZUa', 12.99, NULL),
(4, 'DISPONIBLE', '2026-03-03 20:30:30.408911', 'CZ4qYn6poycoOTyQg1UCzkjIvoZ2', 'pi_3T6yeIRys0KojuGP0NKhGKot', 'cs_test_a16i7HarGogdke613NlPpHqlD5cHUlZsn60QcJhxQ55rNaeRdFxOivM8wV', 11.99, NULL),
(9, 'PAGADO', '2026-03-09 19:43:43.991880', 'JrG7DYyFu6PbAzS2jFZcEKznYJC2', 'pi_3T98mIRys0KojuGP1VT3oFCM', 'cs_test_a1bW3pOOvp7OOIyP1gzzUbrVCRbvQaNr83VIDwvBqlL71dGdkVS3p3ttch', 21.990000000000002, NULL),
(10, 'PAGADO', '2026-03-09 19:44:58.460554', 'CZ4qYn6poycoOTyQg1UCzkjIvoZ2', 'pi_3T98nVRys0KojuGP08h3VotT', 'cs_test_a13RCu4AWMKgxxK0K09KMSIdL0UT1LF6yhpf7bIDtvk16cqfavt0aYnWBo', 10, NULL),
(11, 'RECOGIDO', '2026-03-09 19:45:41.397275', 'JrG7DYyFu6PbAzS2jFZcEKznYJC2', 'pi_3T98oCRys0KojuGP0NjlKua7', 'cs_test_a1jPdsD0DZHgQQFDYNZvGbCdO8R7MFDgr2O5IMKwJkdhDSGB67CYmOYJ7v', 10, NULL),
(12, 'RECOGIDO', '2026-03-09 20:37:27.435531', 'CZ4qYn6poycoOTyQg1UCzkjIvoZ2', 'pi_3T99cIRys0KojuGP19REekly', 'cs_test_a1lnjoxwTDAHN5fD43EbgGKClBnnOHpKiASTOYpGNw10fS8k6piZUPwZcg', 159.99, 'tienda'),
(13, 'PAGADO', '2026-03-11 20:33:07.961808', 'CZ4qYn6poycoOTyQg1UCzkjIvoZ2', 'pi_3T9sVBRys0KojuGP0NSKgZpH', 'cs_test_b1OeeaFGGP8EKhsJu3Z4R9e2mDP6jRnbESG1Hqar7FuVishi4ilQI2eYY4', 71.98, 'envio'),
(14, 'PAGADO', '2026-03-11 20:41:41.507972', 'CZ4qYn6poycoOTyQg1UCzkjIvoZ2', 'pi_3T9sdSRys0KojuGP1clso87m', 'cs_test_b1a9UvkEHkNs4tGtQPsFsa7Lc46RelODrMkhXrxZoue1s5bG4oZzUGPs9P', 89.97999999999999, 'tienda'),
(15, 'PAGADO', '2026-03-11 20:50:12.981246', 'CZ4qYn6poycoOTyQg1UCzkjIvoZ2', 'pi_3T9sliRys0KojuGP0lwnTh4a', 'cs_test_b10mfO452Wbm2qWmV7YWeoVll6sejagSCbsebBVvq7YYH97co3pcUh4Ji9', 14, 'envio'),
(16, 'PAGADO', '2026-03-11 21:42:04.529805', 'CZ4qYn6poycoOTyQg1UCzkjIvoZ2', 'pi_3T9tZtRys0KojuGP08rhc6zP', 'cs_test_b1VrdBuD2k0Qjn4RMCbORqdyKvAIcsFIHq8xFTxkcoyElIHbLWilQAnB55', 14.98, 'tienda');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `productos`
--

CREATE TABLE `productos` (
  `id` bigint(20) NOT NULL,
  `categoria_id` bigint(20) DEFAULT NULL,
  `descripcion` varchar(2000) DEFAULT NULL,
  `imagen_url` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `precio` double DEFAULT NULL,
  `stock` int(11) DEFAULT NULL,
  `subcategoria_id` bigint(20) DEFAULT NULL,
  `source_product_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `productos`
--

INSERT INTO `productos` (`id`, `categoria_id`, `descripcion`, `imagen_url`, `nombre`, `precio`, `stock`, `subcategoria_id`, `source_product_id`) VALUES
(1, 1, 'El inicio de la aventura de Luffy en busca del One Piece. Un manga legendario que ha cautivado a millones de lectores.', 'https://via.placeholder.com/200x300/dc2626/ffffff?text=One+Piece+1', 'One Piece Vol. 1', 9.95, 50, NULL, NULL),
(2, 1, 'La historia de Naruto Uzumaki comienza. El ninja m??s hiperactivo de la aldea oculta de la Hoja.', 'https://via.placeholder.com/200x300/ff9800/ffffff?text=Naruto+1', 'Naruto Vol. 1', 9.95, 45, NULL, NULL),
(3, 1, 'El cl??sico manga de Akira Toriyama. La b??squeda de las esferas del drag??n comienza con Goku.', 'https://via.placeholder.com/200x300/ff6b00/ffffff?text=Dragon+Ball+1', 'Dragon Ball Vol. 1', 8.95, 60, NULL, NULL),
(4, 1, 'La humanidad lucha contra los titanes. Un manga oscuro y ??pico que revolucion?? el g??nero.', 'https://via.placeholder.com/200x300/654321/ffffff?text=AOT+1', 'Attack on Titan Vol. 1', 10.95, 40, NULL, NULL),
(5, 1, 'Izuku Midoriya sue??a con ser un h??roe. Una nueva generaci??n de h??roes en el manga de superh??roes.', 'https://via.placeholder.com/200x300/2ecc71/ffffff?text=MHA+1', 'My Hero Academia Vol. 1', 9.5, 55, NULL, NULL),
(6, 1, 'Manga - Shonen', 'https://via.placeholder.com/200x300/e74c3c/ffffff?text=Demon+Slayer+1', 'Chainsaw Man Vol. 14', 11.99, 43, NULL, NULL),
(7, 1, 'Yuji Itadori y el mundo de los hechiceros. Maldiciones, acci??n y poderes sobrenaturales.', 'https://via.placeholder.com/200x300/3498db/ffffff?text=JJK+1', 'Jujutsu Kaisen Vol. 1', 10.5, 48, NULL, NULL),
(8, 1, 'Ken Kaneki se convierte en un ghoul. Un thriller sobrenatural en el tokio moderno.', 'https://via.placeholder.com/200x300/34495e/ffffff?text=Tokyo+Ghoul+1', 'Tokyo Ghoul Vol. 1', 9.95, 42, NULL, NULL),
(9, 1, 'Light Yagami encuentra el cuaderno de la muerte. Un juego mental de gato y rat??n.', 'https://via.placeholder.com/200x300/000000/ffffff?text=Death+Note+1', 'Death Note Vol. 1', 10.95, 38, NULL, NULL),
(10, 1, 'Los hermanos Elric buscan la piedra filosofal. Alquimia, aventura y sacrificio.', 'https://via.placeholder.com/200x300/95a5a6/ffffff?text=FMA+1', 'Fullmetal Alchemist Vol. 1', 9.5, 35, NULL, NULL),
(11, 1, 'Denji y su perro-motosierra luchan contra demonios. Acci??n brutal y surrealista.', 'https://via.placeholder.com/200x300/f39c12/ffffff?text=Chainsaw+Man+1', 'Chainsaw Man Vol. 1', 10.95, 45, NULL, NULL),
(12, 1, 'Una familia de esp??as, asesinos y tel??patas. Comedia y acci??n en una misi??n imposible.', 'https://via.placeholder.com/200x300/16a085/ffffff?text=Spy+Family+1', 'Spy x Family Vol. 1', 9.95, 52, NULL, NULL),
(13, 1, 'Guts, el espadach??n negro, busca venganza. Dark fantasy ??pico y brutal.', 'https://via.placeholder.com/200x300/2c3e50/ffffff?text=Berserk+1', 'Berserk Vol. 1', 12.95, 30, NULL, NULL),
(14, 1, 'La historia de Musashi Miyamoto. Arte excepcional y filosof??a samur??i.', 'https://via.placeholder.com/200x300/8e44ad/ffffff?text=Vagabond+1', 'Vagabond Vol. 1', 11.95, 28, NULL, NULL),
(15, 1, 'Ichigo Kurosaki se convierte en shinigami. Espadas, esp??ritus y batallas ??picas.', 'https://via.placeholder.com/200x300/e67e22/ffffff?text=Bleach+1', 'Bleach Vol. 1', 9.5, 40, NULL, NULL),
(16, 1, 'El inicio de la aventura de Luffy en busca del One Piece. Un manga legendario que ha cautivado a millones de lectores.', 'https://via.placeholder.com/200x300/dc2626/ffffff?text=One+Piece+1', 'One Piece Vol. 1', 9.95, 50, NULL, NULL),
(17, 1, 'La historia de Naruto Uzumaki comienza. El ninja m??s hiperactivo de la aldea oculta de la Hoja.', 'https://via.placeholder.com/200x300/ff9800/ffffff?text=Naruto+1', 'Naruto Vol. 1', 9.95, 45, NULL, NULL),
(18, 1, 'El cl??sico manga de Akira Toriyama. La b??squeda de las esferas del drag??n comienza con Goku.', 'https://via.placeholder.com/200x300/ff6b00/ffffff?text=Dragon+Ball+1', 'Dragon Ball Vol. 1', 8.95, 60, NULL, NULL),
(19, 1, 'La humanidad lucha contra los titanes. Un manga oscuro y ??pico que revolucion?? el g??nero.', 'https://via.placeholder.com/200x300/654321/ffffff?text=AOT+1', 'Attack on Titan Vol. 1', 10.95, 40, NULL, NULL),
(20, 1, 'Izuku Midoriya sue??a con ser un h??roe. Una nueva generaci??n de h??roes en el manga de superh??roes.', 'https://via.placeholder.com/200x300/2ecc71/ffffff?text=MHA+1', 'My Hero Academia Vol. 1', 9.5, 55, NULL, NULL),
(21, 1, 'Tanjiro busca venganza contra los demonios. Una historia de hermanos y perseverancia.', 'https://via.placeholder.com/200x300/e74c3c/ffffff?text=Demon+Slayer+1', 'Demon Slayer Vol. 1', 9.95, 50, NULL, NULL),
(22, 1, 'Yuji Itadori y el mundo de los hechiceros. Maldiciones, acci??n y poderes sobrenaturales.', 'https://via.placeholder.com/200x300/3498db/ffffff?text=JJK+1', 'Jujutsu Kaisen Vol. 1', 10.5, 48, NULL, NULL),
(23, 1, 'Manga - Shonen', 'https://via.placeholder.com/200x300/34495e/ffffff?text=Tokyo+Ghoul+1', 'One Piece Vol. 100', 12.99, 49, NULL, NULL),
(24, 1, 'Light Yagami encuentra el cuaderno de la muerte. Un juego mental de gato y rat??n.', 'https://via.placeholder.com/200x300/000000/ffffff?text=Death+Note+1', 'Death Note Vol. 1', 10.95, 38, NULL, NULL),
(25, 1, 'Los hermanos Elric buscan la piedra filosofal. Alquimia, aventura y sacrificio.', 'https://via.placeholder.com/200x300/95a5a6/ffffff?text=FMA+1', 'Fullmetal Alchemist Vol. 1', 9.5, 35, NULL, NULL),
(26, 1, 'Denji y su perro-motosierra luchan contra demonios. Acci??n brutal y surrealista.', 'https://via.placeholder.com/200x300/f39c12/ffffff?text=Chainsaw+Man+1', 'Chainsaw Man Vol. 1', 10.95, 45, NULL, NULL),
(27, 1, 'Una familia de esp??as, asesinos y tel??patas. Comedia y acci??n en una misi??n imposible.', 'https://via.placeholder.com/200x300/16a085/ffffff?text=Spy+Family+1', 'Spy x Family Vol. 1', 9.95, 52, NULL, NULL),
(28, 1, 'Guts, el espadach??n negro, busca venganza. Dark fantasy ??pico y brutal.', 'https://via.placeholder.com/200x300/2c3e50/ffffff?text=Berserk+1', 'Berserk Vol. 1', 12.95, 30, NULL, NULL),
(29, 1, 'La historia de Musashi Miyamoto. Arte excepcional y filosof??a samur??i.', 'https://via.placeholder.com/200x300/8e44ad/ffffff?text=Vagabond+1', 'Vagabond Vol. 1', 11.95, 28, NULL, NULL),
(30, 1, 'Ichigo Kurosaki se convierte en shinigami. Espadas, esp??ritus y batallas ??picas.', 'https://via.placeholder.com/200x300/e67e22/ffffff?text=Bleach+1', 'Bleach Vol. 1', 9.5, 40, NULL, NULL),
(31, NULL, 'TCG - Yu-Gi-Oh', NULL, 'Yu-Gi-Oh! Booster Box', 10, 24, NULL, NULL),
(32, NULL, 'Accesorios - Playmat', NULL, 'Custom Playmat', 29.99, 24, NULL, NULL),
(33, NULL, 'Juegos de mesa - Estrategia', NULL, 'Catan: Base Game', 49.99, 23, NULL, NULL),
(34, NULL, 'Manga - Shonen', NULL, 'oJo\'s Bizarre Adventure Part 2: Battle Tendency (Digital Colored Comics)', 14, 4, NULL, 42),
(35, NULL, 'Accesorios - Fundas', NULL, 'Pokémon Card Sleeve', 8.99, 24, NULL, 27),
(36, NULL, 'Comics - Marvel', NULL, 'Spider-Man Comic', 5.99, 24, NULL, 29);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `products`
--

CREATE TABLE `products` (
  `id` bigint(20) NOT NULL,
  `available` bit(1) NOT NULL,
  `category` varchar(255) NOT NULL,
  `discount` int(11) DEFAULT NULL,
  `image` varchar(255) NOT NULL,
  `is_new` bit(1) NOT NULL,
  `is_reserve` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `original_price` double DEFAULT NULL,
  `price` double NOT NULL,
  `rating` double NOT NULL,
  `reviews` int(11) NOT NULL,
  `subcategory` varchar(255) DEFAULT NULL,
  `stock` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `products`
--

INSERT INTO `products` (`id`, `available`, `category`, `discount`, `image`, `is_new`, `is_reserve`, `name`, `original_price`, `price`, `rating`, `reviews`, `subcategory`, `stock`) VALUES
(4, b'1', 'Manga', 15, 'http://localhost:8080/uploads/products/6105d576-71c5-413b-996e-db6387ed098d.jpg', b'0', b'0', 'Berserk Deluxe Vol. 8', NULL, 39.99, 0, 0, 'Seinen', 0),
(5, b'1', 'Juegos de mesa', 0, 'http://localhost:8080/uploads/products/2164a4e8-c59f-48fa-8934-48145823fcc4.webp', b'0', b'0', 'Catan: Base Game', NULL, 49.99, 0, 0, 'Estrategia', 23),
(6, b'0', 'Manga', 0, 'http://localhost:8080/uploads/products/e560f856-280f-4eb3-b59b-7758b904ed1e.jpg', b'0', b'1', 'Chainsaw Man Vol. 14', NULL, 11.99, 0, 0, 'Seinen', 41),
(7, b'1', 'Accesorios', 20, 'http://localhost:8080/uploads/products/bdb1d3e5-9ae6-4835-9d9b-ed637a0e25ff.webp', b'0', b'0', 'Tapete amarillo Gamegenic', NULL, 9.99, 0, 0, 'Playmat', 24),
(8, b'1', 'Accesorios', 0, 'http://localhost:8080/uploads/products/5e20d932-a668-43fa-ab8b-3f19c00964d0.jpg', b'0', b'0', 'Dados Metálicos Set', NULL, 34.99, 0, 0, 'Dados', 25),
(9, b'1', 'Accesorios', 0, 'http://localhost:8080/uploads/products/1f00f95a-7a49-42f6-9bbe-2c467fad9a47.webp', b'0', b'0', 'Tapete Charmander nº004. Ultra Pro', NULL, 24.99, 0, 0, 'Playmat', 25),
(10, b'1', 'Accesorios', 0, 'http://localhost:8080/uploads/products/50dd97fa-48b1-4c44-9311-97d5cf77b979.webp', b'0', b'0', 'Dragon Shield Sleeves', NULL, 12.99, 0, 0, 'Fundas', 25),
(11, b'1', 'Figuras', 0, 'http://localhost:8080/uploads/products/082e963b-948e-4897-8b3c-d0664ca2d18f.jpg', b'0', b'0', 'Funko Goku', NULL, 14.99, 0, 0, 'Funko Pop', 25),
(12, b'1', 'Figuras', 0, 'http://localhost:8080/uploads/products/0b27bd05-43bb-4199-9ed0-5ce2f69d908f.jpg', b'0', b'0', 'Gojo Satoru Nendoroid', NULL, 54.99, 0, 0, 'Nendoroid', 25),
(13, b'1', 'Figuras', 0, 'http://localhost:8080/uploads/products/0e408e40-1a34-466c-80fb-59df9d6a558c.jpg', b'0', b'0', 'Goku Ultra Instinct Statue', NULL, 249.99, 0, 0, 'Estatuas', 25),
(14, b'1', 'Manga', 0, 'http://localhost:8080/uploads/products/ed456752-e577-4c45-aa7b-5d2b68e1b845.jpg', b'0', b'0', 'Jujutsu Kaisen Vol. 20', NULL, 11.99, 0, 0, 'Seinen', 48),
(15, b'1', 'Manga', 0, 'http://localhost:8080/uploads/products/239c21b7-6d84-48ca-aa2d-3e936a2e3828.jpg', b'0', b'0', 'Jujutsu Kaisen Vol. 22', NULL, 11.99, 0, 0, 'Seinen', 48),
(16, b'1', 'Figuras', 10, 'http://localhost:8080/uploads/products/96f7d8c5-032b-4fd8-9a22-e60ccbaad9b2.jpg', b'0', b'0', 'Luffy Gear 5', 144.43, 129.99, 0, 0, 'Scale Figures', 25),
(17, b'1', 'Figuras', 0, 'http://localhost:8080/uploads/products/68fc8abb-f1ff-4306-b69e-78e03c0aa70b.jpg', b'0', b'0', 'Luffy Gear 5 chibi', NULL, 139.99, 0, 0, 'Nendoroid', 25),
(18, b'1', 'TCG', 20, 'http://localhost:8080/uploads/products/ee9fd012-0f66-498c-901c-00b1b6f97039.webp', b'0', b'0', 'Final Fantasy: Mazo de Commander Límite', NULL, 90, 0, 0, 'Magic', 24),
(19, b'1', 'TCG', 0, 'http://localhost:8080/uploads/products/b66919c5-f873-4ff5-8b2d-317129f34d18.webp', b'1', b'0', 'MTG El Señor de los Anillos - Relatos de la Tierra Media: Sobre de Edición (12 cartas)', NULL, 8, 0, 0, 'Magic', 25),
(20, b'1', 'TCG', 15, 'http://localhost:8080/uploads/products/a6a9226d-23d3-416d-a1c7-bdd9fa53ab7c.webp', b'0', b'0', 'Final Fantasy: Starter Kit Magic The Gathering', NULL, 21, 0, 0, 'Magic', 25),
(21, b'1', 'Figuras', 0, 'http://localhost:8080/uploads/products/36fce6c7-baa1-4fa3-8827-3942fd4f5299.jpg', b'0', b'0', 'Naruto Shippuden Figura', NULL, 34.99, 0, 0, 'Scale Figures', 45),
(22, b'1', 'Figuras', 0, 'http://localhost:8080/uploads/products/dab5e2ad-e1e4-4f3b-bbe9-479b73411b5c.jpg', b'0', b'0', 'Nendoroid Gojo', NULL, 54.99, 0, 0, 'Nendoroid', 25),
(23, b'1', 'Manga', 10, 'http://localhost:8080/uploads/products/c17fd7c8-3363-47a8-a066-ca039a4dba73.jpg', b'0', b'0', 'One Piece Vol. 100', 14.43, 12.99, 0, 0, 'Shonen', 49),
(24, b'1', 'Juegos de mesa', 0, 'http://localhost:8080/uploads/products/cf6e15d0-8d23-4200-95d6-b3397bafd82f.png', b'0', b'0', 'Pandemic', NULL, 39.99, 0, 0, 'Party', 25),
(25, b'1', 'Manga', 0, 'http://localhost:8080/uploads/products/53500331-adb8-49c8-860a-08d3fb1804f2.jpg', b'0', b'0', 'Pandora Vol. 24', NULL, 11.99, 0, 0, 'Shojo', 25),
(26, b'1', 'TCG', 0, 'http://localhost:8080/uploads/products/0a437e17-35e9-4e9f-b54e-56f352db66d7.jpg', b'0', b'0', 'Pokémon Paradox Rift', NULL, 4.5, 0, 0, 'Pokemon', 25),
(27, b'1', 'Accesorios', 0, 'http://localhost:8080/uploads/products/e2ded10c-d7e8-438c-b6ec-97443a74bdaf.jpg', b'0', b'0', 'Pokémon Card Sleeve', NULL, 8.99, 0, 0, 'Fundas', 24),
(28, b'1', 'TCG', 0, 'http://localhost:8080/uploads/products/4c9c061d-68cf-4b05-9b9b-8776ac1c7130.jpg', b'0', b'0', 'Scarlet Violet Booster', NULL, 4.5, 0, 0, 'Pokemon', 25),
(29, b'1', 'Comics', 15, 'http://localhost:8080/uploads/products/627e4e65-0616-4fe5-bd43-face04686302.jpg', b'0', b'0', 'Spider-Man Comic', NULL, 5.99, 0, 0, 'Marvel', 24),
(30, b'1', 'Comics', 0, 'http://localhost:8080/uploads/products/cb7c4195-ef01-4670-9854-d1bddfec621a.jpg', b'0', b'0', 'The Amazing Spider-Man Comic ', NULL, 5.99, 0, 0, 'Marvel', 25),
(31, b'1', 'Comics', 10, 'http://localhost:8080/uploads/products/af6772d3-babd-4666-b1f8-7145952e3e8e.jpg', b'0', b'0', 'Superman: Action Comics', NULL, 18.99, 0, 0, 'DC', 25),
(32, b'1', 'Juegos de mesa', 0, 'http://localhost:8080/uploads/products/af6a2de0-432b-413a-bd85-5296cabe24d4.jpg', b'0', b'0', 'Ticket to Ride', NULL, 44.99, 0, 0, 'Cooperativos', 25),
(34, b'1', 'Manga', 30, 'http://localhost:8080/uploads/products/f82db6cc-a122-4251-b32e-8e53a98df2bb.jpg', b'0', b'0', 'Tokyo Ghoul Manga Box Set', 257.13, 179.99, 0, 0, 'Seinen', 42),
(35, b'1', 'Accesorios', 0, 'http://localhost:8080/uploads/products/e3ea9ece-f66e-474c-a825-eecc9cf95aa5.webp', b'0', b'0', 'Ultimate Guard Deck Box', NULL, 9.99, 0, 0, 'Deck Box', 25),
(36, b'1', 'Comics', 0, 'http://localhost:8080/uploads/products/5d09afd4-b91e-4c00-af8b-a4df9ec87d7f.jpg', b'0', b'0', 'Walking Dead', NULL, 59.99, 0, 0, 'Dark Horse', 25),
(37, b'1', 'Juegos de mesa', 25, 'http://localhost:8080/uploads/products/3ab05175-fca8-4aed-b9b4-8bea5eb4e2f7.jpg', b'0', b'0', 'Wingspan', 73.32, 54.99, 0, 0, 'Estrategia', 25),
(38, b'1', 'Juegos de mesa', 0, 'http://localhost:8080/uploads/products/42e43bf6-4806-409a-a955-5a54d75b7fd7.jpg', b'0', b'0', 'Wingspan Expansions', NULL, 29.99, 0, 0, 'Estrategia', 25),
(39, b'1', 'Figuras', 0, 'http://localhost:8080/uploads/products/0327ffaa-775f-4e1f-88ef-a099cb63bc6d.jpg', b'0', b'0', 'Wolverine Bust', NULL, 89.99, 0, 0, 'Bustos', 25),
(40, b'1', 'TCG', 25, 'http://localhost:8080/uploads/products/2190702c-2c18-4a9b-8a52-564d7eeed88d.webp', b'0', b'0', 'DECK DE LAS CRÓNICAS: El Caído y El Virtuoso (Edición All-Foil)', NULL, 20, 0, 0, 'Yu-Gi-Oh', 20),
(42, b'1', 'Manga', 2, 'http://localhost:8080/uploads/products/1e87999f-9822-4873-abfb-48871bd13cc7.webp', b'0', b'0', 'JoJo\'s Bizarre Adventure Part 2: Battle Tendency (Digital Colored Comics)', NULL, 14, 0, 0, 'Shonen', 4),
(43, b'0', 'TCG', 0, 'http://localhost:8080/uploads/products/65e5f6d1-19e5-48f4-93b9-8e66aa9d4f0d.webp', b'0', b'1', 'EXTRA BOOSTER -ONE PIECE HEROINES EDITION- [EB-03]', NULL, 4.99, 0, 0, 'One Piece', 25),
(44, b'1', 'TCG', 0, 'http://localhost:8080/uploads/products/f2a52202-48f8-4fc9-9c30-0f78d7387255.webp', b'0', b'0', 'Mazo Dragon Ball', NULL, 10, 0, 0, 'Dragon Ball', 20),
(45, b'1', 'TCG', 5, 'http://localhost:8080/uploads/products/e8f66150-5999-4c62-8e91-cdb830483fa5.png', b'0', b'0', 'Digimon Liberator Pack', 5, 4.75, 0, 0, 'Digimon', 3),
(46, b'1', 'Manga', 0, 'http://localhost:8080/uploads/products/b3b066b5-f73b-4cc0-aa3b-fc7e31f90426.jpg', b'0', b'0', 'NINA DEL REINO DE LAS ESTRELLAS Nº12', NULL, 10, 0, 0, 'Josei', 15),
(47, b'1', 'Manga', 20, 'http://localhost:8080/uploads/products/e291f070-2e06-449e-a734-6608fe4f6e9e.jpg', b'0', b'0', 'Inazuma Eleven nº 01', 10, 8, 0, 0, 'Kodomo', 15),
(48, b'0', 'Comics', 0, 'http://localhost:8080/uploads/products/2c20970a-0a6d-42ad-bd6d-842209bb30a8.jpg', b'0', b'1', 'LA SABIDURIA DE LOS MITOS : GILGAMESH', NULL, 35, 0, 0, 'Independientes', 43),
(49, b'1', 'Comics', 0, 'http://localhost:8080/uploads/products/51fc2623-988b-49de-a7a4-ebaca23f69e6.webp', b'0', b'0', 'GIJOE 13', NULL, 20, 0, 0, 'Image', 0),
(50, b'1', 'Accesorios', 0, 'http://localhost:8080/uploads/products/bc27d723-2ceb-42fd-bbb9-dd3eb2ffa05b.webp', b'0', b'0', 'Carpeta Ultimate Guard Zipfolio Rojo (9 Bolsillos)', NULL, 32, 0, 0, 'Carpetas', 0),
(51, b'1', 'Juegos de mesa', 10, 'http://localhost:8080/uploads/products/4310529e-901f-4df2-958d-74353c98fdd3.jpg', b'0', b'0', 'Sillas. Juego de Mesa', 16.99, 15.29, 0, 0, 'Familiar', 14),
(52, b'1', 'Juegos de mesa', 0, 'http://localhost:8080/uploads/products/26498986-9ec6-4d50-aa42-54dcb160ec3e.webp', b'0', b'0', 'Los Hombres Lobo de Castronegro', NULL, 10.99, 0, 0, 'Rol', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `resenas`
--

CREATE TABLE `resenas` (
  `id` bigint(20) NOT NULL,
  `calificacion` int(11) NOT NULL,
  `contenido` varchar(255) DEFAULT NULL,
  `fecha_creacion` datetime(6) DEFAULT NULL,
  `producto_id` bigint(20) NOT NULL,
  `usuario_id` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reservas`
--

CREATE TABLE `reservas` (
  `id` bigint(20) NOT NULL,
  `estado` varchar(255) DEFAULT NULL,
  `fecha_reserva` datetime(6) DEFAULT NULL,
  `personas` int(11) DEFAULT NULL,
  `evento_id` bigint(20) NOT NULL,
  `usuario_id` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `reservas`
--

INSERT INTO `reservas` (`id`, `estado`, `fecha_reserva`, `personas`, `evento_id`, `usuario_id`) VALUES
(9, 'ACTIVA', '2026-02-04 21:01:01.603609', 4, 6, 'admin1'),
(10, 'ACTIVA', '2026-01-09 21:01:01.603609', 2, 6, 'usuario1'),
(11, 'ACTIVA', '2026-01-13 21:01:01.603609', 5, 7, 'usuario2'),
(12, 'ACTIVA', '2026-01-23 21:01:01.603609', 3, 7, 'usuario3'),
(13, 'ACTIVA', '2026-01-29 21:01:01.603609', 5, 8, 'usuario4'),
(14, 'ACTIVA', '2026-01-23 21:01:01.603609', 5, 8, 'usuario5'),
(15, 'CANCELADA', '2026-02-04 21:01:01.603609', 3, 9, 'admin1'),
(16, 'CANCELADA', '2026-02-02 21:01:01.603609', 2, 9, 'usuario1');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `subcategorias`
--

CREATE TABLE `subcategorias` (
  `id` bigint(20) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `categoria_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuarios`
--

CREATE TABLE `usuarios` (
  `uid` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `rol` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `calle` varchar(255) DEFAULT NULL,
  `ciudad` varchar(255) DEFAULT NULL,
  `codigoPostal` varchar(20) DEFAULT NULL,
  `pais` varchar(255) DEFAULT NULL,
  `codigo_postal` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuarios`
--

INSERT INTO `usuarios` (`uid`, `email`, `nombre`, `rol`, `telefono`, `calle`, `ciudad`, `codigoPostal`, `pais`, `codigo_postal`) VALUES
('admin1', 'admin@example.com', 'Administrador', 'ADMIN', NULL, NULL, NULL, NULL, NULL, NULL),
('CZ4qYn6poycoOTyQg1UCzkjIvoZ2', 'admin@rasengacomics.com', 'admin', 'USER', '+34 123 456 123', 'La calle', 'Jan', NULL, 'esp', '12345'),
('JrG7DYyFu6PbAzS2jFZcEKznYJC2', 'pacosans729q7@gmail.com', 'pacosans729q7', 'USER', NULL, NULL, NULL, NULL, NULL, NULL),
('usuario1', 'juan@example.com', 'Juan García', 'USER', NULL, NULL, NULL, NULL, NULL, NULL),
('usuario2', 'maria@example.com', 'María López', 'USER', NULL, NULL, NULL, NULL, NULL, NULL),
('usuario3', 'carlos@example.com', 'Carlos Rodríguez', 'USER', NULL, NULL, NULL, NULL, NULL, NULL),
('usuario4', 'ana@example.com', 'Ana Martínez', 'USER', NULL, NULL, NULL, NULL, NULL, NULL),
('usuario5', 'luis@example.com', 'Luis Fernández', 'USER', NULL, NULL, NULL, NULL, NULL, NULL);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `carritos`
--
ALTER TABLE `carritos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK1oqtem41uj4podo8a2lbsyyhm` (`usuario_id`);

--
-- Indices de la tabla `categorias`
--
ALTER TABLE `categorias`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `codigo_descuento`
--
ALTER TABLE `codigo_descuento`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKbmv6pxyvvhym29qs80kayrjqs` (`code`);

--
-- Indices de la tabla `detalle_pedido`
--
ALTER TABLE `detalle_pedido`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKg9h17fjynh9lgf1kbn0v9p4kf` (`pedido_id`),
  ADD KEY `FKdfdl21316mnac14d7f4oi4m84` (`producto_id`);

--
-- Indices de la tabla `evento`
--
ALTER TABLE `evento`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `favoritos`
--
ALTER TABLE `favoritos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKrxdxytf48cr005vej8540b0rr` (`producto_id`),
  ADD KEY `FKq9wif2hcqfxj8t49wo613wm0h` (`usuario_id`);

--
-- Indices de la tabla `home_carousel_config`
--
ALTER TABLE `home_carousel_config`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `item_carrito`
--
ALTER TABLE `item_carrito`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK8fr6xn504subiyopd73j7thy1` (`carrito_id`),
  ADD KEY `FK56pta5ak42wucf5fidc7b91fx` (`producto_id`);

--
-- Indices de la tabla `notificaciones`
--
ALTER TABLE `notificaciones`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK1mxbjb81ft61gwlh0kabubndc` (`usuario_id`);

--
-- Indices de la tabla `pedidos`
--
ALTER TABLE `pedidos`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FK5g0es69v35nmkmpi8uewbphs2` (`usuario_id`);

--
-- Indices de la tabla `productos`
--
ALTER TABLE `productos`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKpck3on5g40981yo4aspmmf1tk` (`source_product_id`);

--
-- Indices de la tabla `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `resenas`
--
ALTER TABLE `resenas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKey60d288h91q40q8qiagc871p` (`producto_id`),
  ADD KEY `FKp69qvliip9fbkmvh4k0bvlxwc` (`usuario_id`);

--
-- Indices de la tabla `reservas`
--
ALTER TABLE `reservas`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKfwakanxxdmkkhd6h855ui9le4` (`evento_id`),
  ADD KEY `FKcfh7qcr7oxomqk5hhbxdg2m7p` (`usuario_id`);

--
-- Indices de la tabla `subcategorias`
--
ALTER TABLE `subcategorias`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKiucm5ipf0wvec50s8j67r33rk` (`categoria_id`);

--
-- Indices de la tabla `usuarios`
--
ALTER TABLE `usuarios`
  ADD PRIMARY KEY (`uid`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `carritos`
--
ALTER TABLE `carritos`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `categorias`
--
ALTER TABLE `categorias`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `codigo_descuento`
--
ALTER TABLE `codigo_descuento`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT de la tabla `detalle_pedido`
--
ALTER TABLE `detalle_pedido`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT de la tabla `evento`
--
ALTER TABLE `evento`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=48;

--
-- AUTO_INCREMENT de la tabla `favoritos`
--
ALTER TABLE `favoritos`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `item_carrito`
--
ALTER TABLE `item_carrito`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `notificaciones`
--
ALTER TABLE `notificaciones`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `pedidos`
--
ALTER TABLE `pedidos`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT de la tabla `productos`
--
ALTER TABLE `productos`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- AUTO_INCREMENT de la tabla `products`
--
ALTER TABLE `products`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=53;

--
-- AUTO_INCREMENT de la tabla `resenas`
--
ALTER TABLE `resenas`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `reservas`
--
ALTER TABLE `reservas`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT de la tabla `subcategorias`
--
ALTER TABLE `subcategorias`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `carritos`
--
ALTER TABLE `carritos`
  ADD CONSTRAINT `FK1oqtem41uj4podo8a2lbsyyhm` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`uid`);

--
-- Filtros para la tabla `detalle_pedido`
--
ALTER TABLE `detalle_pedido`
  ADD CONSTRAINT `FKdfdl21316mnac14d7f4oi4m84` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`),
  ADD CONSTRAINT `FKg9h17fjynh9lgf1kbn0v9p4kf` FOREIGN KEY (`pedido_id`) REFERENCES `pedidos` (`id`);

--
-- Filtros para la tabla `favoritos`
--
ALTER TABLE `favoritos`
  ADD CONSTRAINT `FKq9wif2hcqfxj8t49wo613wm0h` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`uid`),
  ADD CONSTRAINT `FKrxdxytf48cr005vej8540b0rr` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`);

--
-- Filtros para la tabla `item_carrito`
--
ALTER TABLE `item_carrito`
  ADD CONSTRAINT `FK56pta5ak42wucf5fidc7b91fx` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`),
  ADD CONSTRAINT `FK8fr6xn504subiyopd73j7thy1` FOREIGN KEY (`carrito_id`) REFERENCES `carritos` (`id`);

--
-- Filtros para la tabla `notificaciones`
--
ALTER TABLE `notificaciones`
  ADD CONSTRAINT `FK1mxbjb81ft61gwlh0kabubndc` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`uid`);

--
-- Filtros para la tabla `pedidos`
--
ALTER TABLE `pedidos`
  ADD CONSTRAINT `FK5g0es69v35nmkmpi8uewbphs2` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`uid`);

--
-- Filtros para la tabla `resenas`
--
ALTER TABLE `resenas`
  ADD CONSTRAINT `FKey60d288h91q40q8qiagc871p` FOREIGN KEY (`producto_id`) REFERENCES `productos` (`id`),
  ADD CONSTRAINT `FKp69qvliip9fbkmvh4k0bvlxwc` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`uid`);

--
-- Filtros para la tabla `reservas`
--
ALTER TABLE `reservas`
  ADD CONSTRAINT `FKcfh7qcr7oxomqk5hhbxdg2m7p` FOREIGN KEY (`usuario_id`) REFERENCES `usuarios` (`uid`),
  ADD CONSTRAINT `FKfwakanxxdmkkhd6h855ui9le4` FOREIGN KEY (`evento_id`) REFERENCES `evento` (`id`);

--
-- Filtros para la tabla `subcategorias`
--
ALTER TABLE `subcategorias`
  ADD CONSTRAINT `FKiucm5ipf0wvec50s8j67r33rk` FOREIGN KEY (`categoria_id`) REFERENCES `categorias` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
