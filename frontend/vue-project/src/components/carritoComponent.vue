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
            <span v-if="subtotal > 50" class="free-shipping">Gratis</span>
            <span v-else>{{ shipping.toFixed(2) }}€</span>
          </div>

          <div class="summary-divider"></div>

          <div class="summary-total">
            <span>Total</span>
            <span>{{ total.toFixed(2) }}€</span>
          </div>

          <div class="promo-code">
            <input v-model="promoCode" placeholder="Código promocional" />
            <button @click="applyPromo" class="apply-btn">Aplicar</button>
          </div>

          <button @click="checkout" class="checkout-btn">
            Ir a Pagar
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
import { computed } from 'vue'
import { useCartStore } from '@/stores/cartStore'
import { useAuthStore } from '@/stores/authStore'
import { useRouter } from 'vue-router'
import { ref } from 'vue'

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
const showAuthModal = ref(false)

const incrementQuantity = (itemId: number) => {
  cartStore.incrementQuantity(itemId)
}

const decrementQuantity = (itemId: number) => {
  cartStore.decrementQuantity(itemId)
}

const removeItem = (itemId: number) => {
  cartStore.removeItem(itemId)
}

const applyPromo = () => {
  if (promoCode.value.toUpperCase() === 'DESCUENTO10') {
    alert('Código aplicado: 10% de descuento')
  } else {
    alert('Código promocional inválido')
  }
  promoCode.value = ''
}

const checkout = () => {
  if (!authStore.isAuthenticated) {
    showAuthModal.value = true
    return
  }
  alert('Ir a checkout')
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
</style>