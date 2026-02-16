# Configuración de Webhook de Stripe

## 1. Instalar Stripe CLI

### Windows con Scoop:
```powershell
scoop bucket add stripe https://github.com/stripe/scoop-stripe-cli.git
scoop install stripe
```

### O descarga directa:
https://github.com/stripe/stripe-cli/releases/latest

## 2. Autenticarse con Stripe

```powershell
stripe login
```

Esto abrirá tu navegador para conectar con tu cuenta de Stripe.

## 3. Iniciar el listener de webhook

Con el backend corriendo en http://localhost:8080, ejecuta:

```powershell
stripe listen --forward-to localhost:8080/stripe/webhook
```

Este comando:
- ✅ Escucha eventos de webhook de Stripe
- ✅ Los reenvía a tu servidor local
- ✅ Te da un **webhook signing secret** (whsec_...)

## 4. Copiar el webhook secret

El comando anterior mostrará algo como:
```
> Ready! Your webhook signing secret is whsec_xxxxxxxxxxxxxxxxxxxxx
```

Copia ese secret y actualiza tu `application.properties`:

```properties
stripe.webhook.secret=whsec_xxxxxxxxxxxxxxxxxxxxx
```

## 5. Probar el webhook

En otra terminal, ejecuta:

```powershell
stripe trigger checkout.session.completed
```

Esto simula un pago completado y deberías ver:
- ✅ El evento llegando al listener
- ✅ Tu backend procesando el pedido
- ✅ El pedido guardado en la base de datos

## Comandos útiles

```powershell
# Ver eventos en tiempo real
stripe listen

# Simular eventos específicos
stripe trigger payment_intent.succeeded
stripe trigger checkout.session.completed

# Ver logs de eventos
stripe events list

# Ver un evento específico
stripe events retrieve evt_xxxxx
```

## Notas

- El webhook secret que genera `stripe listen` es SOLO para desarrollo local
- En producción, necesitarás configurar el webhook en el Dashboard de Stripe
- Cada vez que ejecutes `stripe listen` obtendrás un nuevo secret temporal
