# Solución al Problema de Codificación de Caracteres Especiales (Tildes)

## Problema
Los caracteres especiales del español (tildes, ñ, etc.) aparecían corruptos en el frontend, mostrando símbolos como "??" en lugar de "ú", "í", etc.

Ejemplo: "múltiples categorías" se mostraba como "m??ltiples categor??as"

## Causa
El backend no estaba configurado para usar UTF-8 en:
1. Las respuestas HTTP
2. La conexión con la base de datos
3. El encoding de la aplicación web

## Solución Implementada

### 1. Configuración de WebConfig.java
Se agregó la configuración de UTF-8 para los convertidores HTTP:
- Implementa `WebMvcConfigurer`
- Configura `StringHttpMessageConverter` con UTF-8
- Desactiva el charset en headers Accept para evitar conflictos

### 2. Actualización de application.properties
Se añadieron las siguientes propiedades:

```properties
# Charset en la URL de conexión
spring.datasource.url=jdbc:mariadb://localhost:3307/rasengan_comics_database?useUnicode=true&characterEncoding=UTF-8

# Configuración de encoding del servlet
spring.servlet.encoding.charset=UTF-8
spring.servlet.encoding.enabled=true
spring.servlet.encoding.force=true

# Configuración de Hibernate
spring.jpa.properties.hibernate.connection.characterEncoding=utf8
spring.jpa.properties.hibernate.connection.CharSet=utf8
spring.jpa.properties.hibernate.connection.useUnicode=true
```

### 3. Scripts SQL para Base de Datos
Se crearon dos archivos SQL:
- `insert_eventos.sql` - Actualizado con comandos ALTER para UTF-8
- `reinsertar_eventos_utf8.sql` - Script completo para limpiar y reinsertar datos

## Pasos para Aplicar la Solución

### Opción 1: Reiniciar la aplicación (Recomendado si la base de datos está vacía)
1. Detener el servidor backend si está corriendo
2. Compilar el proyecto con Maven: `mvn clean package`
3. Iniciar el servidor backend
4. Los eventos se cargarán automáticamente con la codificación correcta

### Opción 2: Actualizar base de datos existente
Si ya tienes eventos en la base de datos con caracteres corruptos:

1. Abrir phpMyAdmin o el cliente MySQL/MariaDB
2. Seleccionar la base de datos `rasengan_comics_database`
3. Ejecutar el script `reinsertar_eventos_utf8.sql`
4. Reiniciar el servidor backend

### Opción 3: Desde línea de comandos
```bash
# Conectar a MariaDB
mysql -u root -p --default-character-set=utf8mb4

# Usar la base de datos
USE rasengan_comics_database;

# Ejecutar el script
SOURCE c:/Users/Alvaro/Documents/GitHub/rasengan-comic/backend/rasengaComics/reinsertar_eventos_utf8.sql
```

## Verificación
1. Reiniciar el servidor backend
2. Abrir el frontend en el navegador
3. Navegar a la sección de eventos
4. Los caracteres especiales deben mostrarse correctamente:
   - "múltiples" en lugar de "m??ltiples"
   - "categorías" en lugar de "categor??as"
   - "día" en lugar de "d??a"

## Archivos Modificados
- `WebConfig.java` - Configuración de charset HTTP
- `application.properties` - Propiedades de codificación
- `insert_eventos.sql` - Comandos ALTER para UTF-8
- `reinsertar_eventos_utf8.sql` - Script de limpieza y reinserción

## Notas Importantes
- Esta configuración afecta a todas las entidades, no solo eventos
- Los nuevos datos se guardarán automáticamente con UTF-8
- La base de datos se reconfigura con `utf8mb4` (soporte completo para emojis y caracteres especiales)
