<template>
  <div class="events-container">
    <div class="events-wrapper">
      <h1 class="events-title">Eventos Rasengan Comics</h1>
      <p class="events-subtitle">Descubre nuestros próximos eventos y actividades</p>

      <!-- Selector de Mes -->
      <div class="month-selector">
        <button @click="previousMonth" class="nav-btn">← Mes Anterior</button>
        <h2 class="current-month">{{ getCurrentMonthTitle() }}</h2>
        <button @click="nextMonth" class="nav-btn">Siguiente Mes →</button>
      </div>

      <!-- Calendario Mensual -->
      <div class="calendar-container">
        <div class="calendar">
          <!-- Encabezado de días de la semana -->
          <div class="weekdays-header">
            <div v-for="day in ['Lun', 'Mar', 'Mié', 'Jue', 'Vie', 'Sáb', 'Dom']" :key="day" class="weekday">
              {{ day }}
            </div>
          </div>

          <!-- Días del mes -->
          <div class="days-grid-calendar">
            <div 
              v-for="(day, index) in calendarDays" 
              :key="index" 
              :class="['day-cell', { 
                'other-month': !day.isCurrentMonth, 
                'today': day.isToday,
                'has-events': day.events.length > 0
              }]"
            >
              <div class="day-number">{{ day.date }}</div>
              
              <div v-if="day.events.length > 0" class="day-events">
                <div 
                  v-for="event in day.events.slice(0, 2)" 
                  :key="event.id"
                  :class="['event-badge', event.type]"
                  :title="event.name"
                >
                  {{ event.name.substring(0, 12) }}...
                </div>
                <div v-if="day.events.length > 2" class="more-events">
                  +{{ day.events.length - 2 }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Vista detallada de eventos del mes -->
        <div class="events-detail">
          <h3>Eventos de {{ getCurrentMonthTitle() }}</h3>
          
          <div v-if="eventsOfCurrentMonth.length === 0" class="no-events-message">
            <p>📭 No hay eventos programados para este mes</p>
          </div>

          <div v-else class="events-list-detail">
            <div 
              v-for="event in eventsOfCurrentMonth" 
              :key="event.id" 
              :class="['event-card', event.type]"
            >
              <div class="event-date">{{ formatEventDate(event.date) }}</div>
              <div class="event-content">
                <h4 class="event-title">{{ event.name }}</h4>
                <p class="event-desc">{{ event.description }}</p>
                <div class="event-meta">
                  <span class="event-time">🕐 {{ event.time }}</span>
                  <span class="event-location">📍 {{ event.location }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface Event {
  id: number
  name: string
  time: string
  description: string
  type: 'tournament' | 'workshop' | 'special'
  date: Date
  location: string
}

interface CalendarDay {
  date: number
  isCurrentMonth: boolean
  isToday: boolean
  events: Event[]
}

const currentMonth = ref<Date>(new Date())

const generateEvents = (): Event[] => {
  const today = new Date()
  return [
    {
      id: 1,
      name: 'Torneo TCG Magic',
      time: '18:00',
      description: 'Torneo competitivo de Magic The Gathering. Inscripción abierta.',
      type: 'tournament',
      date: new Date(today.getFullYear(), today.getMonth(), 8),
      location: 'Sala Principal',
    },
    {
      id: 2,
      name: 'Taller de Dibujo Manga',
      time: '16:00',
      description: 'Aprende técnicas básicas de manga con nuestros expertos.',
      type: 'workshop',
      date: new Date(today.getFullYear(), today.getMonth(), 15),
      location: 'Aula de Arte',
    },
    {
      id: 3,
      name: 'Noche de Board Games',
      time: '19:00',
      description: 'Juega con otros fanáticos de los juegos de mesa.',
      type: 'special',
      date: new Date(today.getFullYear(), today.getMonth(), 10),
      location: 'Zona Gaming',
    },
    {
      id: 4,
      name: 'Meet & Greet Autores',
      time: '17:00',
      description: 'Conoce a autores de manga locales. Sesión de firmas incluida.',
      type: 'special',
      date: new Date(today.getFullYear(), today.getMonth(), 22),
      location: 'Entrada Principal',
    },
    {
      id: 5,
      name: 'Campeonato Pokémon',
      time: '15:00',
      description: 'Torneo regional de Pokémon TCG con premios.',
      type: 'tournament',
      date: new Date(today.getFullYear(), today.getMonth(), 25),
      location: 'Sala Principal',
    },
    {
      id: 6,
      name: 'Screening Anime',
      time: '20:00',
      description: 'Proyección especial de anime clásicos.',
      type: 'special',
      date: new Date(today.getFullYear(), today.getMonth(), 12),
      location: 'Auditorio',
    },
  ]
}

const allEvents = generateEvents()

const calendarDays = computed((): CalendarDay[] => {
  const year = currentMonth.value.getFullYear()
  const month = currentMonth.value.getMonth()
  
  const firstDay = new Date(year, month, 1)
  const lastDay = new Date(year, month + 1, 0)
  
  const startDate = new Date(firstDay)
  startDate.setDate(startDate.getDate() - firstDay.getDay() + 1)
  
  const days: CalendarDay[] = []
  const today = new Date()
  
  for (let i = 0; i < 42; i++) {
    const date = new Date(startDate)
    date.setDate(date.getDate() + i)
    
    const isCurrentMonth = date.getMonth() === month
    const isToday = date.toDateString() === today.toDateString()
    
    const dayEvents = allEvents.filter(event => 
      event.date.getDate() === date.getDate() &&
      event.date.getMonth() === date.getMonth() &&
      event.date.getFullYear() === date.getFullYear()
    )
    
    days.push({
      date: date.getDate(),
      isCurrentMonth,
      isToday,
      events: dayEvents,
    })
  }
  
  return days
})

const eventsOfCurrentMonth = computed((): Event[] => {
  const month = currentMonth.value.getMonth()
  const year = currentMonth.value.getFullYear()
  
  return allEvents
    .filter(event => 
      event.date.getMonth() === month &&
      event.date.getFullYear() === year
    )
    .sort((a, b) => a.date.getTime() - b.date.getTime())
})

const getCurrentMonthTitle = (): string => {
  return currentMonth.value.toLocaleDateString('es-ES', { month: 'long', year: 'numeric' })
}

const previousMonth = () => {
  currentMonth.value = new Date(currentMonth.value.getFullYear(), currentMonth.value.getMonth() - 1)
}

const nextMonth = () => {
  currentMonth.value = new Date(currentMonth.value.getFullYear(), currentMonth.value.getMonth() + 1)
}

const formatEventDate = (date: Date): string => {
  return date.toLocaleDateString('es-ES', { weekday: 'long', day: 'numeric', month: 'long' })
}
</script>

<style scoped>
.events-container {
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
  min-height: 100vh;
  padding: 40px 20px;
}

.events-wrapper {
  max-width: 1400px;
  margin: 0 auto;
}

.events-title {
  font-size: 36px;
  font-weight: bold;
  color: #1f2937;
  text-align: center;
  margin: 0 0 15px 0;
}

.events-subtitle {
  font-size: 16px;
  color: #6b7280;
  text-align: center;
  margin: 0 0 40px 0;
}

/* Selector de Mes */
.month-selector {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 30px;
  margin-bottom: 40px;
  flex-wrap: wrap;
}

.current-month {
  font-size: 28px;
  font-weight: bold;
  color: #dc2626;
  margin: 0;
  text-transform: capitalize;
  min-width: 250px;
  text-align: center;
}

.nav-btn {
  padding: 10px 20px;
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.nav-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(220, 38, 38, 0.3);
}

/* Contenedor Calendario */
.calendar-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
  margin-bottom: 40px;
}

/* Calendario */
.calendar {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.weekdays-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
  margin-bottom: 15px;
  font-weight: bold;
  color: #dc2626;
  text-align: center;
}

.weekday {
  padding: 10px;
  font-size: 13px;
}

.days-grid-calendar {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 8px;
}

.day-cell {
  aspect-ratio: 1;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  padding: 8px;
  background-color: #fafafa;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  min-height: 80px;
}

.day-cell.other-month {
  background-color: #f3f4f6;
  color: #d1d5db;
}

.day-cell.today {
  background: linear-gradient(135deg, #fecaca 0%, #fee2e2 100%);
  border-color: #dc2626;
  font-weight: bold;
}

.day-cell.has-events {
  border-color: #dc2626;
  background-color: #fffbfb;
}

.day-number {
  font-size: 14px;
  font-weight: bold;
  color: #1f2937;
  margin-bottom: 4px;
}

.day-events {
  display: flex;
  flex-direction: column;
  gap: 3px;
  flex-grow: 1;
}

.event-badge {
  font-size: 10px;
  padding: 3px 6px;
  border-radius: 3px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  color: white;
  font-weight: 600;
}

.event-badge.tournament {
  background-color: #dc2626;
}

.event-badge.workshop {
  background-color: #f59e0b;
}

.event-badge.special {
  background-color: #8b5cf6;
}

.more-events {
  font-size: 10px;
  color: #6b7280;
  text-align: center;
  padding: 2px;
}

/* Vista de Eventos */
.events-detail {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.events-detail h3 {
  font-size: 20px;
  font-weight: bold;
  color: #dc2626;
  margin: 0 0 20px 0;
  padding-bottom: 15px;
  border-bottom: 2px solid #fecaca;
  text-transform: capitalize;
}

.no-events-message {
  text-align: center;
  padding: 40px 20px;
  color: #9ca3af;
  font-size: 16px;
}

.events-list-detail {
  display: flex;
  flex-direction: column;
  gap: 15px;
  max-height: 600px;
  overflow-y: auto;
}

.event-card {
  border-left: 4px solid #dc2626;
  padding: 15px;
  background-color: #fafafa;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.event-card:hover {
  background-color: #f3f4f6;
  transform: translateX(5px);
}

.event-card.tournament {
  border-left-color: #dc2626;
}

.event-card.workshop {
  border-left-color: #f59e0b;
}

.event-card.special {
  border-left-color: #8b5cf6;
}

.event-date {
  font-size: 12px;
  color: #dc2626;
  font-weight: bold;
  text-transform: capitalize;
  margin-bottom: 5px;
}

.event-content {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.event-title {
  font-size: 15px;
  font-weight: bold;
  color: #1f2937;
  margin: 0;
}

.event-desc {
  font-size: 13px;
  color: #6b7280;
  margin: 0;
  line-height: 1.4;
}

.event-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
  font-size: 12px;
  color: #9ca3af;
}

.event-time,
.event-location {
  display: flex;
  align-items: center;
  gap: 6px;
}

/* Responsivo */
@media (max-width: 1024px) {
  .calendar-container {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .events-title {
    font-size: 28px;
  }

  .month-selector {
    gap: 15px;
  }

  .current-month {
    font-size: 24px;
    min-width: auto;
  }

  .calendar {
    padding: 15px;
  }

  .weekday {
    font-size: 11px;
    padding: 8px 4px;
  }

  .day-cell {
    min-height: 60px;
    padding: 6px;
    font-size: 12px;
  }

  .event-badge {
    font-size: 9px;
  }

  .events-detail {
    padding: 15px;
  }

  .events-list-detail {
    max-height: 400px;
  }
}

@media (max-width: 480px) {
  .events-wrapper {
    padding: 0;
  }

  .calendar {
    padding: 10px;
  }

  .days-grid-calendar {
    gap: 4px;
  }

  .weekdays-header {
    gap: 4px;
    margin-bottom: 10px;
  }

  .weekday {
    font-size: 9px;
    padding: 4px 2px;
  }

  .day-cell {
    min-height: 50px;
    padding: 4px;
    font-size: 10px;
  }

  .day-number {
    font-size: 11px;
  }

  .event-badge {
    font-size: 8px;
    padding: 2px 4px;
  }

  .nav-btn {
    padding: 8px 12px;
    font-size: 13px;
  }

  .current-month {
    font-size: 18px;
  }
}
</style>