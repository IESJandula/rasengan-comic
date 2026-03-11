<template>
  <div class="cart-container">
    <div class="cart-wrapper">
      <h1 class="cart-title">Carrito de Compras</h1>

      <!-- Carrito Vacío -->
      <div v-if="cartItems.length === 0" class="empty-cart">
        <p class="empty-message">Tu carrito está vacío</p>
        <router-link to="/catalogo" class="continue-shopping">
          Continuar Comprando
        </router-link>
      </div>

      <!-- Carrito con Productos -->
      <div v-else class="cart-content">
        <!-- Tabla de Productos -->
        <div class="cart-items">
          <div class="cart-header">
            <span class="col-product">Producto</span>
            <span class="col-price">Precio</span>
            <span class="col-quantity">Cantidad</span>
            <span class="col-total">Total</span>
            <span class="col-action">Acción</span>
          </div>

          <div v-for="item in cartItems" :key="item.id" class="cart-item">
            <div class="col-product">
              <img :src="item.image" :alt="item.name" class="item-image" />
              <div class="item-info">
                <h4 class="item-name">{{ item.name }}</h4>
                <p class="item-category">{{ item.category }}</p>
              </div>
            </div>
            <div class="col-price">{{ item.price.toFixed(2) }}€</div>
            <div class="col-quantity">
              <button @click="decrementQuantity(item.id)" class="qty-btn">-</button>
              <input v-model.number="item.quantity" type="number" min="1" />
              <button @click="incrementQuantity(item.id)" class="qty-btn">+</button>
            </div>
            <div class="col-total">{{ (item.price * item.quantity).toFixed(2) }}€</div>
            <div class="col-action">
              <button @click="removeItem(item.id)" class="remove-btn">🗑️</button>
            </div>
          </div>
        </div>

        <!-- Resumen -->
        <div class="cart-summary">
          <h2>Resumen del Pedido</h2>

          <!-- Método de Entrega (solo si está logueado) -->
          <div v-if="authStore.isAuthenticated" class="delivery-section">
            <h3 class="delivery-title">Método de Entrega</h3>
            <select v-model="deliveryMethod" class="delivery-select">
              <option value="envio">📦 Envío a domicilio</option>
              <option value="tienda">🏪 Recogida en tienda</option>
            </select>

            <!-- Dirección de envío -->
            <div v-if="deliveryMethod === 'envio'" class="address-section">
              <div v-if="userAddress" class="address-card-small">
                <p class="address-label">📍 Dirección de envío:</p>
                <p class="address-text">{{ userAddress.street }}</p>
                <p class="address-text">{{ userAddress.city }}, {{ userAddress.zipCode }}</p>
                <p class="address-text">{{ userAddress.country }}</p>
                <router-link to="/perfil" class="edit-address-link">Editar dirección</router-link>
              </div>
              <div v-else class="no-address-warning">
                <p>⚠️ No tienes dirección registrada</p>
                <router-link to="/perfil" class="add-address-link">Añadir dirección</router-link>
              </div>
            </div>

            <!-- Info de recogida en tienda -->
            <div v-else class="store-pickup-info">
              <p class="store-info">📍 Calle Ejemplo 123, Madrid</p>
              <p class="store-info">⏰ Lun-Vie: 10:00-20:00, Sáb: 10:00-14:00</p>
            </div>
          </div>

          <div class="summary-item">
            <span>Subtotal</span>
            <span>{{ subtotal.toFixed(2) }}€</span>
          </div>

          <div class="summary-item">
            <span>IVA</span>
            <span>{{ taxes.toFixed(2) }}€</span>
          </div>

          <div class="summary-item">
            <span>Envío</span>
            <span v-if="deliveryMethod === 'tienda'" class="free-shipping">Gratis (Recogida en tienda)</span>
            <span v-else-if="subtotal > 50" class="free-shipping">Gratis</span>
            <span v-else>{{ calculatedShipping.toFixed(2) }}€</span>
          </div>

          <div class="summary-divider"></div>

          <div class="summary-total">
            <span>Total</span>
            <span>{{ calculatedTotal.toFixed(2) }}€</span>
          </div>

          <div class="promo-code">
            <input v-model.trim="promoCode" placeholder="Código promocional" class="promo-code-input" />
            <button @click="applyPromo" class="apply-btn" :disabled="applyingPromo">
              {{ applyingPromo ? 'Aplicando...' : 'Aplicar' }}
            </button>
          </div>

          <div v-if="appliedDiscountAmount > 0" class="summary-item summary-discount">
            <span>Descuento ({{ appliedDiscountCode }})</span>
            <span>-{{ appliedDiscountAmount.toFixed(2) }}€</span>
          </div>

          <button 
            @click="checkout" 
            class="checkout-btn"
            :disabled="isCheckoutDisabled"
            :class="{ 'disabled': isCheckoutDisabled }"
          >
            {{ checkoutButtonText }}
          </button>

          <router-link to="/catalogo" class="continue-shopping-link">
            Continuar Comprando
          </router-link>
        </div>
      </div>

      <!-- Modal de Autenticación Requerida -->
      <div v-if="showAuthModal" class="auth-modal-overlay">
        <div class="auth-modal">
          <button @click="closeAuthModal" class="modal-close-btn">✕</button>
          
          <div class="modal-content">
            <div class="modal-icon">🔐</div>
            <h2>Inicia sesión para continuar</h2>
            <p>Necesitas tener una cuenta activa para realizar tu compra.</p>
            
            <div class="modal-buttons">
              <button @click="goToLogin" class="btn-login-modal">
                Iniciar Sesión
              </button>
              <button @click="closeAuthModal" class="btn-cancel">
                Cancelar
              </button>
            </div>

            <p class="modal-register-text">
              ¿No tienes cuenta? <router-link to="/registro" @click="showAuthModal = false">Regístrate aquí</router-link>
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, watch } from 'vue'
import { useCartStore } from '@/stores/cartStore'
import { useAuthStore } from '@/stores/authStore'
import { useRouter } from 'vue-router'
import { loadStripe } from '@stripe/stripe-js'
import api from '@/api/axios'

const cartStore = useCartStore()
const authStore = useAuthStore()
const router = useRouter()

// Usar los valores del store directamente (son computed)
const cartItems = computed(() => cartStore.items)
const subtotal = computed(() => cartStore.subtotal)
const shipping = computed(() => cartStore.shipping)
const taxes = computed(() => cartStore.taxes)
const total = computed(() => cartStore.total)

const promoCode = ref('')
const appliedDiscountAmount = ref(0)
const appliedDiscountCode = ref('')
const applyingPromo = ref(false)
const showAuthModal = ref(false)
const stripePromise = loadStripe(import.meta.env.VITE_STRIPE_PUBLISHABLE_KEY)
const deliveryMethod = ref<'envio' | 'tienda'>('envio')

// Dirección del usuario (ahora cargada desde el backend)
const userAddress = ref<{
  street: string
  city: string
  zipCode: string
  country: string
} | null>(null)

// Cargar dirección del usuario desde el backend
const loadUserAddress = async () => {
  if (!authStore.isAuthenticated || !authStore.user?.uid) {
    userAddress.value = null
    return
  }
  
  try {
    const response = await api.get(`/usuarios/${authStore.user.uid}`)
    const userData = response.data
    
    // Solo cargar dirección si todos los campos están completos
    if (userData.calle && userData.ciudad && userData.codigoPostal && userData.pais) {
      userAddress.value = {
        street: userData.calle,
        city: userData.ciudad,
        zipCode: userData.codigoPostal,
        country: userData.pais
      }
    } else {
      userAddress.value = null
    }
  } catch (error) {
    console.error('Error al cargar dirección del usuario:', error)
    userAddress.value = null
  }
}

// Cargar dirección cuando el componente se monta
onMounted(() => {
  if (authStore.isAuthenticated) {
    loadUserAddress()
  }
})

// Observar cambios en la autenticación
watch(() => authStore.isAuthenticated, (isAuthenticated) => {
  if (isAuthenticated) {
    loadUserAddress()
  } else {
    userAddress.value = null
  }
})

// Calcular envío según el método de entrega
const calculatedShipping = computed(() => {
  if (deliveryMethod.value === 'tienda') {
    return 0
  }
  if (subtotal.value > 50) {
    return 0
  }
  return shipping.value
})

// Calcular total con el envío correcto
const calculatedTotal = computed(() => {
  return Math.max(0, subtotal.value + taxes.value + calculatedShipping.value - appliedDiscountAmount.value)
})

// Validar si se puede proceder al pago
const isCheckoutDisabled = computed(() => {
  if (!authStore.isAuthenticated) return false // Mostrará el modal
  if (deliveryMethod.value === 'envio' && !userAddress.value) return true
  if (cartItems.value.length === 0) return true
  return false
})

// Texto del botón de pago
const checkoutButtonText = computed(() => {
  if (!authStore.isAuthenticated) return 'Ir a Pagar'
  if (deliveryMethod.value === 'envio' && !userAddress.value) {
    return 'Añade una dirección para continuar'
  }
  return 'Ir a Pagar'
})

const incrementQuantity = (itemId: number) => {
  cartStore.incrementQuantity(itemId)
}

const decrementQuantity = (itemId: number) => {
  cartStore.decrementQuantity(itemId)
}

const removeItem = (itemId: number) => {
  cartStore.removeItem(itemId)
}

const applyPromo = async () => {
  const normalizedCode = promoCode.value.trim().toUpperCase()

  if (!normalizedCode) {
    alert('Introduce un código promocional')
    return
  }

  if (subtotal.value <= 0) {
    alert('El carrito está vacío')
    return
  }

  applyingPromo.value = true

  try {
    const response = await api.post('/api/discounts/aplicar', null, {
      params: {
        codigo: normalizedCode,
        precio: subtotal.value
      }
    })

    const body = response.data
    if (!body?.success) {
      appliedDiscountAmount.value = 0
      appliedDiscountCode.value = ''
      alert(body?.message || 'Código promocional inválido')
      return
    }

    const discountAmount = Number(body?.data || 0)
    if (discountAmount <= 0) {
      appliedDiscountAmount.value = 0
      appliedDiscountCode.value = ''
      alert('El código no aplica descuento para este carrito')
      return
    }

    appliedDiscountAmount.value = Math.min(discountAmount, subtotal.value + taxes.value + calculatedShipping.value)
    appliedDiscountCode.value = normalizedCode
    promoCode.value = normalizedCode
    alert(`Código aplicado correctamente: ${normalizedCode}`)
  } catch (err: any) {
    appliedDiscountAmount.value = 0
    appliedDiscountCode.value = ''
    const errorMsg = err.response?.data?.message || 'Código promocional inválido'
    alert(errorMsg)
  } finally {
    applyingPromo.value = false
  }
}

watch([subtotal, taxes, calculatedShipping], () => {
  if (appliedDiscountAmount.value > 0) {
    appliedDiscountAmount.value = 0
    appliedDiscountCode.value = ''
  }
})

const checkout = async () => {
  console.log('🚀 Iniciando checkout...')
  console.log('Usuario autenticado:', authStore.isAuthenticated)
  console.log('Usuario data:', authStore.user)
  
  if (!authStore.isAuthenticated) {
    console.log('❌ Usuario no autenticado - mostrando modal')
    showAuthModal.value = true
    return
  }

  // Validar dirección si el método es envío
  if (deliveryMethod.value === 'envio' && !userAddress.value) {
    alert('Por favor, añade una dirección de envío en tu perfil antes de continuar.')
    router.push('/perfil')
    return
  }

  if (!authStore.user?.uid) {
    console.error('❌ No se encontró el UID del usuario')
    alert('No se pudo identificar el usuario')
    return
  }

  if (cartItems.value.length === 0) {
    alert('El carrito está vacío')
    return
  }

  console.log('✅ Usuario válido, preparando payload...')
  console.log('📦 Items en el carrito:', cartItems.value.map(item => ({ id: item.id, name: item.name, quantity: item.quantity })))
  console.log('🚚 Método de entrega:', deliveryMethod.value)

  try {
    // Validar que todos los productos existen antes de enviar
    console.log('🔍 Validando productos en el servidor...')
    const productIds = cartItems.value.map(item => item.id)
    const validationResponse = await api.get('/api/products')
    const validProductIds = new Set(validationResponse.data.map((p: any) => p.id))
    
    const invalidProducts = productIds.filter(id => !validProductIds.has(id))
    if (invalidProducts.length > 0) {
      alert(`Error: Los siguientes productos ya no están disponibles: ${invalidProducts.join(', ')}. Por favor, actualiza tu carrito.`)
      // Limpiar productos inválidos del carrito
      cartItems.value.forEach(item => {
        if (invalidProducts.includes(item.id)) {
          cartStore.removeItem(item.id)
        }
      })
      return
    }

    const payload = {
      usuarioUid: authStore.user.uid,
      usuarioEmail: authStore.user.email,
      usuarioNombre: authStore.user.name,
      metodoEntrega: deliveryMethod.value,
      direccionEnvio: deliveryMethod.value === 'envio' ? userAddress.value : null,
      items: cartItems.value.map((item) => ({
        productoId: item.id,
        cantidad: item.quantity
      }))
    }

    console.log('📦 Payload a enviar:', payload)
    console.log('🌐 Enviando petición a /stripe/checkout-session...')

    const response = await api.post('/stripe/checkout-session', payload)
    
    console.log('📨 Respuesta del servidor:', response.data)
    const { sessionId } = response.data

    if (!sessionId) {
      console.error('❌ No se recibió sessionId del servidor')
      alert('No se pudo iniciar el pago - Sin session ID')
      return
    }

    // Fallback: si el webhook de Stripe falla, confirmar esta sesión al volver del checkout.
    localStorage.setItem('pendingStripeSessionId', sessionId)

    console.log('🔑 SessionId recibido:', sessionId)
    console.log('💳 Cargando Stripe...')

    const stripe = await stripePromise
    
    if (!stripe) {
      console.error('❌ No se pudo cargar Stripe')
      alert('No se pudo iniciar el pago - Error al cargar Stripe')
      return
    }

    console.log('✅ Stripe cargado, redirigiendo al checkout...')
    await stripe.redirectToCheckout({ sessionId })
  } catch (err: any) {
    console.error('❌ Error al iniciar el pago:', err)
    console.error('Error completo:', err.response?.data || err.message)
    
    if (err.response?.data?.message) {
      alert(`No se pudo iniciar el pago: ${err.response.data.message}`)
    } else if (err.message) {
      alert(`No se pudo iniciar el pago: ${err.message}`)
    } else {
      alert('No se pudo iniciar el pago. Por favor, intenta nuevamente.')
    }
  }
}

const goToLogin = () => {
  router.push('/login')
  showAuthModal.value = false
}

const closeAuthModal = () => {
  showAuthModal.value = false
}
</script>

<style scoped>
.cart-container {
  background-color: #f9fafb;
  min-height: 100vh;
  padding: 40px 20px;
}

.cart-wrapper {
  max-width: 1200px;
  margin: 0 auto;
}

.cart-title {
  font-size: 32px;
  font-weight: bold;
  color: #1f2937;
  margin: 0 0 30px 0;
}

.empty-cart {
  text-align: center;
  padding: 60px 20px;
  background-color: white;
  border-radius: 12px;
}

.empty-message {
  color: #6b7280;
  font-size: 18px;
  margin-bottom: 20px;
}

.empty-message {
  color: #6b7280;
  font-size: 16px;
}

/* Modal de Autenticación */
.auth-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.auth-modal {
  background: white;
  border-radius: 12px;
  padding: 40px;
  max-width: 450px;
  width: 90%;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  position: relative;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    transform: translateY(30px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-close-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #9ca3af;
  transition: color 0.3s ease;
}

.modal-close-btn:hover {
  color: #374151;
}

.modal-content {
  text-align: center;
}

.modal-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.auth-modal h2 {
  font-size: 24px;
  font-weight: bold;
  color: #1f2937;
  margin: 0 0 10px 0;
}

.auth-modal p {
  color: #6b7280;
  font-size: 14px;
  margin: 0 0 25px 0;
  line-height: 1.5;
}

.modal-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 20px;
}

.btn-login-modal {
  padding: 14px;
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-login-modal:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(220, 38, 38, 0.3);
}

.btn-cancel {
  padding: 14px;
  background-color: #f3f4f6;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  color: #374151;
  transition: all 0.3s ease;
}

.btn-cancel:hover {
  background-color: #e5e7eb;
}

.modal-register-text {
  color: #6b7280;
  font-size: 13px;
  margin: 0;
}

.modal-register-text a {
  color: #dc2626;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
}

.modal-register-text a:hover {
  text-decoration: underline;
}

.continue-shopping {
  display: inline-block;
  padding: 12px 30px;
  background-color: #dc2626;
  color: white;
  text-decoration: none;
  border-radius: 6px;
  font-weight: 600;
  transition: background-color 0.3s ease;
}

.continue-shopping:hover {
  background-color: #b91c1c;
}

.cart-content {
  display: grid;
  grid-template-columns: 1fr 350px;
  gap: 30px;
}

.cart-items {
  background-color: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.cart-header {
  display: grid;
  grid-template-columns: 3fr 1fr 1.5fr 1fr 0.5fr;
  gap: 15px;
  padding: 15px;
  background-color: #f9fafb;
  border-radius: 8px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 20px;
  font-size: 14px;
}

.cart-item {
  display: grid;
  grid-template-columns: 3fr 1fr 1.5fr 1fr 0.5fr;
  gap: 15px;
  padding: 15px;
  border-bottom: 1px solid #e5e7eb;
  align-items: center;
}

.col-product {
  display: flex;
  align-items: center;
  gap: 12px;
}

.item-image {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  object-fit: cover;
}

.item-info {
  flex: 1;
}

.item-name {
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 4px 0;
  font-size: 14px;
}

.item-category {
  color: #9ca3af;
  font-size: 12px;
  margin: 0;
}

.col-price,
.col-total {
  text-align: center;
  font-weight: 600;
  color: #dc2626;
}

.col-quantity {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: center;
}

.qty-btn {
  width: 30px;
  height: 30px;
  border: 1px solid #e5e7eb;
  background-color: white;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s ease;
}

.qty-btn:hover {
  border-color: #dc2626;
  color: #dc2626;
}

.col-quantity input {
  width: 50px;
  text-align: center;
  border: 1px solid #e5e7eb;
  border-radius: 4px;
  padding: 5px;
  font-weight: 600;
}

.col-action {
  text-align: center;
}

.remove-btn {
  background: none;
  border: none;
  font-size: 18px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.remove-btn:hover {
  transform: scale(1.2);
}

.cart-summary {
  background-color: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: fit-content;
}

.cart-summary h2 {
  font-size: 18px;
  color: #1f2937;
  margin: 0 0 20px 0;
  padding-bottom: 15px;
  border-bottom: 2px solid #e5e7eb;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  color: #6b7280;
  font-size: 14px;
}

.free-shipping {
  color: #10b981;
  font-weight: 600;
}

.summary-divider {
  height: 1px;
  background-color: #e5e7eb;
  margin: 15px 0;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  font-size: 18px;
  font-weight: bold;
  color: #1f2937;
  margin-bottom: 20px;
}

.promo-code {
  display: flex;
  gap: 8px;
  margin-bottom: 15px;
}

.promo-code input {
  flex: 1;
  padding: 10px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-size: 13px;
}

.promo-code-input {
  text-transform: uppercase;
}

.summary-discount {
  color: #10b981;
  font-weight: 600;
}

.apply-btn {
  padding: 10px 15px;
  background-color: #f3f4f6;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  font-size: 13px;
  transition: all 0.3s ease;
}

.apply-btn:hover {
  background-color: #e5e7eb;
}

.checkout-btn {
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  margin-bottom: 12px;
  transition: all 0.3s ease;
}

.checkout-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(220, 38, 38, 0.3);
}

.continue-shopping-link {
  display: block;
  width: 100%;
  padding: 14px;
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  color: white;
  text-decoration: none;
  border-radius: 6px;
  font-weight: 600;
  font-size: 15px;
  text-align: center;
  transition: all 0.3s ease;
}

.continue-shopping-link:hover {
  background-color: #e5e7eb;
  transform: translateY(-2px);
}

@media (max-width: 1024px) {
  .cart-content {
    grid-template-columns: 1fr;
  }

  .cart-summary {
    max-width: 500px;
    margin: 0 auto;
  }
}

@media (max-width: 768px) {
  .cart-title {
    font-size: 24px;
    text-align: center;
  }

  .cart-content {
    gap: 20px;
  }

  .cart-items {
    padding: 15px;
  }

  .cart-header {
    display: none;
  }

  .cart-item {
    grid-template-columns: 1fr;
    gap: 15px;
    padding: 20px;
    background-color: #f9fafb;
    border-radius: 10px;
    border: 2px solid #e5e7eb;
    margin-bottom: 15px;
  }

  .col-product {
    grid-column: 1;
    padding-bottom: 15px;
    border-bottom: 2px solid #e5e7eb;
  }

  .item-image {
    width: 80px;
    height: 80px;
  }

  .item-name {
    font-size: 16px;
  }

  .col-price,
  .col-quantity,
  .col-total {
    display: flex;
    justify-content: space-between;
    align-items: center;
    text-align: left;
    padding: 10px 0;
  }

  .col-price::before {
    content: 'Precio:';
    color: #6b7280;
    font-weight: 600;
    font-size: 14px;
  }

  .col-quantity::before {
    content: 'Cantidad:';
    color: #6b7280;
    font-weight: 600;
    font-size: 14px;
  }

  .col-total::before {
    content: 'Total:';
    color: #6b7280;
    font-weight: 600;
    font-size: 14px;
  }

  .col-quantity {
    justify-content: space-between;
  }

  .col-quantity > div {
    display: flex;
    gap: 10px;
    align-items: center;
  }

  .qty-btn {
    width: 35px;
    height: 35px;
    font-size: 16px;
  }

  .col-quantity input {
    width: 60px;
    height: 35px;
    font-size: 16px;
  }

  .col-action {
    grid-column: 1;
    text-align: center;
    padding-top: 10px;
    border-top: 2px solid #e5e7eb;
  }

  .remove-btn {
    width: 100%;
    padding: 10px 20px;
    background-color: #fee2e2;
    color: #dc2626;
    border: 1px solid #fecaca;
    border-radius: 6px;
    font-size: 14px;
    font-weight: 600;
  }

  .remove-btn:hover {
    background-color: #fecaca;
    transform: none;
  }

  .cart-summary {
    position: static;
    max-width: 100%;
  }

  .summary-item,
  .summary-total {
    font-size: 15px;
  }

  .checkout-btn {
    font-size: 16px;
    padding: 16px;
  }

  .continue-shopping-link {
    font-size: 15px;
    padding: 14px;
  }
}

@media (max-width: 480px) {
  .cart-container {
    padding: 20px 10px;
  }

  .cart-title {
    font-size: 20px;
  }

  .cart-items {
    padding: 10px;
  }

  .cart-item {
    padding: 15px;
  }

  .item-image {
    width: 70px;
    height: 70px;
  }

  .item-name {
    font-size: 15px;
  }

  .col-product {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .col-price,
  .col-quantity,
  .col-total {
    padding: 8px 0;
    font-size: 14px;
  }

  .qty-btn {
    width: 32px;
    height: 32px;
  }

  .col-quantity input {
    width: 50px;
    height: 32px;
    font-size: 14px;
  }

  .promo-code {
    flex-direction: column;
    gap: 10px;
  }

  .promo-code input {
    width: 100%;
    padding: 12px;
    font-size: 14px;
  }

  .apply-btn {
    width: 100%;
    padding: 12px;
    font-size: 14px;
  }

  .cart-summary {
    padding: 20px;
  }

  .checkout-btn {
    font-size: 15px;
    padding: 15px;
  }

  .continue-shopping-link {
    font-size: 14px;
    padding: 13px;
  }
}

/* Estilos para método de entrega */
.delivery-section {
  background-color: #f9fafb;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.delivery-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 12px 0;
}

.delivery-select {
  width: 100%;
  padding: 12px;
  border: 2px solid #e5e7eb;
  border-radius: 6px;
  font-size: 15px;
  background-color: white;
  cursor: pointer;
  transition: border-color 0.2s;
}

.delivery-select:hover {
  border-color: #dc2626;
}

.delivery-select:focus {
  outline: none;
  border-color: #dc2626;
}

.address-section {
  margin-top: 15px;
}

.address-card-small {
  background-color: white;
  padding: 15px;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
}

.address-label {
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
  font-size: 14px;
}

.address-text {
  color: #6b7280;
  margin: 4px 0;
  font-size: 14px;
}

.edit-address-link {
  display: inline-block;
  margin-top: 10px;
  color: #dc2626;
  font-size: 13px;
  text-decoration: none;
  font-weight: 600;
}

.edit-address-link:hover {
  text-decoration: underline;
}

.no-address-warning {
  background-color: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 6px;
  padding: 15px;
  text-align: center;
}

.no-address-warning p {
  color: #dc2626;
  margin: 0 0 10px 0;
  font-size: 14px;
  font-weight: 600;
}

.add-address-link {
  display: inline-block;
  color: #dc2626;
  text-decoration: none;
  font-weight: 600;
  font-size: 14px;
}

.add-address-link:hover {
  text-decoration: underline;
}

.store-pickup-info {
  background-color: white;
  padding: 15px;
  border-radius: 6px;
  border: 1px solid #e5e7eb;
  margin-top: 15px;
}

.store-info {
  color: #6b7280;
  margin: 6px 0;
  font-size: 14px;
}

.checkout-btn.disabled {
  background-color: #9ca3af;
  cursor: not-allowed;
  opacity: 0.6;
}

.checkout-btn.disabled:hover {
  background-color: #9ca3af;
  transform: none;
}
</style>