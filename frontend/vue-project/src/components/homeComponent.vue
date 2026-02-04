<template>
  <div class="home-container">
    <!-- Carrusel -->
    <div class="carousel-wrapper">
      <div class="carousel-container">
        <img
          v-for="(slide, index) in slides"
          :key="index"
          :src="slide"
          :alt="`Slide ${index + 1}`"
          :class="[
            'carousel-image',
            index === currentSlide ? 'active' : ''
          ]"
        />
      </div>

      <!-- Botones del carrusel -->
      <button
        @click="prevSlide"
        class="carousel-button carousel-button-left"
      >
        ◄
      </button>
      <button
        @click="nextSlide"
        class="carousel-button carousel-button-right"
      >
        ►
      </button>

      <!-- Indicadores -->
      <div class="carousel-indicators">
        <button
          v-for="(_, index) in slides"
          :key="index"
          @click="currentSlide = index"
          :class="['indicator', index === currentSlide ? 'active' : '']"
        />
      </div>
    </div>

    <!-- Secciones -->
    <div class="content-wrapper">
      <!-- Productos Más Comprados -->
      <section class="section">
        <h2 class="section-title">
          🏆 PRODUCTOS MÁS COMPRADOS
        </h2>
        <div class="products-grid">
          <div
            v-for="item in productosMasComprados"
            :key="item.id"
            class="product-card"
            @click="viewProduct(item.id)"
            style="cursor: pointer;"
          >
            <img
              :src="resolveImageUrl(item.image)"
              :alt="item.name"
              class="product-image"
            />
            <div class="product-info">
              <h3 class="product-name">{{ item.name }}</h3>
              <p class="product-category">{{ item.category }}</p>
              <p class="product-price">{{ formatPrice(item.price) }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- Reservas -->
      <section class="section">
        <h2 class="section-title">
          📖 PRODUCTOS DISPONIBLES PARA RESERVA
        </h2>
        <div class="reservas-card">
          <p class="reservas-subtitle">Próximos lanzamientos y productos especiales</p>
          
          <!-- Loading -->
          <div v-if="loadingReservas" class="loading-message">
            Cargando productos disponibles...
          </div>

          <!-- Lista de productos para reservar -->
          <div v-else-if="productosReserva.length > 0" class="reservas-list">
            <div
              v-for="producto in productosReserva.slice(0, 3)"
              :key="producto.id"
              class="reserva-item"
            >
              <img 
                :src="resolveImageUrl(producto.image) || 'https://via.placeholder.com/80'" 
                :alt="producto.name"
                class="reserva-image"
              />
              <div class="reserva-info">
                <h4 class="reserva-title">{{ producto.name }}</h4>
                <p class="reserva-category">{{ producto.category }}</p>
                <p class="reserva-price">{{ formatPrice(producto.price) }}</p>
              </div>
              <button 
                @click="handleReservar(producto)" 
                class="reserva-button"
              >
                Reservar
              </button>
            </div>
          </div>

          <!-- Sin productos -->
          <div v-else class="no-events-message">
            <p>No hay productos disponibles para reserva en este momento</p>
          </div>
        </div>
      </section>

      <!-- Últimos Productos por Categoría -->
      <section class="section">
        
        <!-- Juegos de Mesa -->
        <div class="category-section">
          <h3 class="category-section-title">Juegos de Mesa</h3>
          <div class="category-products-grid">
            <div
              v-for="item in ultimosJuegosMesa"
              :key="item.id"
              class="product-card"
              @click="viewProduct(item.id)"
              style="cursor: pointer;"
            >
              <img
                :src="item.imagen"
                :alt="item.nombre"
                class="product-image"
              />
              <div class="product-info">
                <h3 class="product-name">{{ item.nombre }}</h3>
                <p class="product-category">{{ item.subcategoria }}</p>
                <p class="product-price">{{ item.precio }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- TCG -->
        <div class="category-section">
          <h3 class="category-section-title">TCG</h3>
          <div class="category-products-grid">
            <div
              v-for="item in ultimosTCG"
              :key="item.id"
              class="product-card"
              @click="viewProduct(item.id)"
              style="cursor: pointer;"
            >
              <img
                :src="item.imagen"
                :alt="item.nombre"
                class="product-image"
              />
              <div class="product-info">
                <h3 class="product-name">{{ item.nombre }}</h3>
                <p class="product-category">{{ item.subcategoria }}</p>
                <p class="product-price">{{ item.precio }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Comics -->
        <div class="category-section">
          <h3 class="category-section-title">Comics</h3>
          <div class="category-products-grid">
            <div
              v-for="item in ultimosComics"
              :key="item.id"
              class="product-card"
              @click="viewProduct(item.id)"
              style="cursor: pointer;"
            >
              <img
                :src="item.imagen"
                :alt="item.nombre"
                class="product-image"
              />
              <div class="product-info">
                <h3 class="product-name">{{ item.nombre }}</h3>
                <p class="product-category">{{ item.subcategoria }}</p>
                <p class="product-price">{{ item.precio }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Manga -->
        <div class="category-section">
          <h3 class="category-section-title">Manga</h3>
          <div class="category-products-grid">
            <div
              v-for="item in ultimosManga"
              :key="item.id"
              class="product-card"
              @click="viewProduct(item.id)"
              style="cursor: pointer;"
            >
              <img
                :src="item.imagen"
                :alt="item.nombre"
                class="product-image"
              />
              <div class="product-info">
                <h3 class="product-name">{{ item.nombre }}</h3>
                <p class="product-category">{{ item.subcategoria }}</p>
                <p class="product-price">{{ item.precio }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Figuras -->
        <div class="category-section">
          <h3 class="category-section-title">Figuras</h3>
          <div class="category-products-grid">
            <div
              v-for="item in ultimasFiguras"
              :key="item.id"
              class="product-card"
              @click="viewProduct(item.id)"
              style="cursor: pointer;"
            >
              <img
                :src="item.imagen"
                :alt="item.nombre"
                class="product-image"
              />
              <div class="product-info">
                <h3 class="product-name">{{ item.nombre }}</h3>
                <p class="product-category">{{ item.subcategoria }}</p>
                <p class="product-price">{{ item.precio }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Accesorios -->
        <div class="category-section">
          <h3 class="category-section-title">Accesorios</h3>
          <div class="category-products-grid">
            <div
              v-for="item in ultimosAccesorios"
              :key="item.id"
              class="product-card"
              @click="viewProduct(item.id)"
              style="cursor: pointer;"
            >
              <img
                :src="item.imagen"
                :alt="item.nombre"
                class="product-image"
              />
              <div class="product-info">
                <h3 class="product-name">{{ item.nombre }}</h3>
                <p class="product-category">{{ item.subcategoria }}</p>
                <p class="product-price">{{ item.precio }}</p>
              </div>
            </div>
          </div>
        </div>
      </section>
    </div>

    <!-- Modal de Autenticación Requerida -->
    <div v-if="showAuthModal" class="auth-modal-overlay">
      <div class="auth-modal">
        <button @click="closeAuthModal" class="modal-close-btn">✕</button>
        
        <div class="modal-content">
          <div class="modal-icon">🔐</div>
          <h2>Inicia sesión para reservar</h2>
          <p>Necesitas tener una cuenta activa para hacer una reserva de evento.</p>
          
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
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/authStore'
import { useCartStore } from '@/stores/cartStore'
import api from '@/api/axios'

const router = useRouter();
const authStore = useAuthStore()
const cartStore = useCartStore()

// Importar imágenes desde src/assets/images/
import banner1 from '@/assets/images/banner1.webp';
import banner2 from '@/assets/images/banner2.webp';
import banner3 from '@/assets/images/banner3.webp';

// Función para cargar imágenes desde delete_inicio
const getImageUrl = (imageName: string) => {
  return new URL(`../assets/delete_inicio/${imageName}`, import.meta.url).href
}

const resolveImageUrl = (image?: string): string => {
  if (!image) return ''
  if (
    image.startsWith('http://') ||
    image.startsWith('https://') ||
    image.startsWith('data:') ||
    image.startsWith('blob:')
  ) {
    return image
  }
  return new URL(`../assets/delete_inicio/${image}`, import.meta.url).href
}

const currentSlide = ref(0);

// Imágenes del carrusel
const slides = [
  banner1,
  banner2,
  banner3,
];

// Productos más comprados (cargados desde API)
const productosMasComprados = ref<any[]>([])
const allProducts = ref<any[]>([])

// Reservas del usuario
const reservasUsuario = ref<any[]>([])

// Productos disponibles para reservar
const productosReserva = ref<any[]>([])
const loadingReservas = ref(false)
const showAuthModal = ref(false)
const selectedProduct = ref<any>(null)

// Función para formatear precio
const formatPrice = (price: number): string => {
  return `${price.toFixed(2)}€`
}

const normalizeText = (value: string | undefined | null): string => {
  return (value || '').toLowerCase().trim()
}

const mapToHomeItem = (product: any) => ({
  id: product.id,
  nombre: product.name,
  precio: formatPrice(product.price),
  imagen: resolveImageUrl(product.image) || product.image,
  subcategoria: product.subcategory || product.category
})

const setCategoryItems = (categoryKeys: string[], target: { value: any[] }) => {
  const items = allProducts.value
    .filter((p: any) => categoryKeys.some((k) => normalizeText(p.category).includes(k)))
    .slice(0, 3)
    .map(mapToHomeItem)

  if (items.length > 0) {
    target.value = items
  }
}

// Cargar productos más comprados desde la API
const loadProductosMasComprados = async () => {
  try {
    const response = await api.get('/api/products')
    allProducts.value = response.data.map((p: any) => ({
      ...p,
      image: resolveImageUrl(p.image) || p.image
    }))
    // Tomar los primeros 3 productos
    productosMasComprados.value = allProducts.value.slice(0, 3).map((p: any) => ({
      id: p.id,
      name: p.name,
      category: p.category,
      price: p.price,
      image: p.image || 'https://via.placeholder.com/200'
    }))

    setCategoryItems(['juegos', 'mesa'], ultimosJuegosMesa)
    setCategoryItems(['tcg'], ultimosTCG)
    setCategoryItems(['comics'], ultimosComics)
    setCategoryItems(['manga'], ultimosManga)
    setCategoryItems(['figuras', 'figura'], ultimasFiguras)
    setCategoryItems(['accesorios', 'accesorio'], ultimosAccesorios)
    console.log('✅ Productos más comprados cargados:', productosMasComprados.value.length)
  } catch (err) {
    console.error('❌ Error al cargar productos:', err)
    // Si falla, mostrar los datos por defecto
    productosMasComprados.value = [
      {
        id: 1,
        name: 'One Piece Vol. 100',
        category: 'Manga',
        price: 12.99,
        image: getImageUrl('one-piece-vol100.jpg')
      },
      {
        id: 2,
        name: 'Magic Commander',
        category: 'TCG',
        price: 159.99,
        image: getImageUrl('magicComander.jpg')
      },
      {
        id: 3,
        name: 'Funko Goku',
        category: 'Figuras',
        price: 14.99,
        image: getImageUrl('funko-goku.jpg')
      }
    ]
  }
}

// Productos favoritos
const productos = [
  {
    id: 1,
    nombre: 'One Piece Vol. 100',
    precio: '12.99€',
    imagen: getImageUrl('one-piece-vol100.jpg'),
    categoria: 'Manga'
  },
  {
    id: 2,
    nombre: 'Magic Commander',
    precio: '159.99€',
    imagen: getImageUrl('magicComander.jpg'),
    categoria: 'TCG'
  },
  {
    id: 3,
    nombre: 'Funko Goku',
    precio: '14.99€',
    imagen: getImageUrl('funko-goku.jpg'),
    categoria: 'Figuras'
  },
  {
    id: 4,
    nombre: 'Catan Base Game',
    precio: '49.99€',
    imagen: getImageUrl('catan-base-game.jpg'),
    categoria: 'Juegos de Mesa'
  }
];

// Últimos productos por categoría
const ultimosJuegosMesa = ref<any[]>([
  {
    id: 101,
    nombre: 'Wingspan',
    precio: '54.99€',
    imagen: getImageUrl('wingspan.jpg'),
    subcategoria: 'Estrategia'
  },
  {
    id: 102,
    nombre: 'Ticket to Ride',
    precio: '44.99€',
    imagen: getImageUrl('ticket to ride.jpg'),
    subcategoria: 'Familiar'
  },
  {
    id: 103,
    nombre: 'Pandemic',
    precio: '39.99€',
    imagen: getImageUrl('pandemic.png'),
    subcategoria: 'Cooperativo'
  }
]);

const ultimosTCG = ref<any[]>([
  {
    id: 201,
    nombre: 'Yu-Gi-Oh! Booster',
    precio: '89.99€',
    imagen: getImageUrl('yu-gi-oh booster.jpg'),
    subcategoria: 'Yu-Gi-Oh!'
  },
  {
    id: 202,
    nombre: 'MTG Commander Legends',
    precio: '159.99€',
    imagen: getImageUrl('mtg-commander-legends.jpg'),
    subcategoria: 'Magic'
  },
  {
    id: 203,
    nombre: 'Pokémon Paradox Rift',
    precio: '4.50€',
    imagen: getImageUrl('pokemon paradox rift.jpg'),
    subcategoria: 'Pokémon'
  }
]);

const ultimosComics = ref<any[]>([
  {
    id: 301,
    nombre: 'Spider-Man #1',
    precio: '5.99€',
    imagen: getImageUrl('Spider-Man-1.jpg'),
    subcategoria: 'Marvel'
  },
  {
    id: 302,
    nombre: 'Batman: The Knight',
    precio: '24.99€',
    imagen: getImageUrl('batman-the-knight.jpg'),
    subcategoria: 'DC Comics'
  },
  {
    id: 303,
    nombre: 'Walking Dead',
    precio: '59.99€',
    imagen: getImageUrl('walking-dead.jpg'),
    subcategoria: 'Image'
  }
]);

const ultimosManga = ref<any[]>([
  {
    id: 401,
    nombre: 'Jujutsu Kaisen Vol. 20',
    precio: '11.99€',
    imagen: getImageUrl('jujutsu-kaisen-20.jpg'),
    subcategoria: 'Shōnen'
  },
  {
    id: 402,
    nombre: 'Chainsaw Man Vol. 14',
    precio: '11.99€',
    imagen: getImageUrl('Chainsaw man vol14.jpg'),
    subcategoria: 'Shōnen'
  },
  {
    id: 403,
    nombre: 'Berserk Deluxe Vol. 8',
    precio: '39.99€',
    imagen: getImageUrl('berserk-deluxe-vol8.jpg'),
    subcategoria: 'Seinen'
  }
]);

const ultimasFiguras = ref<any[]>([
  {
    id: 501,
    nombre: 'Nendoroid Gojo',
    precio: '54.99€',
    imagen: getImageUrl('nendoroid-gojo.jpg'),
    subcategoria: 'Nendoroid'
  },
  {
    id: 502,
    nombre: 'Goku Ultra Instinct',
    precio: '249.99€',
    imagen: getImageUrl('goku-ultra-instinct-statue.jpg'),
    subcategoria: 'Estatuas'
  },
  {
    id: 503,
    nombre: 'Luffy Gear 5',
    precio: '129.99€',
    imagen: getImageUrl('luffy-gear-5.jpg'),
    subcategoria: 'Scale Figures'
  }
]);

const ultimosAccesorios = ref<any[]>([
  {
    id: 601,
    nombre: 'Dragon Shield Sleeves',
    precio: '12.99€',
    imagen: getImageUrl('dragon-shield-sleeves.jpg'),
    subcategoria: 'Fundas'
  },
  {
    id: 602,
    nombre: 'Ultimate Guard Deck Box',
    precio: '9.99€',
    imagen: getImageUrl('ultimate-guard-deck-box.jpg'),
    subcategoria: 'Deck Box'
  },
  {
    id: 603,
    nombre: 'Custom Playmat',
    precio: '29.99€',
    imagen: getImageUrl('custom-paymat.jpg'),
    subcategoria: 'Playmat'
  }
]);

let timer: ReturnType<typeof setInterval> | null = null;

const prevSlide = () => {
  currentSlide.value = (currentSlide.value - 1 + slides.length) % slides.length;
};

const nextSlide = () => {
  currentSlide.value = (currentSlide.value + 1) % slides.length;
};

const startAutoSlide = () => {
  timer = setInterval(() => {
    nextSlide();
  }, 5000);
};

const viewProduct = (productId: number) => {
  router.push(`/producto/${productId}`);
  window.scrollTo(0, 0);
};

// Cargar reservas del usuario
const loadReservasUsuario = async () => {
  try {
    const userEmail = authStore.user?.email
    
    if (!userEmail) {
      console.warn('⚠️ Usuario no autenticado')
      reservasUsuario.value = []
      return
    }

    const response = await api.get('/reservas', {
      params: {
        email: userEmail
      }
    })
    
    if (response.data && Array.isArray(response.data)) {
      reservasUsuario.value = response.data
        .filter((r: any) => {
          const resUserEmail = r.usuario?.email || r.email || ''
          return resUserEmail.toLowerCase() === userEmail.toLowerCase()
        })
        .map((r: any) => ({
          id: String(r.id),
          producto: {
            nombre: r.evento?.nombre || 'Evento sin nombre',
            categoria: 'Evento',
            editorial: r.evento?.ubicacion || 'Sin ubicación',
            imagen: r.evento?.imagenUrl || 'https://images.unsplash.com/photo-1612036782180-6f0b6cd846fe?w=400'
          },
          estado: r.estado?.toLowerCase() || 'pendiente',
          cantidad: r.personas || 1,
          fechaReserva: r.fechaReserva,
          fechaDisponibilidad: r.fechaHora || r.fechaReserva
        }))
      console.log('✅ Reservas del usuario cargadas:', reservasUsuario.value.length)
    }
  } catch (err) {
    console.error('❌ Error al cargar reservas:', err)
    reservasUsuario.value = []
  }
}

// Cargar productos disponibles para reservar
const loadEventosDisponibles = async () => {
  loadingReservas.value = true
  try {
    const response = await api.get('/api/products/reserves')
    
    if (response.data && Array.isArray(response.data)) {
      // Tomar los primeros 6 productos de reserva
      productosReserva.value = response.data.slice(0, 6).map((p: any) => ({
        ...p,
        image: resolveImageUrl(p.image) || p.image
      }))
      console.log('✅ Productos de reserva cargados:', productosReserva.value.length)
    }
  } catch (err) {
    console.error('❌ Error al cargar productos de reserva:', err)
    // Cargar productos de ejemplo si falla
    productosReserva.value = [
      {
        id: 1,
        name: 'One Piece Vol. 110',
        category: 'Manga',
        price: 12.99,
        image: 'https://images.unsplash.com/photo-1612036782180-6f0b6cd846fe?w=200'
      },
      {
        id: 2,
        name: 'Magic Commander Deck',
        category: 'TCG',
        price: 159.99,
        image: 'https://images.unsplash.com/photo-1614680376573-df3480f0c6ff?w=200'
      },
      {
        id: 3,
        name: 'Funko Pop Exclusive',
        category: 'Figuras',
        price: 24.99,
        image: 'https://images.unsplash.com/photo-1608889476561-6242cfdbf622?w=200'
      },
      {
        id: 4,
        name: 'Chainsaw Man Vol. 15',
        category: 'Manga',
        price: 11.99,
        image: 'https://images.unsplash.com/photo-1612036782180-6f0b6cd846fe?w=200'
      },
      {
        id: 5,
        name: 'Pokémon Booster Box',
        category: 'TCG',
        price: 139.99,
        image: 'https://images.unsplash.com/photo-1614680376573-df3480f0c6ff?w=200'
      },
      {
        id: 6,
        name: 'Nendoroid Gojo',
        category: 'Figuras',
        price: 54.99,
        image: 'https://images.unsplash.com/photo-1608889476561-6242cfdbf622?w=200'
      }
    ]
  } finally {
    loadingReservas.value = false
  }
}

// Formatear fecha de reserva
const formatReservaDate = (dateString: string | undefined): string => {
  if (!dateString) return 'Sin fecha'
  const date = new Date(dateString)
  return date.toLocaleDateString('es-ES', { 
    month: 'long', 
    day: 'numeric'
  })
}

// Estados de reserva
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

// Navegar a reservas
const goToReservas = () => {
  router.push('/reservas')
}

// Manejar click en reservar
const handleReservar = (producto: any) => {
  if (!authStore.isAuthenticated) {
    selectedProduct.value = producto
    showAuthModal.value = true
    return
  }
  // Si está autenticado, agregar al carrito directamente
  cartStore.addToCart(producto)
  console.log('✅ Producto agregado al carrito:', producto.name)
}

// Cerrar modal de autenticación
const closeAuthModal = () => {
  showAuthModal.value = false
}

// Ir a login desde el modal
const goToLogin = () => {
  router.push('/login')
  showAuthModal.value = false
}

onMounted(() => {
  startAutoSlide()
  loadProductosMasComprados()
  loadEventosDisponibles()
  loadReservasUsuario()
});

onBeforeUnmount(() => {
  if (timer) clearInterval(timer);
});
</script>

<style scoped>
.home-container {
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
  min-height: 100vh;
}

/* Carrusel */
.carousel-wrapper {
  position: relative;
  width: 100%;
  height: 450px;
  overflow: hidden;
  background-color: #1f2937;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.carousel-container {
  position: relative;
  width: 100%;
  height: 100%;
}

.carousel-image {
  position: absolute;
  width: 100%;
  height: 100%;
  object-fit: cover;
  opacity: 0;
  transition: opacity 1s ease-in-out;
}

.carousel-image.active {
  opacity: 1;
}

.carousel-button {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  border: none;
  padding: 16px 20px;
  font-size: 20px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.3s ease;
  z-index: 10;
}

.carousel-button:hover {
  background-color: rgba(220, 38, 38, 0.8);
  transform: translateY(-50%) scale(1.1);
}

.carousel-button-left {
  left: 20px;
}

.carousel-button-right {
  right: 20px;
}

.carousel-indicators {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 12px;
  z-index: 10;
}

.indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.5);
  border: 2px solid white;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 0;
}

.indicator.active {
  background-color: #dc2626;
  transform: scale(1.2);
}

/* Contenido */
.content-wrapper {
  max-width: 1400px;
  margin: 0 auto;
  padding: 50px 20px;
}

.section {
  margin-bottom: 60px;
}

.section-title {
  font-size: 32px;
  font-weight: bold;
  color: #1f2937;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 4px solid #dc2626;
  letter-spacing: 3px;
  position: relative;
}

/* Productos Favoritos */
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
}

.product-card {
  background-color: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.product-card:hover {
  border-color: #dc2626;
  box-shadow: 0 8px 24px rgba(220, 38, 38, 0.2);
  transform: translateY(-8px);
}

.product-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.product-info {
  padding: 20px;
}

.product-name {
  font-weight: bold;
  color: #1f2937;
  margin-bottom: 8px;
  font-size: 18px;
}

.product-category {
  color: #6b7280;
  font-size: 14px;
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 1px;
  font-weight: 500;
}

.product-price {
  color: #dc2626;
  font-weight: bold;
  font-size: 20px;
}

/* Reservas */
.reservas-card {
  background-color: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 2px solid #fecaca;
}

.reservas-subtitle {
  color: #6b7280;
  margin-bottom: 25px;
  font-size: 16px;
}

.reservas-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.reserva-image {
  width: 72px;
  height: 72px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 16px;
}

.reserva-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background-color: #f9fafb;
  border-radius: 8px;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.reserva-item:hover {
  background-color: #fef2f2;
  border-left-color: #dc2626;
  transform: translateX(5px);
}

.reserva-info {
  flex: 1;
}

.reserva-title {
  font-weight: bold;
  color: #1f2937;
  margin-bottom: 5px;
  font-size: 18px;
}

.reserva-date {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 8px;
}

.status-badge-mini {
  display: inline-block;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 20px;
  font-weight: 600;
  background-color: #fecaca;
  color: #dc2626;
}

.status-badge-mini.disponible {
  background-color: #d1fae5;
  color: #065f46;
}

.status-badge-mini.recogido {
  background-color: #dbeafe;
  color: #0c4a6e;
}

.not-authenticated,
.no-reservas {
  text-align: center;
  padding: 30px;
  color: #6b7280;
}

.reserva-button {
  background-color: #dc2626;
  color: white;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.reserva-button:hover {
  background-color: #991b1b;
  transform: scale(1.05);
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

.loading-message {
  text-align: center;
  padding: 20px;
  color: #6b7280;
}

.no-events-message {
  text-align: center;
  padding: 30px;
  color: #6b7280;
}

.reserva-location {
  font-size: 13px;
  color: #9ca3af;
  margin: 5px 0 0 0;
}

/* Últimos Productos por Categoría */
.category-section {
  margin-bottom: 50px;
}

.category-section:last-child {
  margin-bottom: 0;
}

.category-section-title {
  font-size: 24px;
  font-weight: bold;
  color: #374151;
  margin-bottom: 25px;
  padding-left: 15px;
  border-left: 5px solid #dc2626;
  letter-spacing: 1px;
}

.category-products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 25px;
}

/* Responsive */
@media (max-width: 1024px) {
  .carousel-wrapper {
    height: 350px;
  }

  .section-title {
    font-size: 28px;
  }

  .products-grid {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
  }

  .category-products-grid {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .carousel-wrapper {
    height: 300px;
  }

  .content-wrapper {
    padding: 30px 15px;
  }

  .section {
    margin-bottom: 40px;
  }

  .section-title {
    font-size: 24px;
    letter-spacing: 2px;
  }

  .products-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .reserva-item {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }

  .reserva-button {
    width: 100%;
  }

  .category-section {
    margin-bottom: 35px;
  }

  .category-section-title {
    font-size: 20px;
    padding-left: 12px;
    border-left-width: 4px;
  }

  .category-products-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }
}

@media (max-width: 480px) {
  .carousel-wrapper {
    height: 250px;
  }

  .carousel-button {
    padding: 12px 16px;
    font-size: 16px;
  }

  .section-title {
    font-size: 20px;
  }

  .category-section-title {
    font-size: 18px;
  }
}
</style>