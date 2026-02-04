-- Borrar eventos existentes
DELETE FROM evento;

-- Insertar nuevos eventos
INSERT INTO evento (nombre, descripcion, fecha_hora, ubicacion, imagen_url) VALUES
('Torneo TCG Magic: The Gathering', 'Competición oficial de Magic con premios para los 3 primeros puestos. Formato Standard. Inscripción limitada a 32 jugadores.', '2026-02-15 18:00:00', 'Sala Principal', 'https://images.unsplash.com/photo-1614680376573-df3480f0c6ff?w=400'),

('Taller de Dibujo Manga', 'Aprende las técnicas básicas del dibujo manga con un ilustrador profesional. Materiales incluidos. Todos los niveles bienvenidos.', '2026-02-22 17:00:00', 'Aula de Arte', 'https://images.unsplash.com/photo-1612036782180-6f0b6cd846fe?w=400'),

('Noche de Board Games', 'Velada de juegos de mesa con nuevos lanzamientos. Trae a tus amigos y descubre nuevos juegos. Torneos de Catan y Carcassonne.', '2026-02-18 19:00:00', 'Zona Gaming', 'https://images.unsplash.com/photo-1610890716171-6b1bb98ffd09?w=400'),

('Meet & Greet con Autores de Manga', 'Conoce a autores locales de manga, firma de ejemplares y sesión de preguntas. Edición limitada disponible para compra.', '2026-02-28 16:00:00', 'Entrada Principal', 'https://images.unsplash.com/photo-1618519764620-7403abdbdfe9?w=400'),

('Campeonato Pokémon TCG', 'Torneo oficial de Pokémon Trading Card Game. Formato Expanded. Premios: sobres, cartas promocionales y trofeos.', '2026-03-02 15:00:00', 'Sala Principal', 'https://images.unsplash.com/photo-1606503153255-59d7a5e5a1b5?w=400'),

('Screening Anime: Nuevos Estrenos', 'Proyección de los últimos episodios de animes populares en pantalla grande. Palomitas y bebidas incluidas.', '2026-02-20 20:00:00', 'Auditorio', 'https://images.unsplash.com/photo-1578632767115-351597cf2477?w=400'),

('Torneo Yu-Gi-Oh! Duel Masters', 'Competición de Yu-Gi-Oh con formato avanzado. Inscripción el mismo día. Premios para los 8 mejores.', '2026-03-08 17:00:00', 'Sala de Torneos', 'https://images.unsplash.com/photo-1628868445294-d0f4e2dd0ff5?w=400'),

('Workshop: Creación de Personajes', 'Taller de diseño y creación de personajes para cómics y manga. Técnicas de personalidad, diseño visual y storytelling.', '2026-03-05 18:30:00', 'Aula Creativa', 'https://images.unsplash.com/photo-1609743522653-52354461eb27?w=400'),

('Feria de Intercambio de Cartas', 'Trae tus cartas duplicadas y intercámbialas con otros coleccionistas. Magic, Pokémon, Yu-Gi-Oh y más.', '2026-02-25 16:00:00', 'Zona de Intercambios', 'https://images.unsplash.com/photo-1511512578047-dfb367046420?w=400'),

('Maratón de One Piece', 'Maratón de episodios especiales de One Piece. 6 horas de aventura con Luffy y la tripulación. Merchandising exclusivo.', '2026-03-10 14:00:00', 'Auditorio', 'https://images.unsplash.com/photo-1635863138275-d9b33299680b?w=400'),

('Torneo Dragon Ball Super Card Game', 'Competición oficial de Dragon Ball Super TCG. Formato estándar. Premios exclusivos de la serie.', '2026-03-12 16:00:00', 'Sala Principal', 'https://images.unsplash.com/photo-1643714332478-89616af39d39?w=400'),

('Cosplay Contest 2026', 'Concurso de cosplay con premios en múltiples categorías. Jurado profesional. Inscripciones hasta el día del evento.', '2026-03-15 18:00:00', 'Escenario Principal', 'https://images.unsplash.com/photo-1609743522653-52354461eb27?w=400');
