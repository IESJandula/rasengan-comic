<template>
  <div class="profile-container">
    <!-- Loading de autenticación -->
    <div v-if="authStore.loading" class="auth-loading">
      <div class="loading-spinner"></div>
      <p>Cargando perfil...</p>
    </div>

    <!-- Mensaje si no está autenticado -->
    <div v-else-if="!authStore.isAuthenticated" class="not-authenticated">
      <div class="auth-icon">🔒</div>
      <h2>Acceso Restringido</h2>
      <p>Necesitas iniciar sesión para ver tu perfil</p>
      <router-link to="/login" class="login-btn">Iniciar Sesión</router-link>
    </div>

    <!-- Contenido del perfil -->
    <div v-else class="profile-wrapper">
      <!-- Header del Perfil -->
      <div class="profile-header">
        <div class="avatar-container">
          <img v-if="user.avatar" :src="user.avatar" :alt="user.name" class="profile-avatar" />
          <div v-else class="profile-avatar-default">
            <span>{{ getInitials(user.name) }}</span>
          </div>
        </div>
        <div class="profile-info">
          <h1 class="profile-name">{{ user.name }}</h1>
          <p class="profile-email">{{ user.email }}</p>
          <button @click="showEditProfileModal = true" class="edit-btn">Editar Perfil</button>
        </div>
      </div>

      <!-- Tabs -->
      <div class="tabs">
        <button
          v-for="tab in tabs"
          :key="tab"
          @click="activeTab = tab"
          :class="['tab-btn', { active: activeTab === tab }]"
        >
          {{ tab }}
        </button>
      </div>

      <!-- Contenido por Tab -->
      <div class="tab-content">
        <!-- Información Personal -->
        <div v-if="activeTab === 'Información Personal'" class="tab-pane">
          <div class="info-grid">
            <div class="info-item">
              <label>Nombre Completo</label>
              <p>{{ user.name }}</p>
            </div>
            <div class="info-item">
              <label>Email</label>
              <p>{{ user.email }}</p>
            </div>
            <div class="info-item">
              <label>Teléfono</label>
              <p>{{ user.phone || 'No registrado' }}</p>
            </div>
            <div class="info-item">
              <label>Fecha de Registro</label>
              <p>{{ user.joinDate }}</p>
            </div>
          </div>
        </div>

        <!-- Dirección de Envío -->
        <div v-if="activeTab === 'Dirección'" class="tab-pane">
          <div v-if="user.address" class="address-card">
            <h3>Dirección Principal</h3>
            <p>{{ user.address.street }}</p>
            <p>{{ user.address.city }}, {{ user.address.zipCode }}</p>
            <p>{{ user.address.country }}</p>
            <button @click="editAddress" class="edit-address-btn">Editar</button>
          </div>
          <div v-else class="no-address">
            <p>No tienes dirección registrada</p>
            <button @click="addAddress" class="add-address-btn">Añadir Dirección</button>
          </div>
        </div>

        <!-- Preferencias -->
        <div v-if="activeTab === 'Preferencias'" class="tab-pane">
          <div class="preferences">
            <label class="preference-item">
              <input v-model="preferences.newsletter" type="checkbox" />
              <span>Recibir ofertas por email</span>
            </label>
            <label class="preference-item">
              <input v-model="preferences.notifications" type="checkbox" />
              <span>Notificaciones de pedidos</span>
            </label>
          </div>
          <button @click="savePreferences" class="save-preferences-btn">Guardar Preferencias</button>
        </div>

        <!-- Mis compras -->
        <div v-if="activeTab === 'Mis compras'" class="tab-pane">
          <!-- Mensaje de éxito después de pago -->
          <div v-if="showSuccessMessage" class="success-banner">
            <div class="success-icon">✓</div>
            <div class="success-content">
              <h3>¡Pago completado con éxito!</h3>
              <p>Tu pedido ha sido procesado correctamente. Recibirás un email de confirmación pronto.</p>
            </div>
          </div>
          
          <div v-if="comprasLoading" class="compras-loading">
            <div class="loading-spinner"></div>
            <p>Cargando tus compras...</p>
          </div>
          <div v-else-if="comprasError" class="compras-error">
            <span class="error-icon">⚠️</span>
            {{ comprasError }}
          </div>
          <div v-else-if="compras.length === 0" class="compras-empty">
            <div class="empty-icon">🛍️</div>
            <h3>Aún no has realizado compras</h3>
            <p>Cuando realices tu primera compra, aparecerá aquí</p>
            <router-link to="/catalogo" class="empty-action-btn">Ir a la tienda</router-link>
          </div>
          <div v-else class="compras-list">
            <div class="compras-stats">
              <div class="stat">
                <span class="stat-label">Total de pedidos</span>
                <span class="stat-value">{{ compras.length }}</span>
              </div>
              <div class="stat">
                <span class="stat-label">Total gastado</span>
                <span class="stat-value">{{ totalGastado.toFixed(2) }}€</span>
              </div>
            </div>
            
            <div v-for="pedido in compras" :key="pedido.id" class="compra-card">
              <div class="compra-header">
                <div class="compra-info">
                  <div class="compra-title">
                    <span class="pedido-icon">📦</span>
                    <h3>Pedido #{{ pedido.id }}</h3>
                  </div>
                  <p class="compra-meta">
                    <span class="compra-date">📅 {{ formatDate(pedido.fechaPedido) }}</span>
                    <span :class="['compra-estado', getEstadoClass(pedido.estado)]">
                      {{ getEstadoText(pedido.estado) }}
                    </span>
                  </p>
                </div>
                <div class="compra-total">
                  <span class="total-label">Total</span>
                  <span class="total-amount">{{ pedido.total.toFixed(2) }}€</span>
                </div>
              </div>
              
              <div class="compra-divider"></div>
              
              <div class="compra-items">
                <div class="items-header">Productos</div>
                <div v-for="item in pedido.items" :key="item.productoId" class="compra-item">
                  <span class="item-name">{{ item.nombre }}</span>
                  <span class="item-quantity">x{{ item.cantidad }}</span>
                  <span class="item-price">{{ (item.precio * item.cantidad).toFixed(2) }}€</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Mis reservas -->
        <div v-if="activeTab === 'Mis reservas'" class="tab-pane tab-pane-reservas">
          <ReservasComponent />
        </div>

        <!-- Seguridad -->
        <div v-if="activeTab === 'Seguridad'" class="tab-pane">
          <div class="security-section">
            <h3>Cambiar Contraseña</h3>
            <p v-if="passwordChangeSuccess" class="success-message">✓ Contraseña cambiada exitosamente</p>
            <p v-if="passwordChangeError" class="error-message">✗ {{ passwordChangeError }}</p>
            <div class="form-group">
              <label>Contraseña Actual</label>
              <input v-model="passwordForm.currentPassword" type="password" placeholder="••••••••" />
            </div>
            <div class="form-group">
              <label>Nueva Contraseña</label>
              <input v-model="passwordForm.newPassword" type="password" placeholder="••••••••" />
            </div>
            <div class="form-group">
              <label>Confirmar Contraseña</label>
              <input v-model="passwordForm.confirmPassword" type="password" placeholder="••••••••" />
            </div>
            <button @click="changePassword" class="change-password-btn" :disabled="passwordChangeLoading">
              {{ passwordChangeLoading ? 'Cambiando...' : 'Cambiar Contraseña' }}
            </button>
          </div>
        </div>
      </div>

      <!-- Modal Editar Perfil -->
      <div v-if="showEditProfileModal" class="modal-overlay" @click="showEditProfileModal = false">
        <div class="modal-content" @click.stop>
          <div class="modal-header">
            <h2>Editar Perfil</h2>
            <button @click="showEditProfileModal = false" class="modal-close">✕</button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>Nombre</label>
              <input v-model="editForm.name" type="text" placeholder="Tu nombre" />
            </div>
            <div class="form-group">
              <label>Teléfono</label>
              <input v-model="editForm.phone" type="tel" placeholder="+34 123 456 789" />
            </div>
          </div>
          <div class="modal-footer">
            <button @click="showEditProfileModal = false" class="btn-cancel">Cancelar</button>
            <button @click="saveProfile" class="btn-save">Guardar Cambios</button>
          </div>
        </div>
      </div>

      <!-- Modal Editar Dirección -->
      <div v-if="showEditAddressModal" class="modal-overlay" @click="showEditAddressModal = false">
        <div class="modal-content" @click.stop>
          <div class="modal-header">
            <h2>Editar Dirección</h2>
            <button @click="showEditAddressModal = false" class="modal-close">✕</button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>Calle</label>
              <input v-model="addressForm.street" type="text" placeholder="Calle Principal 123" />
            </div>
            <div class="form-group">
              <label>Ciudad</label>
              <input v-model="addressForm.city" type="text" placeholder="Madrid" />
            </div>
            <div class="form-group">
              <label>Código Postal</label>
              <input v-model="addressForm.zipCode" type="text" placeholder="28001" />
            </div>
            <div class="form-group">
              <label>País</label>
              <input v-model="addressForm.country" type="text" placeholder="España" />
            </div>
          </div>
          <div class="modal-footer">
            <button @click="showEditAddressModal = false" class="btn-cancel">Cancelar</button>
            <button @click="saveAddress" class="btn-save">Guardar Dirección</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed } from 'vue'
import { useAuthStore } from '@/stores/authStore'
import { useCartStore } from '@/stores/cartStore'
import { useRoute, useRouter } from 'vue-router'
import api from '@/api/axios'
import { updatePassword, reauthenticateWithCredential, EmailAuthProvider } from 'firebase/auth'
import { auth } from '@/firebase'
import ReservasComponent from '@/components/reservasComponent.vue'

const authStore = useAuthStore()
const cartStore = useCartStore()
const route = useRoute()
const router = useRouter()

const activeTab = ref('Información Personal')
const isAdmin = ref(true) // TODO: Obtener del backend
const tabs = computed(() => {
  const baseTabs = ['Información Personal', 'Dirección', 'Preferencias', 'Mis compras', 'Mis reservas', 'Seguridad']
  return baseTabs
})

// Estados de compras
const compras = ref<any[]>([])
const comprasLoading = ref(false)
const comprasError = ref('')

// Datos del usuario
const user = ref({
  name: authStore.user?.name || 'Usuario',
  email: authStore.user?.email || '',
  avatar: authStore.user?.avatar || '',
  phone: '+34 123 456 789',
  joinDate: '15 de Marzo, 2024',
  address: {
    street: 'Calle Principal 123',
    city: 'Madrid',
    zipCode: '28001',
    country: 'España'
  }
})

// Preferencias
const preferences = ref({
  newsletter: true,
  notifications: true,
  sms: false
})

type PersistedProfile = {
  name: string
  phone: string
  address: {
    street: string
    city: string
    zipCode: string
    country: string
  }
  preferences: {
    newsletter: boolean
    notifications: boolean
    sms: boolean
  }
}

const getProfileStorageKey = () => {
  const identity = authStore.user?.uid || authStore.user?.email
  return identity ? `rasenga_profile_${identity}` : null
}

const saveProfileToStorage = () => {
  const storageKey = getProfileStorageKey()
  if (!storageKey) return

  const payload: PersistedProfile = {
    name: user.value.name,
    phone: user.value.phone,
    address: { ...user.value.address },
    preferences: { ...preferences.value }
  }

  localStorage.setItem(storageKey, JSON.stringify(payload))
}

const loadProfileFromStorage = () => {
  const storageKey = getProfileStorageKey()
  if (!storageKey) return

  const storedProfile = localStorage.getItem(storageKey)
  if (!storedProfile) return

  try {
    const parsed: PersistedProfile = JSON.parse(storedProfile)

    user.value = {
      ...user.value,
      name: parsed.name || user.value.name,
      phone: parsed.phone || user.value.phone,
      address: {
        ...user.value.address,
        ...(parsed.address || {})
      }
    }

    preferences.value = {
      ...preferences.value,
      ...(parsed.preferences || {})
    }

    if (authStore.user) {
      authStore.user.name = user.value.name
    }
  } catch (error) {
    console.error('Error al cargar perfil desde localStorage:', error)
  }
}

// Cargar perfil del usuario desde el backend
const loadUserProfile = async () => {
  if (!authStore.user?.uid) return
  
  try {
    const response = await api.get(`/usuarios/${authStore.user.uid}`)
    const userData = response.data
    
    // Actualizar datos del usuario con los del backend
    if (userData) {
      user.value = {
        ...user.value,
        name: userData.nombre || user.value.name,
        email: userData.email || user.value.email,
        phone: userData.telefono || user.value.phone,
        address: {
          street: userData.calle || '',
          city: userData.ciudad || '',
          zipCode: userData.codigoPostal || '',
          country: userData.pais || ''
        }
      }
      
      // Actualizar authStore
      if (authStore.user) {
        authStore.user.name = user.value.name
      }
      
      // También guardar en localStorage como fallback
      saveProfileToStorage()
    }
  } catch (error) {
    console.error('Error al cargar perfil del backend:', error)
    // Si falla, intentar cargar desde localStorage
    loadProfileFromStorage()
  }
}

// Estados de modales
const showEditProfileModal = ref(false)
const showEditAddressModal = ref(false)
// Gestión de Descuentos (variables que se pueden eliminar)
// const showDiscountModal = ref(false)
// const discounts = ref<any[]>([])
// const discountsLoading = ref(false)
// const discountForm = ref({...})

const categories = ['TCG', 'Manga', 'Cómics', 'Merchandising', 'Accesorios']
const products = ref<any[]>([])

// Formularios
const editForm = ref({
  name: user.value.name,
  phone: user.value.phone
})

const addressForm = ref({
  street: user.value.address.street,
  city: user.value.address.city,
  zipCode: user.value.address.zipCode,
  country: user.value.address.country
})

const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordChangeLoading = ref(false)
const passwordChangeSuccess = ref(false)
const passwordChangeError = ref('')

// Función para obtener iniciales
const getInitials = (name: string) => {
  if (!name) return 'U'
  const names = name.split(' ').filter(n => n.length > 0)
  if (names.length === 0) return 'U'
  if (names.length === 1) return (names[0] || '').charAt(0).toUpperCase()
  return ((names[0] || '').charAt(0) + (names[names.length - 1] || '').charAt(0)).toUpperCase()
}

// Guardar perfil
const saveProfile = async () => {
  if (!authStore.user?.uid) {
    alert('❌ Error: No se pudo identificar al usuario')
    return
  }
  
  try {
    // Actualizar nombre en el endpoint de perfil
    await api.put(`/usuarios/${authStore.user.uid}`, {
      nombre: editForm.value.name,
      email: user.value.email
    })
    
    // Actualizar teléfono en el endpoint de dirección
    await api.put(`/usuarios/${authStore.user.uid}/direccion`, {
      telefono: editForm.value.phone,
      calle: user.value.address.street,
      ciudad: user.value.address.city,
      codigoPostal: user.value.address.zipCode,
      pais: user.value.address.country
    })
    
    // Actualizar localmente
    user.value.name = editForm.value.name
    user.value.phone = editForm.value.phone
    if (authStore.user) {
      authStore.user.name = user.value.name
    }
    saveProfileToStorage()
    showEditProfileModal.value = false
    alert('✓ Perfil actualizado correctamente')
  } catch (error) {
    console.error('Error al actualizar perfil:', error)
    alert('❌ Error al actualizar perfil. Inténtalo de nuevo.')
  }
}

// Editar dirección
const editAddress = () => {
  addressForm.value = {
    street: user.value.address.street,
    city: user.value.address.city,
    zipCode: user.value.address.zipCode,
    country: user.value.address.country
  }
  showEditAddressModal.value = true
}

// Guardar dirección
const saveAddress = async () => {
  if (!authStore.user?.uid) {
    alert('❌ Error: No se pudo identificar al usuario')
    return
  }
  
  try {
    // Actualizar en el backend
    await api.put(`/usuarios/${authStore.user.uid}/direccion`, {
      telefono: user.value.phone,
      calle: addressForm.value.street,
      ciudad: addressForm.value.city,
      codigoPostal: addressForm.value.zipCode,
      pais: addressForm.value.country
    })
    
    // Actualizar localmente
    user.value.address = { ...addressForm.value }
    saveProfileToStorage()
    showEditAddressModal.value = false
    alert('✓ Dirección actualizada correctamente')
  } catch (error) {
    console.error('Error al actualizar dirección:', error)
    alert('❌ Error al actualizar dirección. Inténtalo de nuevo.')
  }
}

// Añadir dirección
const addAddress = () => {
  addressForm.value = {
    street: '',
    city: '',
    zipCode: '',
    country: 'España'
  }
  showEditAddressModal.value = true
}

// Guardar preferencias
const savePreferences = () => {
  saveProfileToStorage()
  alert('✓ Preferencias guardadas correctamente')
}

// Cambiar contraseña con Firebase
const changePassword = async () => {
  passwordChangeSuccess.value = false
  passwordChangeError.value = ''

  // Validaciones
  if (!passwordForm.value.currentPassword || !passwordForm.value.newPassword || !passwordForm.value.confirmPassword) {
    passwordChangeError.value = 'Todos los campos son obligatorios'
    return
  }

  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    passwordChangeError.value = 'Las contraseñas nuevas no coinciden'
    return
  }

  if (passwordForm.value.newPassword.length < 6) {
    passwordChangeError.value = 'La nueva contraseña debe tener al menos 6 caracteres'
    return
  }

  passwordChangeLoading.value = true

  try {
    const currentUser = auth.currentUser
    if (!currentUser || !currentUser.email) {
      passwordChangeError.value = 'No se pudo verificar el usuario'
      return
    }

    // Reautenticar con la contraseña actual
    const credential = EmailAuthProvider.credential(
      currentUser.email,
      passwordForm.value.currentPassword
    )
    await reauthenticateWithCredential(currentUser, credential)

    // Actualizar la contraseña
    await updatePassword(currentUser, passwordForm.value.newPassword)

    passwordChangeSuccess.value = true
    passwordForm.value = {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    }

    setTimeout(() => {
      passwordChangeSuccess.value = false
    }, 3000)
  } catch (error: any) {
    console.error('Error al cambiar contraseña:', error)
    if (error.code === 'auth/wrong-password') {
      passwordChangeError.value = 'La contraseña actual es incorrecta'
    } else if (error.code === 'auth/weak-password') {
      passwordChangeError.value = 'La contraseña es muy débil'
    } else {
      passwordChangeError.value = 'Error al cambiar la contraseña. Intenta de nuevo.'
    }
  } finally {
    passwordChangeLoading.value = false
  }
}

// Formateado de fechas
const formatDate = (value: string) => {
  if (!value) return ''
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) return value
  return date.toLocaleDateString('es-ES', { 
    day: '2-digit', 
    month: 'long', 
    year: 'numeric' 
  })
}

// Estado helpers
const getEstadoClass = (estado: string) => {
  const estadoMap: Record<string, string> = {
    'PAGADO': 'estado-pagado',
    'PENDIENTE': 'estado-pendiente',
    'ENVIADO': 'estado-enviado',
    'ENTREGADO': 'estado-entregado',
    'CANCELADO': 'estado-cancelado'
  }
  return estadoMap[estado] || 'estado-default'
}

const getEstadoText = (estado: string) => {
  const estadoMap: Record<string, string> = {
    'PAGADO': '✓ Pagado',
    'PENDIENTE': '⏳ Pendiente',
    'ENVIADO': '🚚 Enviado',
    'ENTREGADO': '✓ Entregado',
    'CANCELADO': '✗ Cancelado'
  }
  return estadoMap[estado] || estado
}

// Computed properties
const totalGastado = computed(() => {
  return compras.value.reduce((sum, pedido) => sum + (pedido.total || 0), 0)
})

const confirmarSesionStripePendiente = async () => {
  const sessionIdFromQuery = typeof route.query.session_id === 'string' ? route.query.session_id : ''
  const sessionIdFromStorage = localStorage.getItem('pendingStripeSessionId') || ''
  const sessionId = sessionIdFromQuery || sessionIdFromStorage

  if (!sessionId) {
    return
  }

  try {
    await api.post('/stripe/confirm-session', { sessionId })
  } catch (error) {
    console.error('Error confirmando sesión Stripe:', error)
  } finally {
    localStorage.removeItem('pendingStripeSessionId')
  }
}

// Cargar compras
const loadCompras = async () => {
  if (!authStore.user?.uid) {
    return
  }

  comprasLoading.value = true
  comprasError.value = ''

  try {
    const response = await api.get(`/pedidos/usuario/${authStore.user.uid}`)
    const pedidos = Array.isArray(response.data) ? response.data : []

    compras.value = pedidos
      .map((pedido: any) => {
        const items = Array.isArray(pedido.items)
          ? pedido.items.filter((item: any) => !item.reserva)
          : []

        const total = items.reduce((sum: number, item: any) => {
          const precio = Number(item?.precio || 0)
          const cantidad = Number(item?.cantidad || 0)
          return sum + (precio * cantidad)
        }, 0)

        return {
          ...pedido,
          items,
          total
        }
      })
      .filter((pedido: any) => pedido.items.length > 0)
  } catch (err) {
    console.error('Error al cargar compras:', err)
    comprasError.value = 'No se pudieron cargar las compras'
  } finally {
    comprasLoading.value = false
  }
}

// Inicialización
const initTabFromQuery = () => {
  if (route.query.tab === 'compras') {
    activeTab.value = 'Mis compras'
    return
  }

  if (route.query.tab === 'reservas') {
    activeTab.value = 'Mis reservas'
  }
}

// ==================== Gestión de Descuentos ====================

// Cargar descuentos
// Mostrar mensaje de éxito después de pago
const showSuccessMessage = ref(false)

onMounted(async () => {
  if (authStore.user?.name) {
    user.value.name = authStore.user.name
    editForm.value.name = authStore.user.name
  }

  // Cargar perfil desde el backend (con fallback a localStorage)
  await loadUserProfile()
  
  editForm.value.name = user.value.name
  editForm.value.phone = user.value.phone
  addressForm.value = { ...user.value.address }

  initTabFromQuery()
  
  // Si viene de un pago exitoso
  if (route.query.checkout === 'success') {
    cartStore.clearCart()
    activeTab.value = 'Mis compras'
    showSuccessMessage.value = true

    await confirmarSesionStripePendiente()
    
    // Esperar un poco para que el webhook procese el pedido
    setTimeout(async () => {
      await loadCompras()
    }, 2000)
    
    // Ocultar mensaje después de 5 segundos
    setTimeout(() => {
      showSuccessMessage.value = false
      // Limpiar el parámetro de la URL
      router.replace({ query: { tab: 'compras' } })
    }, 5000)
  } else if (activeTab.value === 'Mis compras') {
    loadCompras()
  }
})

watch(
  () => authStore.user?.uid,
  async () => {
    if (!authStore.user) return
    if (authStore.user.name) {
      user.value.name = authStore.user.name
    }
    await loadUserProfile()
    editForm.value.name = user.value.name
    editForm.value.phone = user.value.phone
    addressForm.value = { ...user.value.address }
  }
)

watch(activeTab, (value) => {
  if (value === 'Mis compras' && compras.value.length === 0) {
    loadCompras()
  }
})
</script>

<style scoped>
.profile-container {
  background-color: #f9fafb;
  min-height: 100vh;
  padding: 40px 20px;
}

.profile-wrapper {
  max-width: 1000px;
  margin: 0 auto;
}

.profile-header {
  background-color: white;
  border-radius: 12px;
  padding: 30px;
  display: flex;
  align-items: center;
  gap: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.profile-avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 3px solid #dc2626;
  object-fit: cover;
}

.profile-avatar-default {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 3px solid #dc2626;
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  font-weight: bold;
  color: white;
}

.auth-loading, .not-authenticated {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 70vh;
  gap: 20px;
  text-align: center;
}

.auth-icon {
  font-size: 80px;
  opacity: 0.5;
}

.not-authenticated h2 {
  color: #1f2937;
  margin: 0;
}

.not-authenticated p {
  color: #6b7280;
  margin: 0;
}

.login-btn {
  padding: 12px 30px;
  background-color: #dc2626;
  color: white;
  text-decoration: none;
  border-radius: 8px;
  font-weight: 600;
  transition: background-color 0.3s ease;
  margin-top: 10px;
}

.login-btn:hover {
  background-color: #b91c1c;
}

/* Modales */
.modal-overlay {
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
  animation: fadeIn 0.2s ease;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h2 {
  margin: 0;
  color: #1f2937;
  font-size: 22px;
}

.modal-close {
  background: none;
  border: none;
  font-size: 28px;
  color: #6b7280;
  cursor: pointer;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.modal-close:hover {
  background-color: #f3f4f6;
  color: #1f2937;
}

.modal-body {
  padding: 25px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 20px 25px;
  border-top: 1px solid #e5e7eb;
}

.btn-cancel, .btn-save {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-cancel {
  background-color: #f3f4f6;
  color: #374151;
}

.btn-cancel:hover {
  background-color: #e5e7eb;
}

.btn-save {
  background-color: #dc2626;
  color: white;
}

.btn-save:hover {
  background-color: #b91c1c;
}

/* Mensajes */
.success-message {
  padding: 12px 16px;
  background-color: #d1fae5;
  color: #065f46;
  border-radius: 6px;
  border-left: 4px solid #10b981;
  margin-bottom: 15px;
}

.error-message {
  padding: 12px 16px;
  background-color: #fee2e2;
  color: #991b1b;
  border-radius: 6px;
  border-left: 4px solid #dc2626;
  margin-bottom: 15px;
}

.profile-info {
  flex: 1;
}

.profile-name {
  font-size: 28px;
  font-weight: bold;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.profile-email {
  color: #6b7280;
  margin: 0 0 15px 0;
}

.edit-btn {
  padding: 10px 20px;
  background-color: #dc2626;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.edit-btn:hover {
  background-color: #b91c1c;
}

.tabs {
  display: flex;
  gap: 15px;
  background-color: white;
  padding: 0 30px;
  border-radius: 12px 12px 0 0;
  border-bottom: 2px solid #e5e7eb;
}

.tab-btn {
  padding: 15px 20px;
  border: none;
  background: none;
  color: #6b7280;
  font-weight: 600;
  cursor: pointer;
  border-bottom: 3px solid transparent;
  transition: all 0.3s ease;
}

.tab-btn:hover {
  color: #1f2937;
}

.tab-btn.active {
  color: #dc2626;
  border-bottom-color: #dc2626;
}

.tab-content {
  background-color: white;
  border-radius: 0 0 12px 12px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.tab-pane {
  animation: fadeIn 0.3s ease;
}

.compras-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 15px;
}

/* Banner de éxito después de pago */
.success-banner {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px 25px;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
  border-radius: 12px;
  margin-bottom: 30px;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
  animation: slideDown 0.5s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.success-icon {
  width: 50px;
  height: 50px;
  background-color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: bold;
  color: #10b981;
  flex-shrink: 0;
}

.success-content {
  flex: 1;
}

.success-content h3 {
  color: white;
  margin: 0 0 5px 0;
  font-size: 18px;
  font-weight: 600;
}

.success-content p {
  color: rgba(255, 255, 255, 0.9);
  margin: 0;
  font-size: 14px;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f4f6;
  border-top-color: #dc2626;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.compras-error {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #dc2626;
  padding: 20px;
  background-color: #fef2f2;
  border-radius: 8px;
  border-left: 4px solid #dc2626;
}

.error-icon {
  font-size: 24px;
}

.compras-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  gap: 15px;
}

.empty-icon {
  font-size: 80px;
  opacity: 0.5;
}

.compras-empty h3 {
  color: #1f2937;
  margin: 0;
}

.compras-empty p {
  color: #6b7280;
  margin: 0;
}

.empty-action-btn {
  margin-top: 10px;
  padding: 12px 30px;
  background-color: #dc2626;
  color: white;
  text-decoration: none;
  border-radius: 8px;
  font-weight: 600;
  transition: background-color 0.3s ease;
}

.empty-action-btn:hover {
  background-color: #b91c1c;
}

.compras-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.compras-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-bottom: 25px;
}

.stat {
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  padding: 20px;
  border-radius: 12px;
  color: white;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-label {
  font-size: 13px;
  opacity: 0.9;
  font-weight: 500;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
}

.compra-card {
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  padding: 0;
  overflow: hidden;
  transition: all 0.3s ease;
  background: white;
}

.compra-card:hover {
  border-color: #dc2626;
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.1);
}

.compra-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
  background: #f9fafb;
}

.compra-info {
  flex: 1;
}

.compra-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.pedido-icon {
  font-size: 24px;
}

.compra-title h3 {
  margin: 0;
  font-size: 18px;
  color: #1f2937;
}

.compra-meta {
  display: flex;
  gap: 15px;
  align-items: center;
  margin: 0;
}

.compra-date {
  color: #6b7280;
  font-size: 14px;
}

.compra-estado {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}

.estado-pagado {
  background-color: #d1fae5;
  color: #065f46;
}

.estado-pendiente {
  background-color: #fef3c7;
  color: #92400e;
}

.estado-enviado {
  background-color: #dbeafe;
  color: #1e40af;
}

.estado-entregado {
  background-color: #d1fae5;
  color: #065f46;
}

.estado-cancelado {
  background-color: #fee2e2;
  color: #991b1b;
}

.compra-total {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.total-label {
  font-size: 12px;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.total-amount {
  font-size: 24px;
  font-weight: 800;
  color: #dc2626;
}

.compra-divider {
  height: 1px;
  background: #e5e7eb;
  margin: 0 20px;
}

.compra-items {
  padding: 20px;
}

.items-header {
  font-weight: 600;
  color: #374151;
  margin-bottom: 12px;
  font-size: 14px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.compra-item {
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: 15px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
  margin-bottom: 8px;
  align-items: center;
}

.compra-item:last-child {
  margin-bottom: 0;
}

.item-name {
  color: #1f2937;
  font-weight: 500;
}

.item-quantity {
  color: #6b7280;
  font-size: 14px;
  padding: 4px 10px;
  background: white;
  border-radius: 6px;
  font-weight: 600;
}

.item-price {
  color: #dc2626;
  font-weight: 700;
  min-width: 80px;
  text-align: right;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 30px;
}

.info-item {
  display: flex;
  flex-direction: column;
}

.info-item label {
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
  font-size: 14px;
}

.info-item p {
  color: #6b7280;
  margin: 0;
}

.address-card {
  background-color: #f9fafb;
  padding: 20px;
  border-radius: 8px;
  border-left: 3px solid #dc2626;
}

.address-card h3 {
  color: #1f2937;
  margin: 0 0 10px 0;
}

.address-card p {
  color: #6b7280;
  margin: 5px 0;
}

.edit-address-btn,
.add-address-btn {
  margin-top: 15px;
  padding: 10px 20px;
  background-color: #dc2626;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.edit-address-btn:hover,
.add-address-btn:hover {
  background-color: #b91c1c;
}

.no-address {
  text-align: center;
  padding: 40px 20px;
  color: #6b7280;
}

.preferences {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 20px;
}

.preference-item {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 12px;
  background-color: #f9fafb;
  border-radius: 6px;
  transition: background-color 0.3s ease;
}

.preference-item:hover {
  background-color: #f3f4f6;
}

.preference-item input {
  width: 20px;
  height: 20px;
  cursor: pointer;
  accent-color: #dc2626;
}

.preference-item span {
  color: #374151;
}

.save-preferences-btn {
  padding: 12px 30px;
  background-color: #dc2626;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
  align-self: flex-start;
}

.save-preferences-btn:hover {
  background-color: #b91c1c;
}

.security-section {
  max-width: 400px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 15px;
}

.form-group label {
  font-weight: 600;
  color: #374151;
  font-size: 14px;
}

.form-group input {
  padding: 10px 12px;
  border: 2px solid #e5e7eb;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.3s ease;
}

.form-group input:focus {
  outline: none;
  border-color: #dc2626;
}

.change-password-btn {
  padding: 12px 30px;
  background-color: #dc2626;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.change-password-btn:hover {
  background-color: #b91c1c;
}

/* ==================== Gestión de Descuentos ==================== */

.discounts-section {
  width: 100%;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.section-header h3 {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin: 0;
}

.create-discount-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.3);
}

.create-discount-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(220, 38, 38, 0.4);
}

.btn-icon {
  font-size: 20px;
  font-weight: bold;
}

.discounts-loading,
.discounts-empty {
  text-align: center;
  padding: 60px 20px;
}

.discounts-loading .loading-spinner {
  width: 50px;
  height: 50px;
  margin: 0 auto 20px;
  border: 4px solid #f3f4f6;
  border-top-color: #dc2626;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.discounts-empty .empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.discounts-empty h4 {
  font-size: 20px;
  color: #374151;
  margin: 0 0 10px 0;
}

.discounts-empty p {
  color: #6b7280;
  margin: 0;
}

.discounts-list {
  display: grid;
  gap: 20px;
}

.discount-card {
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
}

.discount-card:hover {
  border-color: #dc2626;
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.1);
}

.discount-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e5e7eb;
}

.discount-code-badge {
  font-size: 18px;
  font-weight: 700;
  color: #dc2626;
  background: #fef2f2;
  padding: 8px 16px;
  border-radius: 8px;
  font-family: 'Courier New', monospace;
  letter-spacing: 1px;
}

.discount-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  width: 36px;
  height: 36px;
  border: 2px solid #e5e7eb;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.2s ease;
}

.action-btn:hover {
  transform: scale(1.1);
}

.edit-btn:hover {
  border-color: #3b82f6;
  background: #eff6ff;
}

.delete-btn:hover {
  border-color: #dc2626;
  background: #fef2f2;
}

.discount-info {
  display: grid;
  gap: 12px;
}

.discount-info > div {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.discount-info .label {
  font-weight: 600;
  color: #6b7280;
  font-size: 14px;
}

.discount-info .value {
  font-weight: 600;
  color: #1f2937;
  font-size: 15px;
}

.discount-value .value {
  color: #10b981;
  font-size: 18px;
}

.discount-dates {
  color: #6b7280;
  font-size: 14px;
}

.date-item {
  background: #f9fafb;
  padding: 6px 12px;
  border-radius: 6px;
}

/* Modal de Descuentos */
.discount-modal {
  max-width: 650px;
}

.discount-modal .modal-header h2 {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0;
}

.modal-icon {
  font-size: 28px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group.half {
  margin-bottom: 0;
}

.form-group label {
  display: block;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
  font-size: 14px;
}

.form-group label.required::after {
  content: ' *';
  color: #dc2626;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 10px 14px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 15px;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #dc2626;
  box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1);
}

.uppercase-input {
  text-transform: uppercase;
}

.select-input {
  cursor: pointer;
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%23374151' d='M6 9L1 4h10z'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  padding-right: 38px;
}

.input-with-suffix {
  position: relative;
  display: flex;
  align-items: center;
}

.input-with-suffix input {
  padding-right: 45px;
}

.input-suffix {
  position: absolute;
  right: 12px;
  font-weight: 700;
  color: #6b7280;
  font-size: 16px;
  pointer-events: none;
}

.date-input {
  cursor: pointer;
}

.form-hint {
  display: block;
  margin-top: 6px;
  font-size: 13px;
  color: #6b7280;
  font-style: italic;
}

@media (max-width: 768px) {
  .profile-header {
    flex-direction: column;
    text-align: center;
  }

  .tabs {
    flex-wrap: wrap;
    gap: 0;
  }

  .tab-btn {
    flex: 1;
  }

  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .section-header {
    flex-direction: column;
    gap: 15px;
  }
  
  .create-discount-btn {
    width: 100%;
    justify-content: center;
  }
  
  .form-row {
    grid-template-columns: 1fr;
    gap: 0;
  }
  
  .form-group.half {
    margin-bottom: 20px;
  }
  
  .discount-modal {
    max-width: 95%;
  }
}
</style>