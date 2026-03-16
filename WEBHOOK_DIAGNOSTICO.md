# Diagnóstico do Webhook de Stripe - Problemas encontrados y soluciones

## 🔍 Problemas Identificados

### 1. **Webhook no procesado durante compras previas**
- **Estado actual**: No hay pedidos (`pedidos`) en la base de datos
- **Causa probable**: El servicio `stripe listen` no estaba activado
- **Impacto**: Las compras realizadas anteriormente no se procesaron

### 2. **Configuración de Stripe Listen**
- **Requerimiento**: El comando `stripe listen --forward-to localhost:8080/stripe/webhook` debe estar ejecutándose
- **Estado**: ✅ Ya está ejecutándose ahora
- **Nota**: Este comando debe mantenerse activo en una terminal separada durante el desarrollo local

## ✅ Soluciones Implementadas

### 1. **Logging Mejorado**
Se agregó logging detallado en tres componentes:

#### a. `StripeWebhookController.java`
- Logs de recepción del webhook
- Logs de verificación de firma
- Logs de procesamiento de eventos

#### b. `StripeService.java`
- Logs detallados de `procesarCheckoutCompletado()`
- Logs de extracción de items de la sesión
- Logs de IDs de usuario y productos

#### c. `PedidoService.java`
- Logs de creación del pedido
- Logs de procesamiento de cada item
- Logs de actualización de stock
- Logs de guardado de detalles del pedido

### 2. **Servidor Backend**
- ✅ Compilado y ejecutándose en puerto `8080`
- ✅ Base de datos MariaDB conectada
- ✅ Controllers REST disponibles

## 📋 Pasos para Resolver

### Paso 1: Verificar que `stripe listen` está activo
```bash
stripe listen --forward-to localhost:8080/stripe/webhook
```
**Estado**: ✅ Ejecutándose

### Paso 2: Verificar credenciales de Stripe
En `application.properties`:
```properties
stripe.api.key=sk_test_51SyII7Rys0KojuGPJWpJTaTvYfzwLp9ij27YxRHMX4ZXdFiVowYOEh2gW4o0HXL9HpCCvnvniqMtx1gzUvjwG3UO00cLkk2EB1
stripe.webhook.secret=whsec_aed0ff0c12957022c4cb54e85b437055f9a83f2620f22867df81c53297455273
```
**Status**: ✅ Configuradas

### Paso 3: Iniciar el Frontend
Necesitas ejecutar el servidor de desarrollo de Vue.js:
```bash
cd frontend/vue-project
npm install  # Si no está hecho
npm run dev
```
**Status**: ⚠️ Requiere acción

### Paso 4: Hacer una compra de prueba
1. Abre http://localhost:5173 en tu navegador
2. Navega al catálogo
3. Añade productos al carrito
4. Procede al checkout
5. Usa tarjeta de prueba: `4242 4242 4242 4242`
6. Fecha y CVC: cualquiera futura/3 dígitos

### Paso 5: Verificar los logs
Debería ver logs en la consola del backend con el formato:
```
【WEBHOOK RECIBIDO】 ...
【SESSION VALIDA】 ...
【PROCESANDO CHECKOUT COMPLETADO】 ...
【USUARIO ENCONTRADO】 ...
【ITEM AGREGADO】 ...
【PEDIDO CREADO EXITOSAMENTE】 ...
```

### Paso 6: Revisar la base de datos
```sql
SELECT COUNT(*) FROM pedidos;
SELECT * FROM pedidos LIMIT 5;
SELECT * FROM detalles_pedido LIMIT 5;
```

### Paso 7: Verificar en el Frontend
- Ir a Perfil > Mis compras
- Los pedidos deberían aparecer aquí

## 🔧 Información Técnica

### Flujo del Webhook
```
1. Stripe → stripe listen (CLI)
2. stripe listen → localhost:8080/stripe/webhook
3. StripeWebhookController procesa el evento
4. StripeService.procesarCheckoutCompletado() extrae items
5. PedidoService.crearPedidoPagado() crea el pedido y detalles
6. Base de datos guarda: pedidos + detalles_pedido
7. Frontend obtiene pedidos vía GET /pedidos/usuario/{uid}
```

### Endpoints relevantes
- **Webhook**: `POST /stripe/webhook`
- **Obtener Pedidos**: `GET /pedidos/usuario/{uid}`
- **Listar Pedidos**: `GET /pedidos`
- **Obtener Pedido**: `GET /pedidos/{id}`

## 📊 Estado Actual

| Componente | Estado | Notas |
|-----------|--------|-------|
| Backend Spring Boot | ✅ Ejecutándose | Puerto 8080 |
| Base de datos MariaDB | ✅ Conectada | Tablas existen |
| Stripe CLI (`listen`) | ✅ Activo | En terminal separada |
| Frontend Vue.js | ⚠️ No ejecutándose | Necesita iniciar |
| Logging | ✅ Implementado | Con formato 【】 |
| Configuración Stripe | ✅ Completa | API key + Webhook secret |

## ⚠️ Próximas Acciones

1. **Inicia el Frontend** (CRÍTICO)
   ```bash
   cd frontend/vue-project
   npm run dev
   ```

2. **Monitorea los logs** en la consola del backend

3. **Haz una compra de prueba** y verifica que logs aparecen

4. **Revisa que el pedido se guardó** en la base de datos

5. **Verifica en el Frontend** que aparecen en "Mis compras"

## 🐛 Debugging

Si algo sigue sin funcionar:
1. Revisa los logs del servidor (【】)
2. Verifica que `stripe listen` muestra el webhook enviado
3. Revisa que la sesión tiene el `usuarioUid` en metadata
4. Verifica que el usuario existe en la tabla `usuarios`
5. Revisa que los productos existen en la tabla `products`

## 📝 Notas Importantes

- El webhook secret debe coincidir con el que proporciona `stripe listen`
- `stripe listen` debe mantenerse ejecutándose en una terminal durante el desarrollo
- El usuario debe estar autenticado cuando hace la compra, de lo contrario no se creará la sesión Stripe correctamente
- Los IDs de productos deben existir en la tabla `products`
