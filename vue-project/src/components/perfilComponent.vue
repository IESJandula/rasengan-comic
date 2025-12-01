<template>
  <div class="profile-container">
    <div class="profile-wrapper">
      <!-- Header del Perfil -->
      <div class="profile-header">
        <img :src="user.avatar" :alt="user.name" class="profile-avatar" />
        <div class="profile-info">
          <h1 class="profile-name">{{ user.name }}</h1>
          <p class="profile-email">{{ user.email }}</p>
          <button @click="editProfile" class="edit-btn">Editar Perfil</button>
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

        <!-- Seguridad -->
        <div v-if="activeTab === 'Seguridad'" class="tab-pane">
          <div class="security-section">
            <h3>Cambiar Contraseña</h3>
            <div class="form-group">
              <label>Contraseña Actual</label>
              <input type="password" placeholder="••••••••" />
            </div>
            <div class="form-group">
              <label>Nueva Contraseña</label>
              <input type="password" placeholder="••••••••" />
            </div>
            <div class="form-group">
              <label>Confirmar Contraseña</label>
              <input type="password" placeholder="••••••••" />
            </div>
            <button class="change-password-btn">Cambiar Contraseña</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAuthStore } from '@/stores/authStore'

const authStore = useAuthStore()

const activeTab = ref('Información Personal')

const tabs = ['Información Personal', 'Dirección', 'Preferencias', 'Seguridad']

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

const preferences = ref({
  newsletter: true,
  notifications: true,
  sms: false
})

const editProfile = () => {
  alert('Editar perfil')
}

const editAddress = () => {
  alert('Editar dirección')
}

const addAddress = () => {
  alert('Añadir dirección')
}

const savePreferences = () => {
  alert('Preferencias guardadas')
}
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
}
</style>