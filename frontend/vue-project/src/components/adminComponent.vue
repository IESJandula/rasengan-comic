<template>
  <!-- Protección: Solo admin puede ver esto -->
  <div v-if="isAdmin" class="admin-container">
    <!-- Sidebar -->
    <aside class="admin-sidebar">
      <h2 class="admin-title">Admin Panel</h2>
      <nav class="admin-nav">
        <button
          v-for="tab in tabs"
          :key="tab"
          @click="activeTab = tab"
          :class="['nav-btn', { active: activeTab === tab }]"
        >
          {{ tab }}
        </button>
      </nav>
    </aside>

    <!-- Main Content -->
    <main class="admin-main">
      <!-- Estadísticas -->
      <div v-if="activeTab === 'Estadísticas'" class="admin-section">
        <h1>Panel de Estadísticas</h1>
        
        <div class="stats-grid">
          <div class="stat-card">
            <h3>Productos</h3>
            <p class="stat-number">{{ products.length }}</p>
          </div>
          <div class="stat-card">
            <h3>Usuarios</h3>
            <p class="stat-number">{{ users.length }}</p>
          </div>
          <div class="stat-card">
            <h3>Eventos</h3>
            <p class="stat-number">{{ events.length }}</p>
          </div>
          <div class="stat-card">
            <h3>Reservas Activas</h3>
            <p class="stat-number">{{ reservasActivas }}</p>
          </div>
        </div>
      </div>

      <!-- Reservas -->
      <div v-if="activeTab === 'Reservas'" class="admin-section">
        <h1>Gestión de Reservas de Clientes</h1>
        
        <!-- Filtros de reservas -->
        <div class="reservas-filters">
          <button 
            v-for="filter in reservasFilters" 
            :key="filter.value"
            @click="activeReservaFilter = filter.value"
            :class="['filter-btn', { active: activeReservaFilter === filter.value }]"
          >
            {{ filter.label }} ({{ getReservaCountByStatus(filter.value) }})
          </button>
        </div>

        <!-- Lista de Reservas -->
        <div class="reservas-admin-list">
          <div 
            v-for="reserva in filteredReservasAdmin" 
            :key="reserva.id"
            class="reserva-admin-card"
          >
            <div class="reserva-admin-header">
              <div class="reserva-info-left">
                <span class="reserva-id">#{{ reserva.id }}</span>
                <span :class="['status-badge', reserva.estado]">
                  {{ getStatusLabel(reserva.estado) }}
                </span>
              </div>
              <div class="reserva-info-right">
                <span class="reserva-date">{{ formatDate(reserva.fechaReserva) }}</span>
              </div>
            </div>

            <div class="reserva-admin-body">
              <div class="cliente-info">
                <img :src="reserva.cliente.avatar" :alt="reserva.cliente.nombre" class="cliente-avatar" />
                <div class="cliente-details">
                  <h4>{{ reserva.cliente.nombre }}</h4>
                  <p>{{ reserva.cliente.email }}</p>
                  <p>Tel: {{ reserva.cliente.telefono }}</p>
                </div>
              </div>

              <div class="producto-reservado">
                <img :src="reserva.producto.imagen" :alt="reserva.producto.nombre" class="producto-img" />
                <div class="producto-info">
                  <h4>{{ reserva.producto.nombre }}</h4>
                  <p class="categoria">{{ reserva.producto.categoria }}</p>
                  <p class="cantidad">Cantidad: {{ reserva.cantidad }} unidades</p>
                  <p class="precio">{{ formatPrice(reserva.precioUnitario) }} x {{ reserva.cantidad }} = <strong>{{ formatPrice(reserva.total) }}</strong></p>
                </div>
              </div>

              <div v-if="reserva.fechaDisponibilidad" class="disponibilidad-info">
                <strong>📅 Disponible desde:</strong> {{ formatDate(reserva.fechaDisponibilidad) }}
              </div>

              <div v-if="reserva.notas" class="notas-admin">
                <strong>📝 Notas del cliente:</strong> {{ reserva.notas }}
              </div>
            </div>

            <div class="reserva-admin-actions">
              <button 
                v-if="reserva.estado === 'pendiente'" 
                @click="marcarDisponible(reserva)" 
                class="btn-success"
              >
                ✓ Marcar Disponible
              </button>
              <button 
                v-if="reserva.estado === 'disponible'" 
                @click="marcarEntregado(reserva)" 
                class="btn-primary"
              >
                📦 Marcar como Entregado
              </button>
              <button 
                @click="contactarCliente(reserva)" 
                class="btn-contact"
              >
                📧 Contactar Cliente
              </button>
              <button 
                @click="editarReserva(reserva)" 
                class="btn-edit"
              >
                ✏️ Editar
              </button>
              <button 
                v-if="reserva.estado !== 'recogido' && reserva.estado !== 'cancelada'" 
                @click="cancelarReservaAdmin(reserva)" 
                class="btn-cancel"
              >
                ✗ Cancelar
              </button>
            </div>
          </div>

          <div v-if="filteredReservasAdmin.length === 0" class="empty-reservas">
            <p>No hay reservas {{ activeReservaFilter !== 'todas' ? activeReservaFilter : '' }}</p>
          </div>
        </div>
      </div>

      <!-- Productos -->
      <div v-if="activeTab === 'Productos'" class="admin-section">
        <h1>Gestión de Productos</h1>
        
        <button @click="showProductForm = true" class="add-btn">Añadir Producto</button>

        <!-- Formulario Producto -->
        <div v-if="showProductForm" class="form-modal">
          <div class="form-content">
            <h2>{{ editingProduct ? 'Editar' : 'Nuevo' }} Producto</h2>
            <form @submit.prevent="saveProduct">
              <input v-model="productForm.name" placeholder="Nombre del producto" required />
              <input v-model="productForm.category" placeholder="Categoría" required />
              <input v-model.number="productForm.price" type="number" placeholder="Precio" required />
              <input v-model.number="productForm.discount" type="number" placeholder="Descuento %" />
              <textarea v-model="productForm.description" placeholder="Descripción"></textarea>
              <input type="file" @change="handleImageUpload" accept="image/*" />
              <div class="form-buttons">
                <button type="submit" class="save-btn">Guardar</button>
                <button type="button" @click="showProductForm = false" class="cancel-btn">Cancelar</button>
              </div>
            </form>
          </div>
        </div>

        <!-- Lista de Productos -->
        <div class="items-list">
          <div v-for="product in products" :key="product.id" class="item-card">
            <img :src="product.image" :alt="product.name" class="item-image" />
            <div class="item-details">
              <h3>{{ product.name }}</h3>
              <p>${{ product.price }}</p>
            </div>
            <div class="item-actions">
              <button @click="editProductHandler(product)" class="edit-btn">✏️</button>
              <button @click="deleteProductHandler(product.id)" class="delete-btn">🗑️</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Eventos -->
      <div v-if="activeTab === 'Eventos'" class="admin-section">
        <h1>Gestión de Eventos</h1>
        
        <button @click="showEventForm = true" class="add-btn">Crear Evento</button>

        <!-- Formulario Evento -->
        <div v-if="showEventForm" class="form-modal">
          <div class="form-content">
            <h2>{{ editingEvent ? 'Editar' : 'Nuevo' }} Evento</h2>
            <form @submit.prevent="saveEvent">
              <input v-model="eventForm.name" placeholder="Nombre del evento" required />
              <input v-model="eventForm.date" type="date" required />
              <input v-model="eventForm.time" type="time" required />
              <textarea v-model="eventForm.description" placeholder="Descripción"></textarea>
              <select v-model="eventForm.type">
                <option value="tournament">Torneo</option>
                <option value="workshop">Taller</option>
                <option value="special">Especial</option>
              </select>
              <div class="form-buttons">
                <button type="submit" class="save-btn">Guardar</button>
                <button type="button" @click="showEventForm = false" class="cancel-btn">Cancelar</button>
              </div>
            </form>
          </div>
        </div>

        <!-- Lista de Eventos -->
        <div class="items-list">
          <div v-for="event in events" :key="event.id" class="item-card">
            <div class="item-details">
              <h3>{{ event.name }}</h3>
              <p>{{ event.date }} - {{ event.time }}</p>
              <p>{{ event.description }}</p>
            </div>
            <div class="item-actions">
              <button @click="editEventHandler(event)" class="edit-btn">✏️</button>
              <button @click="deleteEventHandler(event.id)" class="delete-btn">🗑️</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Descuentos -->
      <div v-if="activeTab === 'Descuentos'" class="admin-section">
        <h1>Gestión de Descuentos</h1>
        
        <button @click="showDiscountForm = true" class="add-btn">Crear Descuento</button>

        <!-- Formulario Descuento -->
        <div v-if="showDiscountForm" class="form-modal">
          <div class="form-content">
            <h2>{{ editingDiscount ? 'Editar' : 'Nuevo' }} Descuento</h2>
            <form @submit.prevent="saveDiscount">
              <input v-model="discountForm.code" placeholder="Código" required />
              <input v-model.number="discountForm.percentage" type="number" placeholder="Porcentaje" required />
              <input v-model="discountForm.description" placeholder="Descripción" required />
              <input v-model="discountForm.expiryDate" type="date" required />
              <div class="form-buttons">
                <button type="submit" class="save-btn">Guardar</button>
                <button type="button" @click="showDiscountForm = false" class="cancel-btn">Cancelar</button>
              </div>
            </form>
          </div>
        </div>

        <!-- Lista de Descuentos -->
        <div class="items-list">
          <div v-for="discount in discounts" :key="discount.id" class="item-card">
            <div class="item-details">
              <h3>{{ discount.code }}</h3>
              <p>{{ discount.percentage }}% - {{ discount.description }}</p>
              <p class="expiry">Expira: {{ discount.expiryDate }}</p>
            </div>
            <div class="item-actions">
              <button @click="editDiscountHandler(discount)" class="edit-btn">✏️</button>
              <button @click="deleteDiscountHandler(discount.id)" class="delete-btn">🗑️</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Usuarios -->
      <div v-if="activeTab === 'Usuarios'" class="admin-section">
        <h1>Gestión de Usuarios</h1>
        
        <div class="items-list">
          <div v-for="user in users" :key="user.id" class="item-card">
            <img :src="user.avatar" :alt="user.name" class="item-image" />
            <div class="item-details">
              <h3>{{ user.name }}</h3>
              <p>{{ user.email }}</p>
              <p class="join-date">Se unió: {{ user.joinDate }}</p>
            </div>
            <div class="item-actions">
              <button @click="viewUserHandler(user)" class="view-btn">👁️</button>
              <button @click="deleteUserHandler(user.id)" class="delete-btn">🗑️</button>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>

  <!-- No autorizado -->
  <div v-else class="unauthorized">
    <h1>Acceso Denegado</h1>
    <p>Solo los administradores pueden acceder a este panel.</p>
    <router-link to="/" class="back-btn">Volver al inicio</router-link>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useAuthStore } from '@/stores/authStore'
import { useRouter } from 'vue-router'

interface Product {
  id: number
  name: string
  category: string
  price: number
  discount: number
  description?: string
  image: string
}

interface Event {
  id: number
  name: string
  date: string
  time: string
  description: string
  type: 'tournament' | 'workshop' | 'special'
}

interface Discount {
  id: number
  code: string
  percentage: number
  description: string
  expiryDate: string
}

interface User {
  id: number
  name: string
  email: string
  avatar: string
  joinDate: string
}

interface Cliente {
  nombre: string
  email: string
  telefono: string
  avatar: string
}

interface ProductoReservado {
  nombre: string
  categoria: string
  imagen: string
}

interface ReservaAdmin {
  id: string
  cliente: Cliente
  producto: ProductoReservado
  estado: 'pendiente' | 'disponible' | 'recogido' | 'cancelada'
  cantidad: number
  precioUnitario: number
  total: number
  fechaReserva: string
  fechaDisponibilidad?: string
  notas?: string
}

const authStore = useAuthStore()
const router = useRouter()

// Verificar si es admin
const isAdmin = computed(() => {
  return authStore.user?.email === 'admin@rasengacomics.com'
})

// Si no es admin, redirigir
if (!isAdmin.value) {
  router.push('/')
}

const activeTab = ref('Estadísticas')
const tabs = ['Estadísticas', 'Reservas', 'Productos', 'Eventos', 'Descuentos', 'Usuarios']

// Reservas Admin
const activeReservaFilter = ref('todas')
const reservasFilters = [
  { label: 'Todas', value: 'todas' },
  { label: 'Pendientes', value: 'pendiente' },
  { label: 'Disponibles', value: 'disponible' },
  { label: 'Recogidas', value: 'recogido' },
  { label: 'Canceladas', value: 'cancelada' }
]

const reservasAdmin = ref<ReservaAdmin[]>([
  {
    id: 'RES2024001',
    cliente: {
      nombre: 'Juan Pérez',
      email: 'juan@example.com',
      telefono: '+34 612 345 678',
      avatar: 'https://ui-avatars.com/api/?name=Juan+Perez&background=dc2626&color=fff'
    },
    producto: {
      nombre: 'One Piece Vol. 108',
      categoria: 'Manga',
      imagen: 'https://images.unsplash.com/photo-1612036782180-6f0b6cd846fe?w=200'
    },
    estado: 'disponible',
    cantidad: 1,
    precioUnitario: 8.50,
    total: 8.50,
    fechaReserva: '2025-10-15T10:30:00',
    fechaDisponibilidad: '2025-10-28T09:00:00',
    notas: 'Edición especial con póster'
  },
  {
    id: 'RES2024002',
    cliente: {
      nombre: 'María García',
      email: 'maria@example.com',
      telefono: '+34 623 456 789',
      avatar: 'https://ui-avatars.com/api/?name=Maria+Garcia&background=dc2626&color=fff'
    },
    producto: {
      nombre: 'Yu-Gi-Oh! 25th Anniversary Pack',
      categoria: 'Trading Cards',
      imagen: 'https://images.unsplash.com/photo-1511882150382-421056c89033?w=200'
    },
    estado: 'pendiente',
    cantidad: 3,
    precioUnitario: 15.00,
    total: 45.00,
    fechaReserva: '2025-10-20T14:20:00',
    fechaDisponibilidad: '2025-11-10T10:00:00',
    notas: 'Llamar cuando llegue'
  },
  {
    id: 'RES2024003',
    cliente: {
      nombre: 'Carlos Ruiz',
      email: 'carlos@example.com',
      telefono: '+34 634 567 890',
      avatar: 'https://ui-avatars.com/api/?name=Carlos+Ruiz&background=dc2626&color=fff'
    },
    producto: {
      nombre: 'Chainsaw Man Vol. 15',
      categoria: 'Manga',
      imagen: 'https://images.unsplash.com/photo-1618519764620-7403abdbdfe9?w=200'
    },
    estado: 'recogido',
    cantidad: 1,
    precioUnitario: 9.00,
    total: 9.00,
    fechaReserva: '2025-09-25T11:00:00',
    fechaDisponibilidad: '2025-10-05T10:00:00'
  },
  {
    id: 'RES2024004',
    cliente: {
      nombre: 'Ana Martínez',
      email: 'ana@example.com',
      telefono: '+34 645 678 901',
      avatar: 'https://ui-avatars.com/api/?name=Ana+Martinez&background=dc2626&color=fff'
    },
    producto: {
      nombre: 'Naruto Box Set Complete',
      categoria: 'Box Set',
      imagen: 'https://images.unsplash.com/photo-1578662996442-48f60103fc96?w=200'
    },
    estado: 'pendiente',
    cantidad: 1,
    precioUnitario: 299.00,
    total: 299.00,
    fechaReserva: '2025-10-28T16:45:00',
    fechaDisponibilidad: '2025-12-01T10:00:00'
  }
])

const filteredReservasAdmin = computed(() => {
  if (activeReservaFilter.value === 'todas') {
    return reservasAdmin.value
  }
  return reservasAdmin.value.filter(r => r.estado === activeReservaFilter.value)
})

const reservasActivas = computed(() => {
  return reservasAdmin.value.filter(r => r.estado === 'pendiente' || r.estado === 'disponible').length
})

const getReservaCountByStatus = (status: string): number => {
  if (status === 'todas') return reservasAdmin.value.length
  return reservasAdmin.value.filter(r => r.estado === status).length
}

const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleDateString('es-ES', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatPrice = (price: number): string => {
  return `${price.toFixed(2)}€`
}

const getStatusLabel = (estado: string): string => {
  const labels: Record<string, string> = {
    pendiente: '⏳ Pendiente',
    disponible: '✓ Disponible',
    recogido: '✓ Recogido',
    cancelada: '✗ Cancelada'
  }
  return labels[estado] || estado
}

const marcarDisponible = (reserva: ReservaAdmin): void => {
  if (confirm(`¿Marcar como disponible la reserva #${reserva.id}?`)) {
    reserva.estado = 'disponible'
    reserva.fechaDisponibilidad = new Date().toISOString()
    alert(`Reserva marcada como disponible. Se notificará a ${reserva.cliente.nombre}`)
  }
}

const marcarEntregado = (reserva: ReservaAdmin): void => {
  if (confirm(`¿Confirmar entrega de la reserva #${reserva.id} a ${reserva.cliente.nombre}?`)) {
    reserva.estado = 'recogido'
    alert('Reserva marcada como entregada')
  }
}

const contactarCliente = (reserva: ReservaAdmin): void => {
  const mensaje = `Hola ${reserva.cliente.nombre}, te contactamos sobre tu reserva #${reserva.id}`
  alert(`Abrir email para: ${reserva.cliente.email}\n\nMensaje: ${mensaje}`)
}

const editarReserva = (reserva: ReservaAdmin): void => {
  alert(`Editar reserva #${reserva.id} (funcionalidad en desarrollo)`)
}

const cancelarReservaAdmin = (reserva: ReservaAdmin): void => {
  if (confirm(`¿Cancelar la reserva #${reserva.id}? Se notificará a ${reserva.cliente.nombre}`)) {
    reserva.estado = 'cancelada'
    alert('Reserva cancelada')
  }
}

// Productos
const showProductForm = ref(false)
const editingProduct = ref<Product | null>(null)
const productForm = ref<Omit<Product, 'id'>>({ 
  name: '', 
  category: '', 
  price: 0, 
  discount: 0, 
  description: '', 
  image: '' 
})

const products = ref<Product[]>([
  { 
    id: 1, 
    name: 'One Piece Vol. 100', 
    category: 'Manga', 
    price: 12.99, 
    discount: 10, 
    image: 'https://images.unsplash.com/photo-1612036782180-69db8e541e1f?w=100&h=100&fit=crop' 
  },
  { 
    id: 2, 
    name: 'Batman: The Dark Knight', 
    category: 'Comics', 
    price: 24.99, 
    discount: 0, 
    image: 'https://images.unsplash.com/photo-1594743315886-a18d195ce546?w=100&h=100&fit=crop' 
  }
])

// Eventos
const showEventForm = ref(false)
const editingEvent = ref<Event | null>(null)
const eventForm = ref<Omit<Event, 'id'>>({ 
  name: '', 
  date: '', 
  time: '', 
  description: '', 
  type: 'tournament' 
})

const events = ref<Event[]>([
  { 
    id: 1, 
    name: 'Torneo TCG Magic', 
    date: '2024-01-20', 
    time: '18:00', 
    description: 'Torneo competitive', 
    type: 'tournament' 
  }
])

// Descuentos
const showDiscountForm = ref(false)
const editingDiscount = ref<Discount | null>(null)
const discountForm = ref<Omit<Discount, 'id'>>({ 
  code: '', 
  percentage: 0, 
  description: '', 
  expiryDate: '' 
})

const discounts = ref<Discount[]>([
  { 
    id: 1, 
    code: 'DESCUENTO10', 
    percentage: 10, 
    description: '10% en toda la tienda', 
    expiryDate: '2024-02-28' 
  }
])

// Usuarios
const users = ref<User[]>([
  { 
    id: 1, 
    name: 'Admin', 
    email: 'admin@rasengacomics.com', 
    avatar: 'https://via.placeholder.com/50', 
    joinDate: '2024-01-01' 
  },
  { 
    id: 2, 
    name: 'Usuario', 
    email: 'usuario@rasengacomics.com', 
    avatar: 'https://via.placeholder.com/50', 
    joinDate: '2024-01-15' 
  }
])

// Métodos Productos
const saveProduct = (): void => {
  if (editingProduct.value) {
    const index = products.value.findIndex(p => p.id === editingProduct.value!.id)
    products.value[index] = { id: editingProduct.value.id, ...productForm.value }
  } else {
    products.value.push({ id: Date.now(), ...productForm.value })
  }
  showProductForm.value = false
  productForm.value = { name: '', category: '', price: 0, discount: 0, description: '', image: '' }
  editingProduct.value = null
}

const editProductHandler = (product: Product): void => {
  editingProduct.value = product
  productForm.value = { ...product }
  showProductForm.value = true
}

const deleteProductHandler = (id: number): void => {
  products.value = products.value.filter(p => p.id !== id)
}

const handleImageUpload = (): void => {
  alert('Imagen cargada correctamente')
}

// Métodos Eventos
const saveEvent = (): void => {
  if (editingEvent.value) {
    const index = events.value.findIndex(e => e.id === editingEvent.value!.id)
    events.value[index] = { id: editingEvent.value.id, ...eventForm.value }
  } else {
    events.value.push({ id: Date.now(), ...eventForm.value })
  }
  showEventForm.value = false
  eventForm.value = { name: '', date: '', time: '', description: '', type: 'tournament' }
  editingEvent.value = null
}

const editEventHandler = (event: Event): void => {
  editingEvent.value = event
  eventForm.value = { ...event }
  showEventForm.value = true
}

const deleteEventHandler = (id: number): void => {
  events.value = events.value.filter(e => e.id !== id)
}

// Métodos Descuentos
const saveDiscount = (): void => {
  if (editingDiscount.value) {
    const index = discounts.value.findIndex(d => d.id === editingDiscount.value!.id)
    discounts.value[index] = { id: editingDiscount.value.id, ...discountForm.value }
  } else {
    discounts.value.push({ id: Date.now(), ...discountForm.value })
  }
  showDiscountForm.value = false
  discountForm.value = { code: '', percentage: 0, description: '', expiryDate: '' }
  editingDiscount.value = null
}

const editDiscountHandler = (discount: Discount): void => {
  editingDiscount.value = discount
  discountForm.value = { ...discount }
  showDiscountForm.value = true
}

const deleteDiscountHandler = (id: number): void => {
  discounts.value = discounts.value.filter(d => d.id !== id)
}

// Métodos Usuarios
const viewUserHandler = (user: User): void => {
  alert(`Usuario: ${user.name}\nEmail: ${user.email}`)
}

const deleteUserHandler = (id: number): void => {
  users.value = users.value.filter(u => u.id !== id)
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.admin-container {
  display: flex;
  min-height: 100vh;
  background-color: #f9fafb;
}

.admin-sidebar {
  width: 250px;
  background-color: #1f2937;
  color: white;
  padding: 30px 20px;
  position: sticky;
  top: 0;
  height: 100vh;
  overflow-y: auto;
}

.admin-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 30px;
  color: #dc2626;
}

.admin-nav {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.nav-btn {
  padding: 12px 15px;
  background: none;
  border: none;
  color: #d1d5db;
  text-align: left;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.3s ease;
  font-weight: 500;
}

.nav-btn:hover {
  background-color: #374151;
  color: white;
}

.nav-btn.active {
  background-color: #dc2626;
  color: white;
}

.admin-main {
  flex: 1;
  padding: 40px;
  overflow-y: auto;
}

.admin-section {
  background-color: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.admin-section h1 {
  color: #1f2937;
  margin-bottom: 25px;
  border-bottom: 2px solid #dc2626;
  padding-bottom: 15px;
}

/* Reservas Admin Styles */
.reservas-filters {
  display: flex;
  gap: 10px;
  margin-bottom: 25px;
  flex-wrap: wrap;
}

.filter-btn {
  padding: 10px 18px;
  border: 2px solid #e5e7eb;
  background: white;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
  color: #6b7280;
  font-size: 14px;
}

.filter-btn:hover {
  border-color: #dc2626;
  color: #dc2626;
}

.filter-btn.active {
  background-color: #dc2626;
  color: white;
  border-color: #dc2626;
}

.reservas-admin-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.reserva-admin-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  overflow: hidden;
  transition: box-shadow 0.3s ease;
}

.reserva-admin-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.reserva-admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
}

.reserva-info-left {
  display: flex;
  gap: 15px;
  align-items: center;
}

.reserva-id {
  font-weight: bold;
  color: #1f2937;
  font-size: 16px;
}

.status-badge {
  display: inline-block;
  padding: 5px 14px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
}

.status-badge.pendiente {
  background-color: #fef3c7;
  color: #92400e;
}

.status-badge.disponible {
  background-color: #d1fae5;
  color: #065f46;
}

.status-badge.recogido {
  background-color: #dbeafe;
  color: #1e40af;
}

.status-badge.cancelada {
  background-color: #fee2e2;
  color: #991b1b;
}

.reserva-info-right {
  color: #6b7280;
  font-size: 14px;
}

.reserva-admin-body {
  padding: 20px;
  display: grid;
  gap: 20px;
}

.cliente-info {
  display: flex;
  gap: 15px;
  align-items: center;
  padding: 15px;
  background-color: #f9fafb;
  border-radius: 8px;
}

.cliente-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #dc2626;
}

.cliente-details h4 {
  color: #1f2937;
  margin-bottom: 5px;
  font-size: 16px;
}

.cliente-details p {
  color: #6b7280;
  font-size: 13px;
  margin: 2px 0;
}

.producto-reservado {
  display: flex;
  gap: 15px;
  align-items: center;
  padding: 15px;
  background-color: #fffbeb;
  border-radius: 8px;
  border-left: 4px solid #f59e0b;
}

.producto-img {
  width: 80px;
  height: 110px;
  object-fit: cover;
  border-radius: 6px;
  border: 2px solid #e5e7eb;
}

.producto-info h4 {
  color: #1f2937;
  margin-bottom: 8px;
  font-size: 15px;
}

.producto-info .categoria {
  display: inline-block;
  background: #e5e7eb;
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  color: #4b5563;
  margin-bottom: 5px;
}

.producto-info .cantidad {
  color: #6b7280;
  font-size: 13px;
  margin: 5px 0;
}

.producto-info .precio {
  color: #1f2937;
  font-size: 14px;
  margin-top: 8px;
}

.producto-info .precio strong {
  color: #dc2626;
  font-size: 16px;
}

.disponibilidad-info {
  padding: 12px 15px;
  background-color: #f0fdf4;
  border-radius: 6px;
  color: #166534;
  font-size: 14px;
  border-left: 4px solid #22c55e;
}

.notas-admin {
  padding: 12px 15px;
  background-color: #eff6ff;
  border-radius: 6px;
  color: #1e40af;
  font-size: 14px;
  border-left: 4px solid #3b82f6;
}

.reserva-admin-actions {
  display: flex;
  gap: 10px;
  padding: 15px 20px;
  border-top: 1px solid #e5e7eb;
  background-color: #fafafa;
  flex-wrap: wrap;
}

.reserva-admin-actions button {
  padding: 10px 18px;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 13px;
}

.btn-success {
  background-color: #22c55e;
  color: white;
}

.btn-success:hover {
  background-color: #16a34a;
}

.btn-primary {
  background-color: #3b82f6;
  color: white;
}

.btn-primary:hover {
  background-color: #2563eb;
}

.btn-contact {
  background-color: #8b5cf6;
  color: white;
}

.btn-contact:hover {
  background-color: #7c3aed;
}

.btn-edit {
  background-color: #fbbf24;
  color: #1f2937;
}

.btn-edit:hover {
  background-color: #f59e0b;
}

.btn-cancel {
  background-color: #fee2e2;
  color: #991b1b;
}

.btn-cancel:hover {
  background-color: #fecaca;
}

.empty-reservas {
  text-align: center;
  padding: 60px 20px;
  color: #6b7280;
  font-size: 16px;
}

/* Estilos existentes */
.add-btn {
  padding: 12px 24px;
  background-color: #dc2626;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  margin-bottom: 20px;
  transition: background-color 0.3s ease;
}

.add-btn:hover {
  background-color: #b91c1c;
}

.form-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.form-content {
  background-color: white;
  padding: 30px;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
}

.form-content h2 {
  color: #1f2937;
  margin-bottom: 20px;
}

.form-content form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-content input,
.form-content textarea,
.form-content select {
  padding: 12px;
  border: 2px solid #e5e7eb;
  border-radius: 6px;
  font-size: 14px;
  font-family: inherit;
}

.form-content input:focus,
.form-content textarea:focus,
.form-content select:focus {
  outline: none;
  border-color: #dc2626;
  box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1);
}

.form-buttons {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.save-btn,
.cancel-btn {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.save-btn {
  background-color: #dc2626;
  color: white;
}

.save-btn:hover {
  background-color: #b91c1c;
}

.cancel-btn {
  background-color: #e5e7eb;
  color: #1f2937;
}

.cancel-btn:hover {
  background-color: #d1d5db;
}

.items-list {
  display: grid;
  gap: 15px;
}

.item-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 15px;
  background-color: #f9fafb;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.item-image {
  width: 80px;
  height: 80px;
  border-radius: 6px;
  object-fit: cover;
}

.item-details {
  flex: 1;
}

.item-details h3 {
  color: #1f2937;
  margin-bottom: 5px;
}

.item-details p {
  color: #6b7280;
  font-size: 14px;
  margin: 3px 0;
}

.expiry,
.join-date {
  color: #9ca3af;
  font-size: 12px;
}

.item-actions {
  display: flex;
  gap: 10px;
}

.edit-btn,
.delete-btn,
.view-btn {
  padding: 8px 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.edit-btn {
  background-color: #fbbf24;
  color: #1f2937;
}

.edit-btn:hover {
  background-color: #f59e0b;
}

.delete-btn {
  background-color: #ef4444;
  color: white;
}

.delete-btn:hover {
  background-color: #dc2626;
}

.view-btn {
  background-color: #3b82f6;
  color: white;
}

.view-btn:hover {
  background-color: #2563eb;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.stat-card {
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  color: white;
  padding: 25px;
  border-radius: 8px;
  text-align: center;
}

.stat-card h3 {
  font-size: 14px;
  margin-bottom: 10px;
  opacity: 0.9;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
}

.unauthorized {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: #f9fafb;
  text-align: center;
}

.unauthorized h1 {
  color: #1f2937;
  margin-bottom: 10px;
}

.unauthorized p {
  color: #6b7280;
  margin-bottom: 20px;
}

.back-btn {
  padding: 12px 30px;
  background-color: #dc2626;
  color: white;
  text-decoration: none;
  border-radius: 6px;
  font-weight: 600;
  transition: background-color 0.3s ease;
}

.back-btn:hover {
  background-color: #b91c1c;
}

@media (max-width: 1024px) {
  .admin-container {
    flex-direction: column;
  }

  .admin-sidebar {
    width: 100%;
    height: auto;
    position: sticky;
  }

  .admin-nav {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  }

  .admin-main {
    padding: 20px;
  }

  .reserva-admin-actions {
    flex-direction: column;
  }

  .reserva-admin-actions button {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .admin-main {
    padding: 15px;
  }

  .admin-section {
    padding: 15px;
  }

  .item-card {
    flex-direction: column;
    text-align: center;
  }

  .form-content {
    width: 95%;
  }

  .reserva-admin-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .cliente-info,
  .producto-reservado {
    flex-direction: column;
    text-align: center;
  }

  .producto-img {
    width: 100%;
    height: 150px;
  }
}
</style>