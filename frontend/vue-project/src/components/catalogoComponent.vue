<template>
  <div class="products-container">
    <div class="products-wrapper">
      <!-- Filtros (Izquierda) -->
      <aside class="filters-sidebar">
        <h2 class="filters-title">Filtros</h2>

        <!-- Ordenar por -->
        <div class="filter-section">
          <h3 class="filter-subtitle">Ordenar por</h3>
          <select v-model="sortBy" class="sort-select">
            <option value="newest">Más Recientes</option>
            <option value="price-asc">Precio: Menor a Mayor</option>
            <option value="price-desc">Precio: Mayor a Menor</option>
            <option value="name">Nombre: A - Z</option>
            <option value="rating">Mejor Valorados</option>
            <option value="discount">Mayor Descuento</option>
          </select>
        </div>

        <!-- Categoría -->
        <div class="filter-section">
          <h3 class="filter-subtitle">Categoría</h3>
          <div class="filter-group">
            <!-- TCG -->
            <div class="category-group">
              <label class="filter-checkbox main-category">
                <input 
                  type="checkbox" 
                  v-model="selectedCategories" 
                  value="TCG"
                  @change="onCategoryChange('TCG')"
                />
                <span>TCG</span>
              </label>
              <div class="subcategory-list" v-show="selectedCategories.includes('TCG')">
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Yu-Gi-Oh" />
                  <span>Yu-Gi-Oh!</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Magic" />
                  <span>Magic: The Gathering</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Pokemon" />
                  <span>Pokémon TCG</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="One Piece" />
                  <span>One Piece Card Game</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Digimon" />
                  <span>Digimon</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Dragon Ball" />
                  <span>Dragon Ball Super</span>
                </label>
              </div>
            </div>

            <!-- Manga -->
            <div class="category-group">
              <label class="filter-checkbox main-category">
                <input 
                  type="checkbox" 
                  v-model="selectedCategories" 
                  value="Manga"
                  @change="onCategoryChange('Manga')"
                />
                <span>Manga</span>
              </label>
              <div class="subcategory-list" v-show="selectedCategories.includes('Manga')">
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Shonen" />
                  <span>Shōnen</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Seinen" />
                  <span>Seinen</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Shojo" />
                  <span>Shōjo</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Josei" />
                  <span>Josei</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Kodomo" />
                  <span>Kodomo</span>
                </label>
              </div>
            </div>

            <!-- Comics -->
            <div class="category-group">
              <label class="filter-checkbox main-category">
                <input 
                  type="checkbox" 
                  v-model="selectedCategories" 
                  value="Comics"
                  @change="onCategoryChange('Comics')"
                />
                <span>Comics</span>
              </label>
              <div class="subcategory-list" v-show="selectedCategories.includes('Comics')">
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Marvel" />
                  <span>Marvel</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="DC" />
                  <span>DC Comics</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Image" />
                  <span>Image Comics</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Dark Horse" />
                  <span>Dark Horse</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Independientes" />
                  <span>Independientes</span>
                </label>
              </div>
            </div>

            <!-- Figuras -->
            <div class="category-group">
              <label class="filter-checkbox main-category">
                <input 
                  type="checkbox" 
                  v-model="selectedCategories" 
                  value="Figuras"
                  @change="onCategoryChange('Figuras')"
                />
                <span>Figuras</span>
              </label>
              <div class="subcategory-list" v-show="selectedCategories.includes('Figuras')">
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Nendoroid" />
                  <span>Nendoroid</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Funko Pop" />
                  <span>Funko Pop</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Scale Figures" />
                  <span>Scale Figures</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Estatuas" />
                  <span>Estatuas</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Bustos" />
                  <span>Bustos</span>
                </label>
              </div>
            </div>

            <!-- Juegos de mesa -->
            <div class="category-group">
              <label class="filter-checkbox main-category">
                <input 
                  type="checkbox" 
                  v-model="selectedCategories" 
                  value="Juegos de mesa"
                  @change="onCategoryChange('Juegos de mesa')"
                />
                <span>Juegos de mesa</span>
              </label>
              <div class="subcategory-list" v-show="selectedCategories.includes('Juegos de mesa')">
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Estrategia" />
                  <span>Estrategia</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Familiar" />
                  <span>Familiar</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Party" />
                  <span>Party Games</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Cooperativos" />
                  <span>Cooperativos</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Rol" />
                  <span>Juegos de Rol</span>
                </label>
              </div>
            </div>

            <!-- Accesorios -->
            <div class="category-group">
              <label class="filter-checkbox main-category">
                <input 
                  type="checkbox" 
                  v-model="selectedCategories" 
                  value="Accesorios"
                  @change="onCategoryChange('Accesorios')"
                />
                <span>Accesorios</span>
              </label>
              <div class="subcategory-list" v-show="selectedCategories.includes('Accesorios')">
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Fundas" />
                  <span>Fundas & Sleeves</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Deck Box" />
                  <span>Deck Boxes</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Playmat" />
                  <span>Playmats</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Dados" />
                  <span>Dados</span>
                </label>
                <label class="filter-checkbox subcategory">
                  <input type="checkbox" v-model="selectedSubcategories" value="Carpetas" />
                  <span>Carpetas & Binders</span>
                </label>
              </div>
            </div>
          </div>
        </div>

        <!-- Rango de Precio -->
        <div class="filter-section">
          <h3 class="filter-subtitle">Rango de Precio</h3>
          <div class="price-range">
            <label class="price-label">
              Min: 
              <input 
                v-model.number="priceRange.min" 
                type="number" 
                class="price-input"
              />€
            </label>
            <label class="price-label">
              Max: 
              <input 
                v-model.number="priceRange.max" 
                type="number" 
                class="price-input"
              />€
            </label>
          </div>
          <input 
            type="range" 
            v-model.number="priceRange.min" 
            :max="priceRange.max"
            min="0"
            class="slider"
          />
          <input 
            type="range" 
            v-model.number="priceRange.max" 
            :min="priceRange.min"
            max="500"
            class="slider"
          />
          <div class="price-display">
            {{ priceRange.min }}€ - {{ priceRange.max }}€
          </div>
        </div>

        <!-- Estado del Producto -->
        <div class="filter-section">
          <h3 class="filter-subtitle">Estado</h3>
          <div class="filter-group">
            <label class="filter-checkbox">
              <input 
                type="checkbox" 
                v-model="onlyAvailable"
              />
              <span>Solo disponibles</span>
            </label>
            <label class="filter-checkbox">
              <input 
                type="checkbox" 
                v-model="onlyReserves"
              />
              <span>Solo reservas</span>
            </label>
            <label class="filter-checkbox">
              <input 
                type="checkbox" 
                v-model="onlyDiscounts"
              />
              <span>Solo ofertas</span>
            </label>
            <label class="filter-checkbox">
              <input 
                type="checkbox" 
                v-model="onlyNew"
              />
              <span>✨ Solo novedades</span>
            </label>
          </div>
        </div>
        <!-- Rango de Descuento -->
        <div class="filter-section">
          <h3 class="filter-subtitle">Descuento Mínimo</h3>
          <select v-model="minDiscount" class="sort-select">
            <option value="0">Todos</option>
            <option value="10">10% o más</option>
            <option value="20">20% o más</option>
            <option value="30">30% o más</option>
            <option value="50">50% o más</option>
          </select>
        </div>

        

        <!-- Botón Limpiar Filtros -->
        <button @click="clearFilters" class="clear-filters-btn">
          Limpiar Filtros
        </button>
      </aside>

      <!-- Productos (Derecha) -->
      <main class="products-main">
        <div class="products-header">
          <h1 class="products-title">Nuestros Productos</h1>
          <p class="products-count">{{ filteredProducts.length }} productos encontrados</p>
        </div>

        <!-- Loading State -->
        <div v-if="loading" class="loading-state">
          <div class="spinner"></div>
          <p>Cargando productos...</p>
        </div>

        <!-- Error State -->
        <div v-else-if="error" class="error-state">
          <p>{{ error }}</p>
          <button @click="loadProducts" class="retry-btn">Reintentar</button>
        </div>

        <!-- Grid de Productos -->
        <div v-else-if="filteredProducts.length > 0" class="products-grid">
          <div 
            v-for="product in filteredProducts" 
            :key="product.id"
            class="product-card"
            @click="viewProduct(product.id)"
            style="cursor: pointer;"
          >
            <div class="product-image-container">
              <img :src="product.image" :alt="product.name" class="product-image" />
              <span v-if="!product.available" class="out-of-stock">Agotado</span>
              <span v-if="product.isReserve" class="reserve-badge">Reserva</span>
              <span v-if="product.isNew" class="new-badge">Nuevo</span>
              <span v-if="product.discount" class="discount-badge">-{{ product.discount }}%</span>
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <p class="product-category">{{ product.category }}</p>
              <div class="product-price">
                <span v-if="product.originalPrice" class="original-price">
                  {{ product.originalPrice }}€
                </span>
                <span class="current-price">{{ product.price }}€</span>
              </div>
              <button 
                @click.stop="addToCart(product.id)"
                :disabled="!product.available"
                :class="[
                  'add-to-cart-btn', 
                  product.isReserve ? 'reserve-btn' : '',
                  recentlyAddedProducts.has(product.id) ? 'added-animation' : ''
                ]"
              >
                <span v-if="recentlyAddedProducts.has(product.id)">✅ Añadido al carrito</span>
                <span v-else>{{ product.isReserve ? 'Reservar' : (product.available ? '🛒 Agregar al Carrito' : 'No Disponible') }}</span>
              </button>
            </div>
          </div>
        </div>

        <!-- Sin resultados -->
        <div v-else class="no-products">
          <p>No se encontraron productos con los filtros seleccionados</p>
          <button @click="clearFilters" class="clear-filters-link">
            Limpiar filtros
          </button>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import api from '../api/axios'
import { useCartStore } from '@/stores/cartStore'

const getImageUrl = (imageName: string) => {
  return new URL(`../assets/delete_inicio/${imageName}`, import.meta.url).href
}

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

interface Product {
  id: number
  name: string
  category: string
  subcategory?: string
  price: number
  originalPrice?: number
  discount?: number
  image: string
  available: boolean
  rating: number
  reviews: number
  isReserve?: boolean
  isNew?: boolean
}

const products = ref<Product[]>([])
const loading = ref(true)
const error = ref<string | null>(null)
const recentlyAddedProducts = ref<Set<number>>(new Set())

// Función para cargar productos desde la API
const loadProducts = async () => {
  try {
    loading.value = true
    error.value = null
    const response = await api.get('/api/products')
    // Transformar las imágenes a URLs completas
    products.value = response.data.map((product: Product) => ({
      ...product,
      image: getImageUrl(product.image)
    }))
    console.log('✅ Productos cargados:', response.data.length)
  } catch (err) {
    console.error('❌ Error al cargar productos:', err)
    error.value = 'Error al cargar los productos'
    // Fallback: usar productos vacíos si falla
    products.value = []
  } finally {
    loading.value = false
  }
}

// Datos hardcodeados como respaldo (se mantendrán pero no se usarán)
const productsBackup: Product[] = [
  {
    id: 1,
    name: 'Attack of Titans Vol. 34',
    category: 'Manga',
    subcategory: 'Shonen',
    price: 12.99,
    discount: 10,
    originalPrice: 14.99,
    image: getImageUrl('attack-of-titans-vol34.jpg'),
    available: true,
    rating: 5,
    reviews: 145,
    isNew: true,
  },
  {
    id: 2,
    name: 'Batman: The Knight',
    category: 'Comics',
    subcategory: 'DC',
    price: 24.99,
    image: getImageUrl('batman-the-knight.jpg'),
    available: true,
    rating: 4,
    reviews: 89,
  },
  {
    id: 3,
    name: 'Batman: The Knight 2',
    category: 'Comics',
    subcategory: 'DC',
    price: 26.99,
    image: getImageUrl('batman-the-knight-2.jpg'),
    available: true,
    rating: 4,
    reviews: 134,
  },
  {
    id: 4,
    name: 'Berserk Deluxe Vol. 8',
    category: 'Manga',
    subcategory: 'Seinen',
    price: 39.99,
    discount: 15,
    originalPrice: 46.99,
    image: getImageUrl('berserk-deluxe-vol8.jpg'),
    available: true,
    rating: 5,
    reviews: 567,
  },
  {
    id: 5,
    name: 'Catan: Base Game',
    category: 'Juegos de mesa',
    subcategory: 'Estrategia',
    price: 49.99,
    image: getImageUrl('catan-base-game.jpg'),
    available: true,
    rating: 5,
    reviews: 312,
  },
  {
    id: 6,
    name: 'Chainsaw Man Vol. 14',
    category: 'Manga',
    subcategory: 'Shonen',
    price: 11.99,
    image: getImageUrl('Chainsaw man vol14.jpg'),
    available: false,
    rating: 5,
    reviews: 289,
    isReserve: true,
  },
  {
    id: 7,
    name: 'Custom Playmat',
    category: 'Accesorios',
    subcategory: 'Playmat',
    price: 29.99,
    discount: 20,
    originalPrice: 37.49,
    image: getImageUrl('custom-paymat.jpg'),
    available: true,
    rating: 4,
    reviews: 198,
  },
  {
    id: 8,
    name: 'Dados Metálicos Set',
    category: 'Accesorios',
    subcategory: 'Dados',
    price: 34.99,
    image: getImageUrl('dados-metalicos-set.jpg'),
    available: true,
    rating: 5,
    reviews: 156,
  },
  {
    id: 9,
    name: 'Dragon Shield Playmat',
    category: 'Accesorios',
    subcategory: 'Playmat',
    price: 24.99,
    image: getImageUrl('dragon-shield-playmat.jpg'),
    available: true,
    rating: 5,
    reviews: 289,
  },
  {
    id: 10,
    name: 'Dragon Shield Sleeves',
    category: 'Accesorios',
    subcategory: 'Fundas',
    price: 12.99,
    image: getImageUrl('dragon-shield-sleeves.jpg'),
    available: true,
    rating: 5,
    reviews: 421,
    isNew: true,
  },
  {
    id: 11,
    name: 'Funko Goku',
    category: 'Figuras',
    subcategory: 'Funko Pop',
    price: 14.99,
    image: getImageUrl('funko-goku.jpg'),
    available: true,
    rating: 4,
    reviews: 178,
  },
  {
    id: 12,
    name: 'Gojo Satoru Nendoroid',
    category: 'Figuras',
    subcategory: 'Nendoroid',
    price: 54.99,
    image: getImageUrl('gojo-satoru-nendoroid.jpg'),
    available: false,
    rating: 5,
    reviews: 445,
    isReserve: true,
  },
  {
    id: 13,
    name: 'Goku Ultra Instinct Statue',
    category: 'Figuras',
    subcategory: 'Estatuas',
    price: 249.99,
    image: getImageUrl('goku-ultra-instinct-statue.jpg'),
    available: false,
    rating: 5,
    reviews: 234,
    isReserve: true,
  },
  {
    id: 14,
    name: 'Jujutsu Kaisen Vol. 20',
    category: 'Manga',
    subcategory: 'Shonen',
    price: 11.99,
    image: getImageUrl('jujutsu-kaisen-20.jpg'),
    available: true,
    rating: 5,
    reviews: 892,
    isNew: true,
  },
  {
    id: 15,
    name: 'Jujutsu Kaisen Vol. 22',
    category: 'Manga',
    subcategory: 'Shonen',
    price: 11.99,
    image: getImageUrl('jujutsu-kaisen-vol-22.jpg'),
    available: true,
    rating: 5,
    reviews: 678,
  },
  {
    id: 16,
    name: 'Luffy Gear 5',
    category: 'Figuras',
    subcategory: 'Scale Figures',
    price: 129.99,
    discount: 10,
    originalPrice: 144.99,
    image: getImageUrl('luffy-gear-5.jpg'),
    available: true,
    rating: 5,
    reviews: 567,
  },
  {
    id: 17,
    name: 'Luffy Gear 5 Figure 2',
    category: 'Figuras',
    subcategory: 'Scale Figures',
    price: 139.99,
    image: getImageUrl('luffy-gear-5-figure2.jpg'),
    available: false,
    rating: 5,
    reviews: 345,
    isReserve: true,
  },
  {
    id: 18,
    name: 'Magic Commander',
    category: 'TCG',
    subcategory: 'Magic',
    price: 159.99,
    discount: 20,
    originalPrice: 199.99,
    image: getImageUrl('magicComander.jpg'),
    available: true,
    rating: 5,
    reviews: 890,
  },
  {
    id: 19,
    name: 'MTG Booster Pack',
    category: 'TCG',
    subcategory: 'Magic',
    price: 4.99,
    image: getImageUrl('mtg-booster-pack.jpg'),
    available: true,
    rating: 4,
    reviews: 234,
  },
  {
    id: 20,
    name: 'MTG Commander Legends',
    category: 'TCG',
    subcategory: 'Magic',
    price: 159.99,
    discount: 15,
    originalPrice: 188.22,
    image: getImageUrl('mtg-commander-legends.jpg'),
    available: true,
    rating: 5,
    reviews: 456,
  },
  {
    id: 21,
    name: 'Naruto Figure',
    category: 'Figuras',
    subcategory: 'Nendoroid',
    price: 34.99,
    image: getImageUrl('naruto-figure.jpg'),
    available: true,
    rating: 5,
    reviews: 678,
  },
  {
    id: 22,
    name: 'Nendoroid Gojo',
    category: 'Figuras',
    subcategory: 'Nendoroid',
    price: 54.99,
    image: getImageUrl('nendoroid-gojo.jpg'),
    available: true,
    rating: 5,
    reviews: 789,
    isNew: true,
  },
  {
    id: 23,
    name: 'One Piece Vol. 100',
    category: 'Manga',
    subcategory: 'Shonen',
    price: 12.99,
    discount: 10,
    originalPrice: 14.99,
    image: getImageUrl('one-piece-vol100.jpg'),
    available: true,
    rating: 5,
    reviews: 1567,
    isNew: true,
  },
  {
    id: 24,
    name: 'Pandemic',
    category: 'Juegos de mesa',
    subcategory: 'Cooperativos',
    price: 39.99,
    image: getImageUrl('pandemic.png'),
    available: true,
    rating: 5,
    reviews: 890,
  },
  {
    id: 25,
    name: 'Pandora Vol. 24',
    category: 'Manga',
    subcategory: 'Shojo',
    price: 11.99,
    image: getImageUrl('pandora-vol24.jpg'),
    available: true,
    rating: 4,
    reviews: 345,
  },
  {
    id: 26,
    name: 'Pokémon Paradox Rift',
    category: 'TCG',
    subcategory: 'Pokemon',
    price: 4.50,
    image: getImageUrl('pokemon paradox rift.jpg'),
    available: true,
    rating: 5,
    reviews: 567,
    isNew: true,
  },
  {
    id: 27,
    name: 'Pokémon Card Sleeve',
    category: 'Accesorios',
    subcategory: 'Fundas',
    price: 8.99,
    image: getImageUrl('pokemon-card-sleeve.jpg'),
    available: true,
    rating: 4,
    reviews: 456,
  },
  {
    id: 28,
    name: 'Scarlet Violet Booster',
    category: 'TCG',
    subcategory: 'Pokemon',
    price: 4.50,
    image: getImageUrl('scarlet-violet-booster.jpg'),
    available: true,
    rating: 5,
    reviews: 678,
  },
  {
    id: 29,
    name: 'Spider-Man Comic',
    category: 'Comics',
    subcategory: 'Marvel',
    price: 5.99,
    discount: 15,
    originalPrice: 6.99,
    image: getImageUrl('Spider-Man-1.jpg'),
    available: true,
    rating: 4,
    reviews: 234,
  },
  {
    id: 30,
    name: 'Spider-Man Comic 2',
    category: 'Comics',
    subcategory: 'Marvel',
    price: 5.99,
    image: getImageUrl('Spider-Man-comic.jpg'),
    available: true,
    rating: 4,
    reviews: 345,
  },
  {
    id: 31,
    name: 'Superman: Action Comics',
    category: 'Comics',
    subcategory: 'DC',
    price: 18.99,
    discount: 10,
    originalPrice: 21.09,
    image: getImageUrl('super-man-action-comics.jpg'),
    available: true,
    rating: 4,
    reviews: 456,
  },
  {
    id: 32,
    name: 'Ticket to Ride',
    category: 'Juegos de mesa',
    subcategory: 'Familiar',
    price: 44.99,
    image: getImageUrl('ticket to ride.jpg'),
    available: true,
    rating: 5,
    reviews: 789,
  },
  {
    id: 33,
    name: 'Ticket to Ride Europa',
    category: 'Juegos de mesa',
    subcategory: 'Familiar',
    price: 44.99,
    image: getImageUrl('ticket-to-ride-europe.jpg'),
    available: true,
    rating: 5,
    reviews: 890,
  },
  {
    id: 34,
    name: 'Tokyo Ghoul Manga Box Set',
    category: 'Manga',
    subcategory: 'Seinen',
    price: 179.99,
    discount: 30,
    originalPrice: 257.13,
    image: getImageUrl('tokyo-ghoul-manga-box-set.jpg'),
    available: true,
    rating: 5,
    reviews: 1234,
  },
  {
    id: 35,
    name: 'Ultimate Guard Deck Box',
    category: 'Accesorios',
    subcategory: 'Deck Box',
    price: 9.99,
    image: getImageUrl('ultimate-guard-deck-box.jpg'),
    available: true,
    rating: 4,
    reviews: 567,
  },
  {
    id: 36,
    name: 'Walking Dead',
    category: 'Comics',
    subcategory: 'Image',
    price: 59.99,
    image: getImageUrl('walking-dead.jpg'),
    available: true,
    rating: 5,
    reviews: 678,
  },
  {
    id: 37,
    name: 'Wingspan',
    category: 'Juegos de mesa',
    subcategory: 'Estrategia',
    price: 54.99,
    discount: 25,
    originalPrice: 73.32,
    image: getImageUrl('wingspan.jpg'),
    available: true,
    rating: 5,
    reviews: 890,
  },
  {
    id: 38,
    name: 'Wingspan Expansions',
    category: 'Juegos de mesa',
    subcategory: 'Estrategia',
    price: 29.99,
    image: getImageUrl('wingspan-expansions.jpg'),
    available: true,
    rating: 5,
    reviews: 456,
  },
  {
    id: 39,
    name: 'Wolverine Bust',
    category: 'Figuras',
    subcategory: 'Bustos',
    price: 89.99,
    image: getImageUrl('wolverine-bust.jpg'),
    available: true,
    rating: 4,
    reviews: 234,
  },
  {
    id: 40,
    name: 'Yu-Gi-Oh! Booster Box',
    category: 'TCG',
    subcategory: 'Yu-Gi-Oh',
    price: 89.99,
    discount: 25,
    originalPrice: 119.99,
    image: getImageUrl('yu-gi-oh booster.jpg'),
    available: true,
    rating: 5,
    reviews: 789,
  }
]


const selectedCategories = ref<string[]>([])
const selectedSubcategories = ref<string[]>([])
const priceRange = ref({ min: 0, max: 500 })
const onlyAvailable = ref(false)
const onlyReserves = ref(false)
const onlyDiscounts = ref(false)
const onlyNew = ref(false)
const minRating = ref<number>(0)
const minDiscount = ref<number>(0)
const sortBy = ref('newest')

// Inicializar filtros desde la URL y cargar productos
onMounted(async () => {
  // Cargar productos desde la API
  await loadProducts()
  
  const category = route.query.category as string
  const subcategory = route.query.sub as string
  
  if (category) {
    selectedCategories.value = [category]
  }
  
  if (subcategory) {
    selectedSubcategories.value = [subcategory]
  }
})

// Observar cambios en la URL
watch(() => route.query, (newQuery) => {
  const category = newQuery.category as string
  const subcategory = newQuery.sub as string
  
  // Actualizar categoría
  if (category && !selectedCategories.value.includes(category)) {
    selectedCategories.value = [category]
  } else if (!category) {
    selectedCategories.value = []
  }
  
  // Actualizar subcategoría
  if (subcategory && !selectedSubcategories.value.includes(subcategory)) {
    selectedSubcategories.value = [subcategory]
  } else if (!subcategory) {
    selectedSubcategories.value = []
  }
}, { deep: true })

// Cuando se desmarca una categoría, desmarcar sus subcategorías
const onCategoryChange = (category: string) => {
  if (!selectedCategories.value.includes(category)) {
    // Si se desmarca la categoría, desmarcar todas sus subcategorías
    const subcategoryMap: Record<string, string[]> = {
      'TCG': ['Yu-Gi-Oh', 'Magic', 'Pokemon', 'One Piece', 'Digimon', 'Dragon Ball'],
      'Manga': ['Shonen', 'Seinen', 'Shojo', 'Josei', 'Kodomo'],
      'Comics': ['Marvel', 'DC', 'Image', 'Dark Horse', 'Independientes'],
      'Figuras': ['Nendoroid', 'Funko Pop', 'Scale Figures', 'Estatuas', 'Bustos'],
      'Juegos de mesa': ['Estrategia', 'Familiar', 'Party', 'Cooperativos', 'Rol'],
      'Accesorios': ['Fundas', 'Deck Box', 'Playmat', 'Dados', 'Carpetas']
    }
    
    const subsToRemove = subcategoryMap[category] || []
    selectedSubcategories.value = selectedSubcategories.value.filter(
      sub => !subsToRemove.includes(sub)
    )
  }
}

const filteredProducts = computed(() => {
  const result = products.value.filter((product) => {
    const categoryMatch = 
      selectedCategories.value.length === 0 || 
      selectedCategories.value.includes(product.category)
    
    const subcategoryMatch = 
      selectedSubcategories.value.length === 0 || 
      (product.subcategory && selectedSubcategories.value.includes(product.subcategory))
    
    const priceMatch = 
      product.price >= priceRange.value.min && 
      product.price <= priceRange.value.max
    
    const availabilityMatch = !onlyAvailable.value || product.available
    
    const reserveMatch = !onlyReserves.value || product.isReserve
    
    const discountMatch = !onlyDiscounts.value || (product.discount && product.discount > 0)
    
    const newMatch = !onlyNew.value || product.isNew
    
    const ratingMatch = minRating.value === 0 || product.rating >= minRating.value
    
    const discountMinMatch = minDiscount.value === 0 || (product.discount && product.discount >= minDiscount.value)

    return categoryMatch && subcategoryMatch && priceMatch && availabilityMatch && 
           reserveMatch && discountMatch && newMatch && ratingMatch && discountMinMatch
  })

  // Ordenar
  if (sortBy.value === 'price-asc') {
    result.sort((a, b) => a.price - b.price)
  } else if (sortBy.value === 'price-desc') {
    result.sort((a, b) => b.price - a.price)
  } else if (sortBy.value === 'name') {
    result.sort((a, b) => a.name.localeCompare(b.name))
  } else if (sortBy.value === 'rating') {
    result.sort((a, b) => b.rating - a.rating)
  } else if (sortBy.value === 'discount') {
    result.sort((a, b) => (b.discount || 0) - (a.discount || 0))
  }

  return result
})

const clearFilters = () => {
  selectedCategories.value = []
  selectedSubcategories.value = []
  priceRange.value = { min: 0, max: 500 }
  onlyAvailable.value = false
  onlyReserves.value = false
  onlyDiscounts.value = false
  onlyNew.value = false
  minRating.value = 0
  minDiscount.value = 0
  sortBy.value = 'newest'
}

const addToCart = (productId: number) => {
  const product = products.value.find(p => p.id === productId)
  if (!product) return
  
  if (product.isReserve) {
    alert(`✅ Reserva realizada para: ${product.name}`)
  } else {
    cartStore.addToCart({
      id: product.id,
      name: product.name,
      category: product.category,
      price: product.price,
      image: product.image
    })
    
    // Agregar producto a la lista de recientemente agregados
    recentlyAddedProducts.value.add(productId)
    
    // Remover después de 1.5 segundos para que se vea la animación
    setTimeout(() => {
      recentlyAddedProducts.value.delete(productId)
    }, 1500)
  }
}

const viewProduct = (productId: number) => {
  router.push(`/producto/${productId}`)
}
</script>

<style scoped>
.products-container {
  background-color: #f9fafb;
  min-height: 100vh;
  padding: 20px;
}

.products-wrapper {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  gap: 30px;
}

.filters-sidebar {
  width: 300px;
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: fit-content;
  position: sticky;
  top: 20px;
  max-height: calc(100vh - 40px);
  overflow-y: auto;
}

.filters-sidebar::-webkit-scrollbar {
  width: 6px;
}

.filters-sidebar::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.filters-sidebar::-webkit-scrollbar-thumb {
  background: #dc2626;
  border-radius: 10px;
}

.filters-title {
  font-size: 22px;
  font-weight: bold;
  color: #1f2937;
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 3px solid #dc2626;
}

.filter-section {
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e5e7eb;
}

.filter-section:last-of-type {
  border-bottom: none;
}

.filter-subtitle {
  font-size: 16px;
  font-weight: bold;
  color: #374151;
  margin: 0 0 12px 0;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.filter-checkbox {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 8px;
}

.filter-checkbox input[type="checkbox"],
.filter-checkbox input[type="radio"] {
  cursor: pointer;
  width: 18px;
  height: 18px;
  accent-color: #dc2626;
}

.filter-checkbox span {
  color: #6b7280;
  font-size: 14px;
}

.filter-checkbox:hover span {
  color: #1f2937;
}

.category-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 10px;
}

.main-category {
  font-weight: 600;
}

.main-category span {
  color: #374151;
  font-size: 14px;
}

.subcategory-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-left: 20px;
  padding-left: 10px;
  border-left: 2px solid #e5e7eb;
}

.subcategory span {
  font-size: 13px;
  color: #6b7280;
}

.rating-stars {
  display: flex;
  align-items: center;
  gap: 2px;
}

.rating-text {
  margin-left: 4px;
  font-size: 12px;
}

.price-range {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.price-label {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #6b7280;
  font-size: 14px;
}

.price-input {
  width: 60px;
  padding: 6px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 14px;
}

.slider {
  width: 100%;
  height: 6px;
  accent-color: #dc2626;
  cursor: pointer;
}

.price-display {
  background-color: #fef2f2;
  color: #dc2626;
  padding: 10px;
  border-radius: 4px;
  text-align: center;
  font-weight: bold;
  font-size: 14px;
}

.sort-select {
  width: 100%;
  padding: 10px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 14px;
  background-color: white;
  cursor: pointer;
}

.clear-filters-btn {
  width: 100%;
  padding: 12px;
  background-color: #dc2626;
  color: white;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 10px;
}

.clear-filters-btn:hover {
  background-color: #b91c1c;
}

.products-main {
  flex: 1;
}

.products-header {
  margin-bottom: 30px;
}

.products-title {
  font-size: 32px;
  font-weight: bold;
  color: #1f2937;
  margin: 0 0 10px 0;
}

.products-count {
  color: #6b7280;
  font-size: 16px;
  margin: 0;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 20px;
}

.product-card {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.15);
}

.product-image-container {
  position: relative;
  width: 100%;
  height: 250px;
  overflow: hidden;
  background-color: #f3f4f6;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.out-of-stock {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.reserve-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  background-color: #3b82f6;
  color: white;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.new-badge {
  position: absolute;
  top: 10px;
  left: 10px;
  background-color: #10b981;
  color: white;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.discount-badge {
  position: absolute;
  top: 50px;
  left: 10px;
  background-color: #dc2626;
  color: white;
  padding: 8px 12px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.product-info {
  padding: 15px;
}

.product-name {
  font-size: 16px;
  font-weight: bold;
  color: #1f2937;
  margin: 0 0 5px 0;
  line-height: 1.3;
}

.product-category {
  color: #9ca3af;
  font-size: 12px;
  margin: 0 0 12px 0;
}

.product-price {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.original-price {
  color: #9ca3af;
  text-decoration: line-through;
  font-size: 14px;
}

.current-price {
  color: #dc2626;
  font-size: 18px;
  font-weight: bold;
}

.add-to-cart-btn {
  width: 100%;
  padding: 10px;
  background-color: #dc2626;
  color: white;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.add-to-cart-btn:hover:not(:disabled) {
  background-color: #b91c1c;
}

.add-to-cart-btn.reserve-btn {
  background-color: #3b82f6;
}

.add-to-cart-btn.reserve-btn:hover {
  background-color: #2563eb;
}

.add-to-cart-btn:disabled {
  background-color: #d1d5db;
  cursor: not-allowed;
}

.add-to-cart-btn.added-animation {
  background-color: #22c55e !important;
  animation: pulse 0.5s ease;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.no-products {
  text-align: center;
  padding: 60px 20px;
  background-color: white;
  border-radius: 8px;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  background-color: white;
  border-radius: 8px;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #f3f4f6;
  border-top: 5px solid #dc2626;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-state p {
  color: #6b7280;
  font-size: 16px;
}

.error-state {
  text-align: center;
  padding: 60px 20px;
  background-color: white;
  border-radius: 8px;
}

.error-state p {
  color: #dc2626;
  font-size: 16px;
  margin-bottom: 20px;
}

.retry-btn {
  padding: 10px 20px;
  background-color: #dc2626;
  color: white;
  border: none;
  border-radius: 4px;
  font-weight: bold;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.retry-btn:hover {
  background-color: #b91c1c;
}

.no-products p {
  color: #6b7280;
  font-size: 18px;
  margin: 0 0 20px 0;
}

.clear-filters-link {
  background-color: #dc2626;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s ease;
}

.clear-filters-link:hover {
  background-color: #b91c1c;
}

@media (max-width: 1024px) {
  .products-wrapper {
    flex-direction: column;
  }

  .filters-sidebar {
    width: 100%;
    position: static;
    max-height: none;
  }

  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  }
}

@media (max-width: 768px) {
  .products-container {
    padding: 15px 10px;
  }

  .products-wrapper {
    gap: 20px;
  }

  .filters-sidebar {
    padding: 15px;
  }

  .filters-title {
    font-size: 20px;
  }

  .filter-subtitle {
    font-size: 15px;
  }

  .products-title {
    font-size: 26px;
  }

  .products-count {
    font-size: 14px;
  }

  .products-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
  }

  .product-image-container {
    height: 200px;
  }

  .product-name {
    font-size: 14px;
  }

  .current-price {
    font-size: 16px;
  }
}

@media (max-width: 480px) {
  .products-container {
    padding: 10px 5px;
  }

  .filters-sidebar {
    padding: 12px;
  }

  .filter-subtitle {
    font-size: 14px;
  }

  .filter-checkbox span,
  .subcategory span {
    font-size: 13px;
  }

  .subcategory-list {
    margin-left: 15px;
    padding-left: 8px;
  }

  .products-title {
    font-size: 22px;
  }

  .products-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .product-card {
    max-width: 100%;
  }

  .product-image-container {
    height: 180px;
  }

  .add-to-cart-btn {
    font-size: 13px;
    padding: 9px;
  }

  .clear-filters-btn {
    padding: 10px;
    font-size: 14px;
  }
}
</style>