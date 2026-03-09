<template>
  <div class="reservas-container">
    <div class="header">
      <h1>📦 Mis Reservas de Productos</h1>
      <p class="subtitle">Gestiona todas tus reservas de comics, manga y merchandising</p>
    </div>

    <!-- Filtros -->
    <div class="filters">
      <button 
        v-for="filter in filters" 
        :key="filter.value"
        @click="activeFilter = filter.value"
        :class="['filter-btn', { active: activeFilter === filter.value }]"
      >
        {{ filter.label }}
      </button>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>Cargando reservas...</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="filteredReservas.length === 0" class="empty-state">
      <div class="empty-icon">📭</div>
      <h2>{{ getEmptyStateTitle() }}</h2>
      <p>{{ getEmptyStateMessage() }}</p>
      <router-link to="/tienda" class="btn-primary">Ir a la Tienda</router-link>
    </div>

    <!-- Lista de Reservas -->
    <div v-else class="reservas-list">
      <div 
        v-for="reserva in filteredReservas" 
        :key="reserva.id"
        class="reserva-card"
      >
        <div class="reserva-header">
          <div class="producto-info">
            <img :src="reserva.producto.imagen" :alt="reserva.producto.nombre" class="producto-imagen" />
            <div class="producto-details">
              <h3>{{ reserva.producto.nombre }}</h3>
              <p class="producto-categoria">{{ reserva.producto.categoria }}</p>
              <p class="producto-editorial">Editorial: {{ reserva.producto.editorial }}</p>
            </div>
          </div>
          <div class="reserva-status">
            <span :class="['status-badge', reserva.estado]">
              {{ getStatusLabel(reserva.estado) }}
            </span>
          </div>
        </div>

        <div class="reserva-body">
          <div class="reserva-details">
            <div class="detail-item">
              <span class="label">Número de Reserva:</span>
              <span class="value">#{{ reserva.id }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Cantidad:</span>
              <span class="value">{{ reserva.cantidad }} {{ reserva.cantidad === 1 ? 'unidad' : 'unidades' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Precio Unitario:</span>
              <span class="value">{{ formatPrice(reserva.precioUnitario) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Total:</span>
              <span class="value price">{{ formatPrice(reserva.total) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Fecha de Reserva:</span>
              <span class="value">{{ formatDate(reserva.fechaReserva) }}</span>
            </div>
            <div class="detail-item" v-if="reserva.fechaDisponibilidad">
              <span class="label">Disponible desde:</span>
              <span class="value availability">{{ formatDate(reserva.fechaDisponibilidad) }}</span>
            </div>
            <div class="detail-item" v-if="reserva.estado === 'pendiente' && reserva.tiempoRestante">
              <span class="label">⏱️ Tiempo restante:</span>
              <span class="value timer">{{ reserva.tiempoRestante }}</span>
            </div>
          </div>

          <!-- Nota de la reserva -->
          <div v-if="reserva.notas" class="reserva-notas">
            <p><strong>📝 Notas:</strong> {{ reserva.notas }}</p>
          </div>

          <!-- Progreso de disponibilidad -->
          <div v-if="reserva.estado === 'pendiente'" class="availability-progress">
            <div class="progress-label">
              <span>Estado de disponibilidad</span>
              <span class="progress-percentage">{{ getAvailabilityProgress(reserva) }}%</span>
            </div>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: getAvailabilityProgress(reserva) + '%' }"></div>
            </div>
          </div>
        </div>

        <div class="reserva-footer">
          <button 
            @click="verDetalles(reserva)" 
            class="btn-secondary"
          >
            👁️ Ver Detalles
          </button>
          <button 
            v-if="reserva.estado === 'pendiente' || reserva.estado === 'disponible'" 
            @click="cancelarReserva(reserva)" 
            class="btn-danger"
          >
            ✗ Cancelar Reserva
          </button>
        </div>
      </div>
    </div>

    <!-- Resumen de reservas -->
    <div v-if="filteredReservas.length > 0" class="resumen-section">
      <h3>📊 Resumen</h3>
      <div class="resumen-grid">
        <div class="resumen-item">
          <span class="resumen-label">Total reservas activas:</span>
          <span class="resumen-value">{{ reservasActivas }}</span>
        </div>
        <div class="resumen-item">
          <span class="resumen-label">Valor total reservado:</span>
          <span class="resumen-value price">{{ formatPrice(valorTotalReservado) }}</span>
        </div>
        <div class="resumen-item">
          <span class="resumen-label">Productos disponibles:</span>
          <span class="resumen-value">{{ productosDisponibles }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import api from '@/api/axios'
import { useAuthStore } from '@/stores/authStore'

const authStore = useAuthStore()

interface Producto {
  nombre: string
  categoria: string
  editorial: string
  imagen: string
}

interface Reserva {
  id: string
  pedidoId?: number
  producto: Producto
  estado: 'pendiente' | 'disponible' | 'recogido' | 'cancelada' | 'expirada'
  cantidad: number
  precioUnitario: number
  total: number
  fechaReserva: string
  fechaDisponibilidad?: string
  notas?: string
  tiempoRestante?: string
}

// router not needed here
const loading = ref(false)
const activeFilter = ref('todas')

const filters = [
  { label: 'Todas', value: 'todas' },
  { label: 'Pendientes', value: 'pendiente' },
  { label: 'Disponibles', value: 'disponible' },
  { label: 'Recogidos', value: 'recogido' },
  { label: 'Canceladas', value: 'cancelada' }
]

// Datos de reservas
const reservas = ref<Reserva[]>([])

// Determinar el estado de una reserva basándose en fechas y estado del pedido
const determinarEstadoReserva = (pedidoEstado: string, fechaDisponibilidad?: string, estadoReserva?: string): 'pendiente' | 'disponible' | 'recogido' | 'cancelada' | 'expirada' => {
  const estadoPedidoNormalizado = (pedidoEstado || '').toUpperCase()

  // Si tiene un estado específico de reserva, usarlo
  if (estadoReserva) {
    return estadoReserva as any
  }
  
  // Si el pedido está cancelado
  if (estadoPedidoNormalizado === 'CANCELADO') {
    return 'cancelada'
  }
  
  // Si el pedido está marcado como recogido/completado
  if (estadoPedidoNormalizado === 'RECOGIDO' || estadoPedidoNormalizado === 'COMPLETADO' || estadoPedidoNormalizado === 'ENTREGADO') {
    return 'recogido'
  }
  
  // Si tiene fecha de disponibilidad, verificar si ya está disponible
  if (fechaDisponibilidad) {
    const ahora = new Date()
    const fechaDisp = new Date(fechaDisponibilidad)
    
    if (fechaDisp <= ahora) {
      return 'disponible'
    }
  }
  
  // Por defecto, pendiente (todavía no disponible)
  return 'pendiente'
}

const isItemReserva = (item: any, pedidoEstado: string): boolean => {
  if (item?.reserva === true) {
    return true
  }

  const estadoPedidoNormalizado = (pedidoEstado || '').toUpperCase()
  return (
    estadoPedidoNormalizado === 'PENDIENTE' ||
    estadoPedidoNormalizado === 'DISPONIBLE' ||
    estadoPedidoNormalizado === 'RECOGIDO' ||
    estadoPedidoNormalizado === 'CANCELADO'
  )
}

// Cargar reservas desde la API
const loadReservas = async () => {
  loading.value = true
  reservas.value = []

  try {
    const userUid = authStore.user?.uid

    if (!userUid) {
      console.warn('⚠️ Usuario no autenticado')
      return
    }

    console.log('📋 Cargando reservas de productos para usuario:', userUid)

    const response = await api.get(`/pedidos/usuario/${userUid}`)
    const pedidos = Array.isArray(response.data) ? response.data : []
    const reservasDesdePedidos: Reserva[] = []

    pedidos.forEach((pedido: any) => {
      const fechaPedido = pedido?.fechaPedido || new Date().toISOString()
      const estadoPedido = pedido?.estado || 'PENDIENTE'
      const items = Array.isArray(pedido?.items) ? pedido.items : []

      items
        .filter((item: any) => isItemReserva(item, estadoPedido))
        .forEach((item: any) => {
          const cantidad = Number(item?.cantidad || 0)
          const precioUnitario = Number(item?.precio || 0)
          const nombre = item?.nombre || 'Producto en reserva'
          
          // Calcular fecha de disponibilidad (30 días después del pedido por defecto)
          const fechaDisp = item?.fechaDisponibilidad ? new Date(item.fechaDisponibilidad) : new Date(fechaPedido)
          if (!item?.fechaDisponibilidad) {
            fechaDisp.setDate(fechaDisp.getDate() + 30)
          }
          
          const estado = determinarEstadoReserva(
            estadoPedido,
            fechaDisp.toISOString(),
            item?.estadoReserva
          )

          reservasDesdePedidos.push({
            id: `${pedido?.id ?? 'pedido'}-${item?.productoId ?? Math.random()}`,
            pedidoId: pedido?.id,
            producto: {
              nombre,
              categoria: item?.categoria || 'Producto Reserva',
              editorial: item?.editorial || 'Rasengan Comics',
              imagen: item?.imagen || 'https://images.unsplash.com/photo-1612036782180-69db8e541e1f?w=400'
            },
            estado,
            cantidad,
            precioUnitario,
            total: precioUnitario * cantidad,
            fechaReserva: fechaPedido,
            fechaDisponibilidad: fechaDisp.toISOString(),
            notas: `Reserva generada desde pedido #${pedido?.id ?? '-'}`,
            tiempoRestante: ''
          })
        })
    })

    reservas.value = reservasDesdePedidos
    console.log('✅ Reservas de productos cargadas:', reservas.value.length)
    updateTimers()
  } catch (err) {
    console.error('❌ Error al cargar reservas:', err)
    reservas.value = []
  } finally {
    loading.value = false
  }
}

// Actualizar timers de disponibilidad
const updateTimers = () => {
  reservas.value.forEach(reserva => {
    if (reserva.fechaDisponibilidad && reserva.estado === 'pendiente') {
      const now = new Date().getTime()
      const disponibilidadDate = new Date(reserva.fechaDisponibilidad).getTime()
      const diferenciaMs = disponibilidadDate - now

      if (diferenciaMs <= 0) {
        reserva.tiempoRestante = 'Disponible ahora'
      } else {
        const dias = Math.floor(diferenciaMs / (1000 * 60 * 60 * 24))
        const horas = Math.floor((diferenciaMs % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
        const minutos = Math.floor((diferenciaMs % (1000 * 60 * 60)) / (1000 * 60))

        if (dias > 0) {
          reserva.tiempoRestante = `${dias}d ${horas}h ${minutos}m`
        } else if (horas > 0) {
          reserva.tiempoRestante = `${horas}h ${minutos}m`
        } else {
          reserva.tiempoRestante = `${minutos}m`
        }
      }
    }
  })
}

// Timer para actualizar cada minuto
let timerInterval: ReturnType<typeof setInterval> | null = null

const startTimer = () => {
  timerInterval = setInterval(() => {
    updateTimers()
  }, 60000) // Actualizar cada minuto
}

onMounted(() => {
  loadReservas()
  startTimer()
})

onBeforeUnmount(() => {
  if (timerInterval) {
    clearInterval(timerInterval)
  }
})

const filteredReservas = computed(() => {
  if (activeFilter.value === 'todas') {
    return reservas.value
  }
  return reservas.value.filter(r => r.estado === activeFilter.value)
})

const reservasActivas = computed(() => {
  return reservas.value.filter(r => r.estado === 'pendiente' || r.estado === 'disponible').length
})

const valorTotalReservado = computed(() => {
  return reservas.value
    .filter(r => r.estado === 'pendiente' || r.estado === 'disponible')
    .reduce((sum, r) => sum + r.total, 0)
})

const productosDisponibles = computed(() => {
  return reservas.value.filter(r => r.estado === 'disponible').length
})

const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleDateString('es-ES', {
    year: 'numeric',
    month: 'long',
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
    cancelada: '✗ Cancelada',
    expirada: '⌛ Expirada'
  }
  return labels[estado] || estado
}

const getAvailabilityProgress = (reserva: Reserva): number => {
  if (!reserva.fechaDisponibilidad) return 0
  
  const now = new Date().getTime()
  const reservaDate = new Date(reserva.fechaReserva).getTime()
  const disponibilidadDate = new Date(reserva.fechaDisponibilidad).getTime()
  
  const totalTime = disponibilidadDate - reservaDate
  const elapsedTime = now - reservaDate
  
  const progress = Math.min(100, Math.max(0, (elapsedTime / totalTime) * 100))
  return Math.round(progress)
}

const getEmptyStateTitle = (): string => {
  const titles: Record<string, string> = {
    todas: 'No tienes reservas',
    pendiente: 'No tienes reservas pendientes',
    disponible: 'No tienes reservas disponibles',
    recogido: 'No tienes reservas recogidas',
    cancelada: 'No tienes reservas canceladas'
  }
  return titles[activeFilter.value] || 'No tienes reservas'
}

const getEmptyStateMessage = (): string => {
  const messages: Record<string, string> = {
    todas: 'Explora nuestra tienda y reserva tus productos favoritos',
    pendiente: 'Todas tus reservas están disponibles o ya fueron recogidas',
    disponible: 'No tienes productos listos para recoger en este momento',
    recogido: 'Aún no has recogido ninguna reserva',
    cancelada: 'No has cancelado ninguna reserva'
  }
  return messages[activeFilter.value] || 'Explora nuestra tienda y reserva tus productos favoritos'
}

const verDetalles = (reserva: Reserva): void => {
  console.log('Ver detalles de reserva:', reserva.id)
  // Aquí podrías abrir un modal o navegar a una página de detalles
}

const cancelarReserva = async (reserva: Reserva): Promise<void> => {
  if (!confirm(`¿Estás seguro de que quieres cancelar la reserva de "${reserva.producto.nombre}"?\n\nEsta acción no se puede deshacer.`)) {
    return
  }

  try {
    if (!reserva.pedidoId) {
      console.error('❌ No se puede cancelar: pedidoId no disponible')
      alert('Error: No se pudo identificar el pedido')
      return
    }

    // Llamar al endpoint para actualizar el estado del pedido a CANCELADO
    await api.put(`/pedidos/${reserva.pedidoId}/estado`, {
      estado: 'CANCELADO'
    })

    // Actualizar localmente
    reserva.estado = 'cancelada'
    console.log('✅ Reserva cancelada:', reserva.id)
    
    // Recargar las reservas para reflejar el cambio
    await loadReservas()
    
    alert('Reserva cancelada exitosamente')
  } catch (error) {
    console.error('❌ Error al cancelar reserva:', error)
    alert('Error al cancelar la reserva. Por favor, intenta de nuevo.')
  }
}


</script>

<style scoped>
.reservas-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.header {
  text-align: center;
  margin-bottom: 40px;
}

.header h1 {
  font-size: 2.5rem;
  color: #1f2937;
  margin-bottom: 10px;
}

.subtitle {
  color: #6b7280;
  font-size: 1.1rem;
}

.filters {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  flex-wrap: wrap;
  justify-content: center;
}

.filter-btn {
  padding: 10px 20px;
  border: 2px solid #e5e7eb;
  background: white;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
  color: #6b7280;
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

.loading-state {
  text-align: center;
  padding: 60px 20px;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f4f6;
  border-top: 4px solid #dc2626;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
}

.empty-icon {
  font-size: 5rem;
  margin-bottom: 20px;
}

.empty-state h2 {
  color: #1f2937;
  margin-bottom: 10px;
}

.empty-state p {
  color: #6b7280;
  margin-bottom: 30px;
}

.reservas-list {
  display: flex;
  flex-direction: column;
  gap: 25px;
  margin-bottom: 40px;
}

.reserva-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.reserva-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.reserva-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
  border-bottom: 1px solid #e5e7eb;
  background: #f9fafb;
}

.producto-info {
  display: flex;
  gap: 15px;
  flex: 1;
}

.producto-imagen {
  width: 100px;
  height: 140px;
  object-fit: cover;
  border-radius: 8px;
  border: 2px solid #e5e7eb;
}

.producto-details h3 {
  color: #1f2937;
  margin: 0 0 8px 0;
  font-size: 1.2rem;
}

.producto-categoria,
.producto-editorial {
  color: #6b7280;
  margin: 4px 0;
  font-size: 0.9rem;
}

.producto-categoria {
  display: inline-block;
  background: #e5e7eb;
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

.reserva-status {
  flex-shrink: 0;
}

.status-badge {
  display: inline-block;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.85rem;
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

.status-badge.expirada {
  background-color: #f3f4f6;
  color: #4b5563;
}

.reserva-body {
  padding: 20px;
}

.reserva-details {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-item .label {
  color: #6b7280;
  font-size: 0.85rem;
  font-weight: 500;
}

.detail-item .value {
  color: #1f2937;
  font-weight: 600;
}

.detail-item .value.price {
  color: #dc2626;
  font-size: 1.1rem;
}

.detail-item .value.availability {
  color: #059669;
}

.detail-item .value.timer {
  color: #dc2626;
  font-weight: bold;
  font-size: 1rem;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

.reserva-notas {
  background: #fffbeb;
  border-left: 4px solid #f59e0b;
  padding: 12px 15px;
  margin: 15px 0;
  border-radius: 4px;
}

.reserva-notas p {
  margin: 0;
  color: #92400e;
  font-size: 0.9rem;
}

.availability-progress {
  margin-top: 15px;
}

.progress-label {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 0.85rem;
  color: #6b7280;
}

.progress-percentage {
  font-weight: 600;
  color: #dc2626;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background-color: #e5e7eb;
  border-radius: 10px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #dc2626, #f59e0b);
  transition: width 0.3s ease;
  border-radius: 10px;
}

.reserva-footer {
  display: flex;
  gap: 10px;
  padding: 20px;
  border-top: 1px solid #e5e7eb;
  flex-wrap: wrap;
}

button {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.btn-primary {
  background-color: #dc2626;
  color: white;
  text-decoration: none;
  display: inline-block;
  padding: 12px 30px;
  border-radius: 6px;
}

.btn-primary:hover {
  background-color: #b91c1c;
  transform: translateY(-2px);
}

.btn-secondary {
  background-color: #f3f4f6;
  color: #1f2937;
}

.btn-secondary:hover {
  background-color: #e5e7eb;
}

.btn-danger {
  background-color: #fee2e2;
  color: #991b1b;
}

.btn-danger:hover {
  background-color: #fecaca;
}



.resumen-section {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.resumen-section h3 {
  color: #1f2937;
  margin: 0 0 20px 0;
  font-size: 1.3rem;
}

.resumen-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.resumen-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 15px;
  background: #f9fafb;
  border-radius: 8px;
}

.resumen-label {
  color: #6b7280;
  font-size: 0.9rem;
}

.resumen-value {
  color: #1f2937;
  font-size: 1.5rem;
  font-weight: bold;
}

.resumen-value.price {
  color: #dc2626;
}

@media (max-width: 768px) {
  .header h1 {
    font-size: 2rem;
  }

  .reserva-header {
    flex-direction: column;
    gap: 15px;
  }

  .producto-info {
    flex-direction: column;
  }

  .producto-imagen {
    width: 100%;
    height: 200px;
  }

  .reserva-details {
    grid-template-columns: 1fr;
  }

  .reserva-footer {
    flex-direction: column;
  }

  .reserva-footer button {
    width: 100%;
  }

  .resumen-grid {
    grid-template-columns: 1fr;
  }
}
</style>