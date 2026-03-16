# Configuración de Stripe

## 1. Crear cuenta y obtener API Keys

### Acceder al Dashboard de Stripe
1. Ve a https://dashboard.stripe.com/
2. Crea una cuenta o inicia sesión
3. Cambia a **modo Test** (toggle en la esquina superior derecha)

### Obtener las claves API
1. Ve a **Developers > API keys**
2. Necesitas dos claves:
   - **Secret key** (comienza con `sk_test_...`)
   - **Publishable key** (comienza con `pk_test_...`)

## 2. Configurar Backend (application.properties)

Añade estas propiedades en `backend/rasengaComics/src/main/resources/application.properties`:

```properties
# Stripe API Key (Secret Key de Stripe)
stripe.api.key=sk_test_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

# Webhook Secret (se obtiene del CLI o del Dashboard)
stripe.webhook.secret=whsec_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx

# URLs de redirección después del pago
stripe.success-url=http://localhost:5173/perfil?checkout=success&tab=compras
stripe.cancel-url=http://localhost:5173/carrito?checkout=cancel
```

## 3. Configurar Frontend (Variables de Entorno)

Crea un archivo `.env` en `frontend/vue-project/`:

```env
# Publishable Key de Stripe
VITE_STRIPE_PUBLISHABLE_KEY=pk_test_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

## 4. Configurar Webhook (Desarrollo Local)

Para desarrollo local, usa Stripe CLI:

### Instalar Stripe CLI
```powershell
# Con Scoop
scoop bucket add stripe https://github.com/stripe/scoop-stripe-cli.git
scoop install stripe

# O descarga desde: https://github.com/stripe/stripe-cli/releases/latest
```

### Autenticarse
```powershell
stripe login
```

### Iniciar el listener
```powershell
stripe listen --forward-to localhost:8080/stripe/webhook
```

Esto te dará un **webhook signing secret** (empieza con `whsec_`). Cópialo y actualiza el `stripe.webhook.secret` en `application.properties`.

## 5. Configurar Webhook (Producción)

Cuando despliegues a producción:

1. Ve a **Developers > Webhooks** en el Dashboard de Stripe
2. Haz clic en **Add endpoint**
3. Configura:
   - **Endpoint URL**: `https://tu-dominio.com/stripe/webhook`
   - **Events to send**: Selecciona `checkout.session.completed`
4. Copia el **Signing secret** y actualízalo en tu servidor de producción

## 6. Productos en Stripe

No es necesario crear productos en Stripe. La aplicación crea dinámicamente los productos durante el checkout basándose en tu base de datos local.

## 7. Verificar la Configuración

### Backend
El endpoint de checkout está en:
```
POST http://localhost:8080/stripe/checkout-session
```

### Frontend
El componente `carritoComponent.vue` ya está configurado para:
- Cargar Stripe con la publishable key
- Crear sesiones de checkout
- Redirigir al checkout de Stripe

### Probar el Webhook
```powershell
stripe trigger checkout.session.completed
```

Deberías ver el evento procesado en tu backend y el pedido guardado en la base de datos.

## Resumen de Claves Necesarias

| Ubicación | Variable | Valor |
|-----------|----------|-------|
| Backend | `stripe.api.key` | Secret Key (`sk_test_...`) |
| Backend | `stripe.webhook.secret` | Webhook Secret (`whsec_...`) |
| Frontend | `VITE_STRIPE_PUBLISHABLE_KEY` | Publishable Key (`pk_test_...`) |

## Notas Importantes

- 🔴 Las claves `sk_test_` y `pk_test_` son para **modo prueba**
- 🟢 En producción, usa las claves reales: `sk_live_` y `pk_live_`
- 🔒 **NUNCA** expongas la Secret Key en el frontend
- 🔒 **NUNCA** subas las claves reales a GitHub
- 💳 Usa tarjetas de prueba de Stripe: `4242 4242 4242 4242`

## Tarjetas de Prueba

Para probar pagos en modo test:

- **Éxito**: 4242 4242 4242 4242
- **Requiere autenticación**: 4000 0025 0000 3155
- **Rechazada**: 4000 0000 0000 9995

Usa cualquier fecha futura y cualquier CVC de 3 dígitos.
