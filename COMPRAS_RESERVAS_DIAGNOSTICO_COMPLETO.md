# 📋 Diagnóstico Completo - Problema de Compras y Reservas No Visibles

## 🔴 Problema Reportado
**"Hice una compra con Stripe pero no se ve ningun producto en mis compras ni en mis reservas"**

---

## 🔍 Investigación y Hallazgos

### 1. **Causa Raíz Identificada** 

#### Problema 1: Webhook No Se Estaba Procesando
- **Síntoma**: `Event dataObjectDeserializer().getObject()` retornaba `null`
- **Impacto**: Aunque Stripe enviaba el evento `checkout.session.completed`, la sesión no se extraía correctamente
- **Evidencia en logs**:
  ```
  【ERROR】 Session es null
  ```

#### Problema 2: Stripe CLI Listener No Activo
- **Síntoma**: Sin `stripe listen` ejecutándose, los webhooks no se reenvían a `localhost:8080`
- **Impacto**: Compras pagadas en Stripe no se procesaban en el servidor local
- **Solución**: Mantener `stripe listen --forward-to localhost:8080/stripe/webhook` activo

### 2. **Flujo Identificado en el Código**

#### Frontend (perfilComponent.vue y reservasComponent.vue)
```
Compra → Checkout Stripe → Redirección → GET /pedidos/usuario/{uid}
                                            ↓
                                      [Obtiene lista de pedidos]
                                            ↓
                                      [Filtra items por `item.reserva`]
                                            ↓
                                      [Muestra en "Mis compras" o "Mis reservas"]
```

#### Backend
```
usuario cliente --POST--> /stripe/checkout-session
                            ↓
                         StripeCheckoutRequest (items + usuarioUid)
                            ↓
                         [Crea sesión en Stripe]
                            ↓
                         [Usuario paga]
                            ↓
                Stripe --Webhook--> stripe listen 
                                      ↓
                         localhost:8080/stripe/webhook
                                      ↓
                     StripeWebhookController.handleStripeWebhook()
                                      ↓
                     StripeService.procesarCheckoutCompletado()
                                      ↓
                     PedidoService.crearPedidoPagado()
                                      ↓
                         [Guarda:  pedidos + detalles_pedido]
                                      ↓
                         [Usuario ve en Perfil → Mis compras]
```

---

## ✅ Soluciones Implementadas

### 1. **Arreglado: Extracción de Sesión del Webhook**

**Archivo**: `StripeWebhookController.java`

**Cambio**:
```java
// ANTES (No funcionaba):
Session session = (Session) event.getDataObjectDeserializer()
        .getObject()
        .orElse(null);

// DESPUÉS (Correcto):
Session session = (Session) event.getDataObjectDeserializer().deserializeUnsafe();
```

**Razón**: El método `getObject()` no retornaba un objeto parcialmente deserializado. Usar `deserializeUnsafe()` permite deserializar completamente cualquier objeto Stripe.

### 2. **Mejorado: Logging Comprehensivo**

Se agregó logging detallado en tres capas:

#### a) `StripeWebhookController.java`
```java
【WEBHOOK RECIBIDO】 Payload length: XXXX
【EVENT VERIFICADO】 Type: checkout.session.completed
【PROCESANDO CHECKOUT COMPLETADO】
【SESSION VALIDA】 SessionId: cs_test_123...
【PEDIDO CREADO EXITOSAMENTE】
```

#### b) `StripeService.java`
```java
【PROCESANDO CHECKOUT】 SessionId: cs_test_123...
【USUARIO UID】 usuario_firebase_uid
【LINE ITEMS】 Total items: 2
【PRODUCTO ID】 123
【ITEM AGREGADO】 ProductoId: 123, Cantidad: 1
【TOTAL ITEMS PROCESADOS】 2
【CREANDO PEDIDO】
【PEDIDO CREADO EXITOSAMENTE】
```

#### c) `PedidoService.java`
```java
【CREAR PEDIDO PAGADO】 usuarioUid: ..., items: 2
【USUARIO ENCONTRADO】 Juan Pérez (uid)
【PEDIDO GUARDADO】 ID: 5
【PROCESANDO ITEM】 ProductoId: 123, Cantidad: 1
【PRODUCTO ENCONTRADO】 Comic X Vol. 1
【STOCK ACTUAL】 10, Solicitado: 1
【STOCK ACTUALIZADO】 Nuevo stock: 9
【PEDIDO FINALIZADO】 ID: 5, Total: 15.99
```

### 3. **Recompilado: Backend con Fixes**

```bash
mvn clean package -DskipTests
# Generó: rasengaComics-0.0.1-SNAPSHOT.jar
```

### 4. **Configurado: Sistema Completo Ejecutándose**

**Terminals Activas**:

1. **Backend** (Puerto 8080)
   ```bash
   java -jar target/rasengaComics-0.0.1-SNAPSHOT.jar
   ```
   - ✅ Servidor levantado correctamente
   - ✅ Base de datos conectada (MariaDB localhost:3307)
   - ✅ Endpoints disponibles

2. **Frontend** (Puerto 5174)
   ```bash
   cd frontend/vue-project
   npm run dev
   ```
   - ✅ Accesible en http://localhost:5174

3. **Stripe CLI** (Listener)
   ```bash
   stripe listen --forward-to localhost:8080/stripe/webhook
   ```
   - ✅ Activo y escuchando webhooks

---

## 📊 Estructura de la Base de Datos

### Tabla: `pedidos`
```sql
CREATE TABLE pedidos (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  usuario_id VARCHAR(255) NOT NULL,
  fecha_pedido DATETIME(6),
  estado VARCHAR(255),           -- 'PAGADO', 'COMPLETADO', etc.
  total DOUBLE,
  stripe_session_id VARCHAR(255),
  stripe_payment_intent_id VARCHAR(255),
  FOREIGN KEY (usuario_id) REFERENCES usuarios(uid)
);
```

### Tabla: `detalles_pedido`
```sql
CREATE TABLE detalles_pedido (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  pedido_id BIGINT NOT NULL,
  producto_id BIGINT NOT NULL,
  cantidad INT,
  precio_unitario DOUBLE,
  FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
  FOREIGN KEY (producto_id) REFERENCES productos(id)
);
```

### Tabla: `products` (Tabla con is_reserve flag)
```sql
CREATE TABLE products (
  id BIGINT PRIMARY KEY,
  name VARCHAR(255),
  price DOUBLE,
  stock INT,
  is_reserve BOOLEAN,          -- Determina si va a "Mis compras" o "Mis reservas"
  category VARCHAR(255),
  subcategory VARCHAR(255),
  ...
);
```

---

## 🔄 Flujo Correcto (Post-Fix)

### En Mis Compras:
1. Usuario hace compra (producto con `is_reserve = false`)
2. Paga con Stripe (`4242 4242 4242 4242`)
3. Webhook se procesa → `PedidoService.crearPedidoPagado()`
4. Se crea `Pedido` + `DetallePedido` en BD
5. Frontend hace GET `/pedidos/usuario/{uid}`
6. Filtra items donde `item.reserva = false`
7. **Aparece en "Mis compras"** ✅

### En Mis Reservas:
1. Usuario hace compra (producto con `is_reserve = true`)
2. Paga con Stripe  
3. Webhook se procesa → `PedidoService.crearPedidoPagado()`
4. Se crea `Pedido` + `DetallePedido` en BD
5. Frontend hace GET `/pedidos/usuario/{uid}`
6. Filtra items donde `item.reserva = true`
7. **Aparece en "Mis reservas"** ✅

---

## 🧪 Pasos para Verificar que Funciona

### Paso 1: Confirmar que los Servidores están activos
```bash
# Backend
curl http://localhost:8080/pedidos

# Frontend
# Abre http://localhost:5174 en navegador
```

### Paso 2: Verificar las Credenciales de Stripe
```properties
# En: backend/rasengaComics/src/main/resources/application.properties

stripe.api.key=sk_test_51SyII7Rys0KojuGPJWpJTaTvYfzwLp9ij27YxRHMX4ZXdFiVowYOEh2gW4o0HXL9HpCCvnvniqMtx1gzUvjwG3UO00cLkk2EB1
stripe.webhook.secret=whsec_aed0ff0c12957022c4cb54e85b437055f9a83f2620f22867df81c53297455273
```

### Paso 3: Hacer una Compra de Prueba

1. **Accede al frontend**: http://localhost:5174
2. **Inicia sesión** (si es requerido)
3. **Navega a Catálogo/Tienda**
4. **Añade un producto al carrito**
   - Si es "compra" (no reserva): irá a "Mis compras"
   - Si es "reserva" (checklist marcado): irá a "Mis reservas"
5. **Procede al checkout**
6. **Usa tarjeta de prueba**: `4242 4242 4242 4242`
   - **Fecha**: Cualquiera en el futuro (ej: 12/25)
   - **CVC**: Cualquiera (ej: 123)
7. **Confirma el pago**

### Paso 4: Verificar en el Backend

**Observa los logs en la consola del servidor**:

```
【WEBHOOK RECIBIDO】 Payload length: 3783
【EVENT VERIFICADO】 Type: checkout.session.completed
【PROCESANDO CHECKOUT COMPLETADO】
【SESSION VALIDA】 SessionId: cs_test_a1b2c3d4e5f6
【USUARIO ENCONTRADO】 Juan Pérez (uid123)
【ITEM AGREGADO】 ProductoId: 1, Cantidad: 1
【PEDIDO CREADO EXITOSAMENTE】
```

Si **NO ves estos logs**, y en cambio ves:
```
【ERROR】 Session es null
```

Significa que el deserializar falló. Revisa los imports y la sintaxis.

### Paso 5: Verificar en la Base de Datos

```sql
-- Conectar a MariaDB
mysql -h localhost -P 3307 -u root rasengan_comics_database

-- Ver pedidos creados
SELECT id, usuario_id, estado, total FROM pedidos;

-- Ver detalles de los pedidos
SELECT pedido_id, producto_id, cantidad, precio_unitario FROM detalles_pedido;
```

Expected output:
```
+----+-----------+---------+--------+
| id | usuario_id| estado  | total  |
+----+-----------+---------+--------+
| 1  | uid123    | PAGADO  | 19.99  |
+----+-----------+---------+--------+
```

### Paso 6: Verificar en el Frontend

1. **Ve a Perfil**
2. **Click en "Mis compras"** o **"Mis reservas"**
3. **Deberías ver tu pedido** ✅

---

## 🐛 Troubleshooting

### Problema: "Sigo sin ver mis compras"

#### 1. Verificar que `stripe listen` está activo
```bash
# En otra terminal
stripe listen --forward-to localhost:8080/stripe/webhook
# Deberías ver:
# 🔌 Webhook signing secret for acct_xxxx: whsec_aaa...
# Ready! Your webhook signing secret is whsec_aaa...
```

#### 2. Verificar que el webhook secret coincide
```properties
# application.properties debe usar el secreto de stripe listen
stripe.webhook.secret=whsec_aaa...  # <-- Actualizar si no coincide
```

#### 3. Verificar los logs del servidor
```
Busca líneas que empiecen con:
【WEBHOOK RECIBIDO】 - El webhook llegó
【SESSION VALIDA】   - Se procesó correctamente
【PEDIDO CREADO】    - Se creó en la BD
```

#### 4. Verificar que el usuario está autenticado
```javascript
// En la consola del navegador:
console.log(authStore.user)
// Deberías ver un objeto con uid, email, etc.
```

#### 5. Verificar que el producto existe en la BD
```sql
SELECT id, name, is_reserve FROM products WHERE id = 1;
```

---

## 📝 Cambios Realizados

| Archivo | Cambio | Estado |
|---------|--------|--------|
| `StripeWebhookController.java` | Arreglado deserializado de sesión | ✅ |
| `StripeService.java` | Agregado logging detallado | ✅ |
| `PedidoService.java` | Agregado logging detallado | ✅ |
| `pom.xml` | Dependencias SLF4J (logging) | ✅ |

---

## 🎯 Próximas Pruebas

1. **Hacer una compra normal** (producto con `is_reserve = false`)
   - Verificar en "Mis compras"

2. **Hacer una reserva** (producto con `is_reserve = true`)
   - Verificar en "Mis reservas"

3. **Verificar actualización de stock**
   ```sql
   SELECT id, name, stock FROM products WHERE id = 1;
   -- El stock debería decrementar después de la compra
   ```

4. **Revisar detalles del pedido**
   - Click en "Ver detalles" debería mostrar los items

---

## ⚠️ Notas Importantes

- **`stripe listen` debe estar activo todo el tiempo durante desarrollo**
- **El website_secret se obtiene de `stripe listen`, no del dashboard**
- **Los cambios del source requieren recompilar**: `mvn clean package`
- **El servidor debe reiniciar**: `java -jar target/rasengaComics-0.0.1-SNAPSHOT.jar`
- **Limpiar el caché del navegador si algo sigue sin funcionar**: Ctrl+Shift+Del

---

## 📞 Resumen Ejecutivo

**Problema**: Webhook de Stripe no se estaba procesando correctamente

**Causa**: 
- `event.getDataObjectDeserializer().getObject()` retornaba null
- `stripe listen` no estaba activo

**Solución**:
1. Cambiar a `event.getDataObjectDeserializer().deserializeUnsafe()`
2. Iniciar `stripe listen --forward-to localhost:8080/stripe/webhook`
3. Agregar logging para diagnosticar futuros problemas
4. Recompilar el JAR
5. Reiniciar el servidor

**Estado**: ✅ LISTO PARA PROBAR
