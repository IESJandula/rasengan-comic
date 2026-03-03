<template>
  <nav class="navbar">
    <!-- Overlay para cerrar el menú al hacer clic fuera -->
    <div class="menu-overlay" :class="{ active: isMenuOpen }" @click="closeMenu"></div>
    
    <div class="nav-container">
      <!-- Botón hamburguesa para móvil -->
      <button class="hamburger-button" @click="toggleMenu" :class="{ active: isMenuOpen }">
        <span></span>
        <span></span>
        <span></span>
      </button>

      <router-link to="/" class="navbar-logo">
        <img src="@/assets/img_logo.png" alt="Logo" />
      </router-link>

      <ul class="navbar-menu" :class="{ 'menu-open': isMenuOpen }">
        <li class="mobile-home-item"><router-link to="/" class="nav-link" @click="closeMenu">🏠 Inicio</router-link></li>
        <li><router-link to="/tienda" class="nav-link" @click="closeMenu">Tienda</router-link></li>
        <li><router-link to="/eventos" class="nav-link" @click="closeMenu">Eventos</router-link></li>
        
        <!-- Carrito en móvil -->
        <li class="mobile-cart-item">
          <router-link to="/carrito" class="nav-link mobile-cart-link" @click="closeMenu">
            🛒 Carrito
            <span v-if="cartCount > 0" class="mobile-cart-count">({{ cartCount }})</span>
          </router-link>
        </li>

        <!-- Opciones de usuario en móvil -->
        <li v-if="authStore.isAuthenticated && authStore.user" class="mobile-user-item">
          <router-link to="/perfil" class="nav-link" @click="closeMenu">👤 Mi Perfil</router-link>
          <router-link to="/perfil?tab=compras" class="nav-link" @click="closeMenu">🛍️ Mis Compras</router-link>
          <router-link to="/perfil?tab=reservas" class="nav-link" @click="closeMenu">📅 Mis Reservas</router-link>
          <router-link v-if="isAdmin" to="/admin" class="nav-link" @click="closeMenu">⚙️ Admin Panel</router-link>
          <button @click="logout(); closeMenu()" class="nav-link logout-link">🚪 Cerrar Sesión</button>
        </li>

        <li v-if="!authStore.isAuthenticated" class="mobile-auth-item">
          <button @click="goToLogin(); closeMenu()" class="mobile-login-button">👤 Iniciar Sesión</button>
          <button @click="goToRegister(); closeMenu()" class="mobile-register-button">📝 Registrarse</button>
        </li>
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
            @input="performLiveSearch(searchQuery)"
          />
          <button 
            v-if="searchQuery" 
            @click="clearSearch" 
            class="clear-button"
          >
            ✕
          </button>
          
          <!-- Resultados de búsqueda en vivo -->
          <div v-if="showSearchResults && searchResults.length > 0" class="search-results">
            <div 
              v-for="product in searchResults" 
              :key="product.id"
              class="search-result-item"
              @click="selectSearchResult(product)"
            >
              <div class="result-info">
                <div class="result-name">{{ product.name }}</div>
                <div class="result-category">{{ product.category }}</div>
                <div class="result-price">{{ product.price }}€</div>
              </div>
            </div>
          </div>
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
            <router-link to="/perfil?tab=compras" class="dropdown-item">🛍️ Mis Compras</router-link>
            <router-link to="/perfil?tab=reservas" class="dropdown-item">📅 Mis Reservas</router-link>
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
import { useCartStore } from '@/stores/cartStore'
import { useRouter } from 'vue-router'
import api from '@/api/axios'

const authStore = useAuthStore()
const cartStore = useCartStore()
const router = useRouter()

const cartCount = computed(() => cartStore.totalItems)
const imageError = ref(false)
const searchQuery = ref('')
const searchResults = ref<any[]>([])
const showSearchResults = ref(false)
const isMenuOpen = ref(false)

const toggleMenu = (): void => {
  isMenuOpen.value = !isMenuOpen.value
}

const closeMenu = (): void => {
  isMenuOpen.value = false
}

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
    router.push({ path: '/catalogo', query: { q: searchQuery.value } })
    searchQuery.value = ''
    showSearchResults.value = false
  }
}

const clearSearch = (): void => {
  searchQuery.value = ''
  searchResults.value = []
  showSearchResults.value = false
}

// Búsqueda en vivo desde la API
const performLiveSearch = async (query: string): Promise<void> => {
  if (query.length >= 3) {
    try {
      const response = await api.get('/api/products')
      const products = response.data as any[]
      
      const filtered = products.filter((product) =>
        product.name.toLowerCase().includes(query.toLowerCase()) ||
        product.category.toLowerCase().includes(query.toLowerCase()) ||
        (product.subcategory && product.subcategory.toLowerCase().includes(query.toLowerCase()))
      )
      
      searchResults.value = filtered.slice(0, 5) // Mostrar máximo 5 resultados
      showSearchResults.value = true
    } catch (err) {
      console.error('Error en búsqueda:', err)
      searchResults.value = []
    }
  } else {
    searchResults.value = []
    showSearchResults.value = false
  }
}

const selectSearchResult = (product: any): void => {
  router.push({ path: '/catalogo', query: { q: product.name } })
  searchQuery.value = ''
  showSearchResults.value = false
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

.menu-overlay {
  display: none;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.menu-overlay.active {
  display: block;
  opacity: 1;
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

/* Botón hamburguesa */
.hamburger-button {
  display: none;
  flex-direction: column;
  justify-content: space-around;
  width: 30px;
  height: 25px;
  background: transparent;
  border: none;
  cursor: pointer;
  padding: 0;
  z-index: 1001;
}

.hamburger-button span {
  width: 100%;
  height: 3px;
  background-color: white;
  border-radius: 3px;
  transition: all 0.3s ease;
  transform-origin: center;
}

.hamburger-button.active span:nth-child(1) {
  transform: rotate(45deg) translateY(8px);
}

.hamburger-button.active span:nth-child(2) {
  opacity: 0;
  transform: translateX(-20px);
}

.hamburger-button.active span:nth-child(3) {
  transform: rotate(-45deg) translateY(-8px);
}

.navbar-menu {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
  gap: 30px;
  flex-shrink: 0;
}

.mobile-auth-item,
.mobile-home-item,
.mobile-cart-item,
.mobile-user-item {
  display: none;
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

.search-wrapper {
  position: relative;
}

.search-results {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #e5e7eb;
  border-top: none;
  border-radius: 0 0 8px 8px;
  max-height: 400px;
  overflow-y: auto;
  z-index: 50;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.search-result-item {
  display: flex;
  gap: 12px;
  padding: 10px 12px;
  border-bottom: 1px solid #f3f4f6;
  cursor: pointer;
  transition: background-color 0.2s ease;
  align-items: center;
}

.search-result-item:last-child {
  border-bottom: none;
}

.search-result-item:hover {
  background-color: #f9fafb;
}

.result-info {
  flex: 1;
}

.result-name {
  font-weight: 600;
  color: #1f2937;
  font-size: 13px;
  line-height: 1.3;
}

.result-category {
  font-size: 12px;
  color: #9ca3af;
  margin-top: 2px;
}

.result-price {
  font-weight: 600;
  color: #dc2626;
  font-size: 12px;
  margin-top: 2px;
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
    position: relative;
    justify-content: flex-start;
  }

  /* Ocultar logo en móviles */
  .navbar-logo {
    display: none;
  }

  /* Mostrar botón hamburguesa a la izquierda */
  .hamburger-button {
    display: flex;
    order: -1;
  }

  /* Buscador en móviles */
  .search-bar {
    flex: 1;
    max-width: none;
    margin: 0;
    margin-left: auto;
  }

  .search-wrapper {
    padding: 0 12px;
  }

  .search-input {
    font-size: 13px;
    padding: 10px 0;
  }

  .search-input::placeholder {
    font-size: 12px;
  }

  .search-icon {
    font-size: 16px;
  }

  /* Menú móvil desde la izquierda */
  .navbar-menu {
    position: fixed;
    top: 0;
    left: -100%;
    height: 100vh;
    width: 280px;
    background-color: #dc2626;
    flex-direction: column;
    gap: 0;
    padding: 80px 0 20px 0;
    box-shadow: 4px 0 12px rgba(0, 0, 0, 0.3);
    transition: left 0.3s ease;
    z-index: 1000;
    overflow-y: auto;
  }

  .navbar-menu.menu-open {
    left: 0;
  }

  .navbar-menu li {
    width: 100%;
  }

  .nav-link {
    display: block;
    width: 100%;
    font-size: 16px;
    padding: 16px 24px;
    border-radius: 0;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  }

  .nav-link:hover {
    background-color: #b91c1c;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  }

  /* Mostrar auth, inicio, carrito y usuario en móvil */
  .mobile-auth-item,
  .mobile-home-item,
  .mobile-cart-item,
  .mobile-user-item {
    display: block;
    width: 100%;
    padding: 16px 24px;
  }

  .mobile-home-item,
  .mobile-cart-item {
    padding: 0;
    border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  }

  .mobile-cart-link {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .mobile-cart-count {
    background-color: #fbbf24;
    color: #1f2937;
    padding: 2px 8px;
    border-radius: 12px;
    font-size: 13px;
    font-weight: bold;
  }

  .mobile-user-item {
    display: flex;
    flex-direction: column;
    gap: 0;
    padding: 0;
    border-top: 1px solid rgba(255, 255, 255, 0.2);
    border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  }

  .mobile-user-item .nav-link {
    border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  }

  .mobile-user-item .nav-link:last-child {
    border-bottom: none;
  }

  .logout-link {
    width: 100%;
    text-align: left;
    background: none;
    cursor: pointer;
    font-family: inherit;
  }

  .logout-link:hover {
    background-color: #b91c1c;
  }

  .mobile-auth-item {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .mobile-login-button,
  .mobile-register-button {
    width: 100%;
    border: none;
    border-radius: 6px;
    font-size: 15px;
    padding: 12px 16px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
  }

  .mobile-login-button {
    color: #dc2626;
    background-color: white;
  }

  .mobile-login-button:hover {
    background-color: #f3f4f6;
  }

  .mobile-register-button {
    background-color: transparent;
    color: white;
    border: 2px solid white;
  }

  .mobile-register-button:hover {
    background-color: rgba(255, 255, 255, 0.1);
  }

  /* Ocultar búsqueda desktop */
  .desktop-search {
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
    display: none;
  }

  /* Ocultar carrito y sección de usuario en móviles */
  .user-section {
    display: none;
  }
}

@media (max-width: 480px) {
  .navbar {
    padding: 10px 0;
  }

  .nav-container {
    gap: 10px;
    padding: 0 12px;
  }

  .navbar-logo img {
    height: 36px;
  }

  .navbar-menu {
    width: 260px;
  }

  .nav-link {
    font-size: 15px;
    padding: 14px 20px;
  }

  .mobile-auth-item {
    padding: 12px 20px;
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
}

@media (max-width: 360px) {
  .nav-container {
    padding: 0 10px;
  }

  .navbar-logo img {
    height: 32px;
  }

  .navbar-menu {
    width: 240px;
  }

  .nav-link {
    font-size: 14px;
    padding: 12px 16px;
  }

  .mobile-auth-item {
    padding: 10px 16px;
  }

  .dropdown-menu {
    min-width: 180px;
  }
}
</style>