# Rasengan Comics

Aplicación web de **Rasengan Comics** con arquitectura separada:

- **Frontend**: Vue 3 + Vite + TypeScript + Pinia
- **Backend**: Spring Boot + Spring Security + JPA
- **Base de datos**: MariaDB

## Estructura del proyecto

```text
rasengan-comic/
├─ frontend/vue-project/      # Cliente Vue
├─ backend/rasengaComics/     # API REST Spring Boot
├─ copia de la base de datos/ # SQL de respaldo
└─ *.md                       # Documentación adicional (Stripe, webhook, etc.)
```

## Requisitos

- **Node.js**: `^20.19.0` o `>=22.12.0`
- **Java**: `21`
- **Maven** (o usar `mvnw`/`mvnw.cmd`)
- **MariaDB** corriendo en `localhost:3307`

## Configuración base

El backend usa, por defecto, esta conexión (ver `backend/rasengaComics/src/main/resources/application.properties`):

- URL: `jdbc:mariadb://localhost:3307/rasengan_comics_database`
- Usuario: `root`
- Password: vacío
- Puerto backend: `8080`
- CORS permitido: `http://localhost:5173`

## Levantar el proyecto en local

### 1) Backend (Spring Boot)

Desde `backend/rasengaComics`:

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

Backend disponible en: `http://localhost:8080`

### 2) Frontend (Vue)

Desde `frontend/vue-project`:

```bash
npm install
npm run dev
```

Frontend disponible en: `http://localhost:5173`

## Usuarios de prueba

Puedes iniciar sesión con cualquiera de estos usuarios:

- **Admin**
	- Email: `admin@rasengacomics.com`
	- Password: `123456`

- **Usuario**
	- Email: `usuario@rasengacomics.com`
	- Password: `123456`

## Scripts útiles (frontend)

Desde `frontend/vue-project`:

- `npm run dev` → servidor de desarrollo
- `npm run build` → build de producción
- `npm run preview` → previsualizar build
- `npm run lint` → lint con autofix
- `npm run format` → formatear `src/`

## Documentación adicional

Para configuración de pagos y webhook revisa:

- `STRIPE_SETUP.md`
- `WEBHOOK_SETUP.md`
- `WEBHOOK_DIAGNOSTICO.md`
- `test-stripe.http`