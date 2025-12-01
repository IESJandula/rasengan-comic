<template>
  <nav class="navbar">
    <div class="nav-container">
      <router-link to="/" class="navbar-logo">
        <img src="@/assets/img_logo.png" alt="Logo" />
      </router-link>

      <ul class="navbar-menu">
        <li><router-link to="/tienda" class="nav-link">Tienda</router-link></li>
        <li><router-link to="/eventos" class="nav-link">Eventos</router-link></li>
        <li v-if="authStore.isAuthenticated"><router-link to="/reservas" class="nav-link">Reservas</router-link></li>
      </ul>

      <!-- Barra de búsqueda -->
      <div class="search-bar">
        <div class="search-wrapper">
          <span class="search-icon">🔍</span>
          <input 
            v-model="searchQuery"
            type="text"
            placeholder="Buscar comics, manga, merchandising..."
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <button 
            v-if="searchQuery" 
            @click="clearSearch" 
            class="clear-button"
          >
            ✕
          </button>
        </div>
      </div>

      <div class="user-section">
        <!-- Carrito de Compras  -->
          <router-link to="/carrito" class="cart-link" title="Carrito">
            🛒
            <span v-if="cartCount > 0" class="cart-badge">{{ cartCount }}</span>
          </router-link>


        <!-- Usuario Logueado -->
        <div v-if="authStore.isAuthenticated && authStore.user" class="user-logged">
          <div class="user-avatar-wrapper">
            <img 
              :src="userAvatar" 
              :alt="authStore.user.name" 
              class="user-avatar"
              @error="handleImageError"
            />
          </div>
          
          <div class="dropdown-menu">
            <div class="user-info">
              <p class="user-name">{{ authStore.user.name }}</p>
              <p class="user-email">{{ authStore.user.email }}</p>
            </div>
            <router-link to="/perfil" class="dropdown-item">👤 Mi Perfil</router-link>
            <router-link to="/carrito" class="dropdown-item">🛒 Carrito</router-link>
            <router-link to="/reservas" class="dropdown-item">📅 Mis Reservas</router-link>
            <router-link v-if="isAdmin" to="/admin" class="dropdown-item admin-link">⚙️ Admin Panel</router-link>
            <div class="dropdown-divider"></div>
            <button @click="logout" class="dropdown-item logout">🚪 Cerrar Sesión</button>
          </div>
        </div>

        <!-- Sin Sesión -->
        <div v-else class="user-not-logged">
          <button @click="goToLogin" class="login-button">👤 Iniciar Sesión</button>
          <button @click="goToRegister" class="register-button">📝 Registrarse</button>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useAuthStore } from '@/stores/authStore'
import { useRouter } from 'vue-router'

const authStore = useAuthStore()
const router = useRouter()

const cartCount = ref(3)
const imageError = ref(false)
const searchQuery = ref('')

// Avatar por defecto
const defaultAvatar = 'https://ui-avatars.com/api/?name=Usuario&background=dc2626&color=fff&size=128'

// Computed para obtener el avatar del usuario o usar el por defecto
const userAvatar = computed(() => {
  if (imageError.value) {
    return defaultAvatar
  }
  
  if (authStore.user?.avatar) {
    return authStore.user.avatar
  }
  
  // Generar avatar con el nombre del usuario si está disponible
  if (authStore.user?.name) {
    const name = encodeURIComponent(authStore.user.name)
    return `https://ui-avatars.com/api/?name=${name}&background=dc2626&color=fff&size=128`
  }
  
  return defaultAvatar
})

// Manejar error de carga de imagen
const handleImageError = () => {
  imageError.value = true
}

// Verificar si es admin
const isAdmin = computed(() => {
  return authStore.user?.email === 'admin@rasengacomics.com'
})

const logout = (): void => {
  authStore.logout()
  imageError.value = false // Reset error state
  router.push('/')
}

const goToLogin = (): void => {
  console.log('Navegando a login...')
  router.push('/login')
}

const goToRegister = (): void => {
  console.log('Navegando a registro...')
  router.push('/registro')
}

const handleSearch = (): void => {
  if (searchQuery.value.trim()) {
    console.log('Buscando:', searchQuery.value)
    router.push({ path: '/tienda', query: { q: searchQuery.value } })
    searchQuery.value = ''
  }
}

const clearSearch = (): void => {
  searchQuery.value = ''
}
</script>

<style scoped>
.navbar {
  background-color: #dc2626;
  padding: 15px 0;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 30px;
}

.navbar-logo {
  display: flex;
  align-items: center;
  text-decoration: none;
  flex-shrink: 0;
}

.navbar-logo img {
  height: 50px;
  width: auto;
  cursor: pointer;
  transition: transform 0.3s ease;
  border: 3px solid black;
  border-radius: 100%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.5);
}

.navbar-logo:hover img {
  transform: scale(1.05);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.7);
}

.navbar-menu {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
  gap: 30px;
  flex-shrink: 0;
}

.nav-link {
  color: white;
  text-decoration: none;
  font-size: 16px;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

.nav-link:hover {
  background-color: #b91c1c;
}

.search-bar {
  display: flex;
  align-items: center;
  flex: 1;
  max-width: 600px;
  margin: 0 30px;
}

.search-wrapper {
  position: relative;
  width: 100%;
  display: flex;
  align-items: center;
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 30px;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.search-wrapper:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  background-color: rgba(255, 255, 255, 1);
}

.search-wrapper:focus-within {
  box-shadow: 0 4px 16px rgba(220, 38, 38, 0.2);
  background-color: rgba(255, 255, 255, 1);
}

.search-icon {
  font-size: 18px;
  color: #9ca3af;
  margin-right: 10px;
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  padding: 12px 0;
  border: none;
  background: transparent;
  font-size: 14px;
  outline: none;
  color: #1f2937;
  font-weight: 500;
}

.search-input::placeholder {
  color: #9ca3af;
  font-weight: 400;
}

.clear-button {
  padding: 4px;
  border: none;
  background: #e5e7eb;
  color: #6b7280;
  border-radius: 50%;
  cursor: pointer;
  font-size: 14px;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  flex-shrink: 0;
  margin-left: 8px;
}

.clear-button:hover {
  background-color: #dc2626;
  color: white;
  transform: scale(1.1);
}

.user-section {
  display: flex;
  align-items: center;
  gap: 15px;
}

.cart-icon {
  position: relative;
}

.cart-link {
  font-size: 24px;
  color: white;
  text-decoration: none;
  position: relative;
  transition: transform 0.3s ease;
  display: inline-block;
}

.cart-link:hover {
  transform: scale(1.15);
}

.cart-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background-color: #fbbf24;
  color: #1f2937;
  border-radius: 50%;
  min-width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  padding: 0 4px;
}

.user-logged {
  position: relative;
  display: flex;
  align-items: center;
}

.user-avatar-wrapper {
  cursor: pointer;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid white;
  transition: transform 0.3s ease;
  cursor: pointer;
  object-fit: cover;
  background-color: #dc2626;
}

.user-avatar:hover {
  transform: scale(1.1);
}

.dropdown-menu {
  position: absolute;
  top: 55px;
  right: 0;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  min-width: 240px;
  overflow: hidden;
  z-index: 1000;
  opacity: 0;
  visibility: hidden;
  transform: translateY(-10px);
  transition: all 0.3s ease;
}

.user-logged:hover .dropdown-menu {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.user-info {
  padding: 16px;
  border-bottom: 1px solid #e5e7eb;
  background-color: #f9fafb;
}

.user-name {
  font-weight: bold;
  color: #1f2937;
  margin: 0 0 4px 0;
  font-size: 15px;
}

.user-email {
  color: #6b7280;
  font-size: 13px;
  margin: 0;
}

.dropdown-item {
  display: block;
  width: 100%;
  padding: 12px 16px;
  text-align: left;
  color: #1f2937;
  text-decoration: none;
  background: none;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s ease;
  font-size: 14px;
}

.dropdown-item:hover {
  background-color: #f3f4f6;
}

.dropdown-item.logout {
  color: #dc2626;
  font-weight: 600;
}

.dropdown-item.logout:hover {
  background-color: #fee2e2;
}

.dropdown-item.admin-link {
  color: #7c3aed;
  font-weight: 600;
}

.dropdown-item.admin-link:hover {
  background-color: #f3e8ff;
}

.dropdown-divider {
  height: 1px;
  background-color: #e5e7eb;
  margin: 0;
}

.user-not-logged {
  display: flex;
  gap: 10px;
  align-items: center;
}

.login-button,
.register-button {
  border: none;
  border-radius: 4px;
  font-size: 14px;
  padding: 8px 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.login-button {
  color: white;
  background-color: transparent;
}

.login-button:hover {
  background-color: #b91c1c;
}

.register-button {
  background-color: white;
  color: #dc2626;
}

.register-button:hover {
  background-color: #f3f4f6;
  transform: translateY(-2px);
}

@media (max-width: 1024px) {
  .navbar-menu {
    gap: 20px;
  }

  .nav-link {
    font-size: 14px;
    padding: 6px 10px;
  }

  .search-bar {
    max-width: 350px;
    margin: 0 15px;
  }

  .search-wrapper {
    padding: 0 15px;
  }

  .search-input {
    font-size: 13px;
    padding: 10px 0;
  }

  .search-icon {
    font-size: 16px;
  }
}

@media (max-width: 768px) {
  .navbar {
    padding: 12px 0;
  }

  .nav-container {
    gap: 12px;
    padding: 0 15px;
  }

  .navbar-logo img {
    height: 40px;
    border: 2px solid black;
  }

  .navbar-menu {
    flex: initial;
    gap: 8px;
    justify-content: flex-start;
  }

  .nav-link {
    font-size: 14px;
    padding: 6px 10px;
  }

  .search-bar {
    display: none;
  }

  .user-section {
    gap: 10px;
  }

  .cart-link {
    font-size: 22px;
  }

  .cart-badge {
    width: 18px;
    height: 18px;
    font-size: 11px;
    top: -6px;
    right: -6px;
  }

  .user-avatar {
    width: 36px;
    height: 36px;
  }

  .dropdown-menu {
    min-width: 220px;
    right: -10px;
    top: 48px;
  }

  .user-not-logged {
    gap: 8px;
  }

  .login-button,
  .register-button {
    font-size: 13px;
    padding: 7px 14px;
  }
}

@media (max-width: 480px) {
  .navbar {
    padding: 10px 0;
  }

  .nav-container {
    gap: 10px;
    padding: 0 12px;
    flex-wrap: wrap;
  }

  .navbar-logo {
    order: 1;
  }

  .user-section {
    order: 2;
    margin-left: auto;
  }

  .navbar-menu {
    order: 3;
    width: 100%;
    justify-content: center;
    padding-top: 8px;
    border-top: 1px solid rgba(255, 255, 255, 0.2);
  }

  .search-bar {
    order: 4;
    display: flex;
    width: 100%;
    max-width: 100%;
    margin: 8px 0 0 0;
    padding-top: 8px;
    border-top: 1px solid rgba(255, 255, 255, 0.2);
  }

  .search-wrapper {
    padding: 0 12px;
  }

  .search-input {
    padding: 10px 0;
    font-size: 13px;
  }

  .search-icon {
    font-size: 15px;
    margin-right: 8px;
  }

  .clear-button {
    width: 22px;
    height: 22px;
    font-size: 12px;
  }

  .navbar-logo img {
    height: 36px;
  }

  .nav-link {
    font-size: 13px;
    padding: 6px 12px;
  }

  .cart-link {
    font-size: 20px;
  }

  .user-avatar {
    width: 34px;
    height: 34px;
  }

  .dropdown-menu {
    min-width: 200px;
    right: 0;
    top: 45px;
    left: auto;
    transform-origin: top right;
  }

  .user-info {
    padding: 12px;
  }

  .user-name {
    font-size: 14px;
  }

  .user-email {
    font-size: 12px;
  }

  .dropdown-item {
    padding: 10px 12px;
    font-size: 13px;
  }

  .user-not-logged {
    gap: 6px;
  }

  .login-button {
    padding: 6px 10px;
    font-size: 12px;
  }

  .register-button {
    padding: 6px 12px;
    font-size: 12px;
  }
}

@media (max-width: 360px) {
  .nav-container {
    padding: 0 10px;
  }

  .navbar-logo img {
    height: 32px;
  }

  .nav-link {
    font-size: 12px;
    padding: 5px 10px;
  }

  .login-button,
  .register-button {
    font-size: 11px;
    padding: 5px 8px;
  }

  .dropdown-menu {
    min-width: 180px;
  }
}
</style>