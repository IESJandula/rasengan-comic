<template>
  <div class="events-container">
    <div class="events-wrapper">
      <h1 class="events-title">Eventos Rasengan Comics</h1>
      <p class="events-subtitle">Descubre nuestros próximos eventos y actividades</p>

      <!-- Semanas -->
      <div class="weeks-container">
        <div v-for="(week, weekIndex) in displayedWeeks" :key="weekIndex" class="week">
          <h2 class="week-title">
            {{ getWeekTitle(weekIndex) }}
          </h2>

          <!-- Días de la semana -->
          <div class="days-grid">
            <div v-for="(day, dayIndex) in week" :key="dayIndex" class="day-card">
              <div class="day-header">
                <h3 class="day-name">{{ day.name }}</h3>
                <span class="day-date">{{ formatDate(day.date) }}</span>
              </div>

              <!-- Eventos del día -->
              <div class="events-list">
                <template v-if="day.events.length > 0">
                  <div
                    v-for="event in day.events"
                    :key="event.id"
                    class="event-item"
                  >
                    <div class="event-time">{{ event.time }}</div>
                    <div class="event-info">
                      <h4 class="event-name">{{ event.name }}</h4>
                      <p class="event-description">{{ event.description }}</p>
                    </div>
                  </div>
                </template>
                <div v-else class="no-events">Sin eventos</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Botón Ver Más -->
      <div class="load-more-container">
        <button v-if="!showAllWeeks" @click="showAllWeeks = true" class="load-more-btn">
          Ver 3 semanas más
        </button>
        <button v-else @click="showAllWeeks = false" class="load-more-btn secondary">
          Mostrar menos
        </button>
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
}

interface Day {
  name: string
  date: Date
  events: Event[]
}

const showAllWeeks = ref(false)

const generateEvents = (): Event[] => {
  return [
    {
      id: 1,
      name: 'Torneo TCG Magic',
      time: '18:00',
      description: 'Torneo competitive de Magic The Gathering',
      type: 'tournament',
    },
    {
      id: 2,
      name: 'Taller de Dibujo Manga',
      time: '16:00',
      description: 'Aprende técnicas básicas de manga',
      type: 'workshop',
    },
    {
      id: 3,
      name: 'Noche de Board Games',
      time: '19:00',
      description: 'Juega con otros fanáticos de los juegos de mesa',
      type: 'special',
    },
    {
      id: 4,
      name: 'Meet & Greet Autores',
      time: '17:00',
      description: 'Conoce a autores de manga locales',
      type: 'special',
    },
    {
      id: 5,
      name: 'Campeonato Pokémon',
      time: '15:00',
      description: 'Torneo regional de Pokémon TCG',
      type: 'tournament',
    },
  ]
}

const generateWeeks = (): Day[][] => {
  const weeks: Day[][] = []
  const today = new Date()
  const allEvents = generateEvents()

  const currentDay = today.getDay()
  const diff = today.getDate() - currentDay + (currentDay === 0 ? -6 : 1)
  const monday = new Date(today.setDate(diff))

  for (let weekNum = 0; weekNum < 4; weekNum++) {
    const week: Day[] = []
    const daysOfWeek: string[] = [
      'Lunes',
      'Martes',
      'Miércoles',
      'Jueves',
      'Viernes',
      'Sábado',
      'Domingo',
    ]

    for (let dayNum = 0; dayNum < 7; dayNum++) {
      const dayName = daysOfWeek[dayNum] || 'Día'

      const date = new Date(monday)
      date.setDate(date.getDate() + weekNum * 7 + dayNum)

      const hasEvent = Math.random() > 0.5
      const randomEventIndex = Math.floor(Math.random() * allEvents.length)
      const randomEvent = allEvents[randomEventIndex]

      const events: Event[] = hasEvent && randomEvent ? [randomEvent] : []

      week.push({
        name: dayName,
        date: new Date(date),
        events: events,
      })
    }

    weeks.push(week)
  }

  return weeks
}

const allWeeks = generateWeeks()

const displayedWeeks = computed(() => {
  return showAllWeeks.value ? allWeeks : [allWeeks[0]]
})

const getWeekTitle = (weekIndex: number): string => {
  const week = allWeeks[weekIndex]

  if (!week || week.length === 0) return ''

  const startDate = week[0]?.date
  const endDate = week[6]?.date

  if (!startDate || !endDate) return ''

  if (weekIndex === 0) {
    return `Esta Semana (${formatDate(startDate)} - ${formatDate(endDate)})`
  } else {
    return `Semana ${weekIndex + 1} (${formatDate(startDate)} - ${formatDate(endDate)})`
  }
}

const formatDate = (date: Date): string => {
  return date.toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit' })
}
</script>

<style scoped>
.events-container {
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
  min-height: 100vh;
  padding: 40px 20px;
}

.events-wrapper {
  max-width: 100%;
  margin: 0 auto;
  padding: 0 20px;
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

.weeks-container {
  display: flex;
  flex-direction: column;
  gap: 40px;
}

.week {
  background-color: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.week-title {
  font-size: 22px;
  font-weight: bold;
  color: #dc2626;
  margin: 0 0 20px 0;
  padding-bottom: 15px;
  border-bottom: 2px solid #fecaca;
}

.days-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 15px;
}

.day-card {
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  padding: 15px;
  transition: all 0.3s ease;
  min-width: 0;
}

.day-card:hover {
  border-color: #dc2626;
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.1);
  transform: translateY(-2px);
}

.day-header {
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #e5e7eb;
}

.day-name {
  font-size: 16px;
  font-weight: bold;
  color: #1f2937;
  margin: 0 0 5px 0;
}

.day-date {
  font-size: 12px;
  color: #9ca3af;
}

.events-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.event-item {
  display: flex;
  gap: 8px;
  padding: 8px;
  background-color: white;
  border-radius: 6px;
  border-left: 3px solid #dc2626;
}

.event-time {
  font-size: 12px;
  font-weight: bold;
  color: #dc2626;
  white-space: nowrap;
}

.event-info {
  flex: 1;
  min-width: 0;
}

.event-name {
  font-size: 13px;
  font-weight: bold;
  color: #1f2937;
  margin: 0 0 3px 0;
  line-height: 1.2;
}

.event-description {
  font-size: 11px;
  color: #6b7280;
  margin: 0;
  line-height: 1.2;
}

.no-events {
  font-size: 12px;
  color: #9ca3af;
  padding: 8px;
  text-align: center;
}

.load-more-container {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

.load-more-btn {
  padding: 14px 40px;
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.load-more-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(220, 38, 38, 0.3);
}

.load-more-btn.secondary {
  background: linear-gradient(135deg, #e5e7eb 0%, #d1d5db 100%);
  color: #1f2937;
}

.load-more-btn.secondary:hover {
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.legend {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-top: 40px;
  padding: 20px;
  background-color: white;
  border-radius: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: #6b7280;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.legend-color.tournament {
  background-color: #dc2626;
}

.legend-color.workshop {
  background-color: #f59e0b;
}

.legend-color.special {
  background-color: #8b5cf6;
}

@media (max-width: 1200px) {
  .days-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 768px) {
  .events-title {
    font-size: 28px;
  }

  .days-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 10px;
  }

  .day-card {
    padding: 12px;
  }

  .legend {
    flex-direction: column;
    gap: 15px;
  }
}

@media (max-width: 480px) {
  .events-wrapper {
    padding: 0;
  }

  .week {
    padding: 20px;
    border-radius: 0;
  }

  .days-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .event-item {
    flex-direction: column;
  }

  .event-time {
    white-space: normal;
  }
}
</style>