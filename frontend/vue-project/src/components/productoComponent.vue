<template>
  <div class="product-detail-container">
    <div v-if="loading" class="loading-state">
      <p>Cargando producto...</p>
    </div>
    
    <div v-else-if="error" class="error-state">
      <p>{{ error }}</p>
      <button @click="() => router.back()" class="back-btn">Volver atrás</button>
    </div>

    <div v-else class="product-detail-wrapper">
      <!-- Galería de imágenes -->
      <div class="product-gallery">
        <div class="main-image">
          <img :src="currentImage" :alt="product.name" />
          <span v-if="product.discount" class="discount-badge">-{{ product.discount }}%</span>
          <span v-if="!product.available" class="out-of-stock-badge">Agotado</span>
        </div>
        <div class="thumbnail-images">
          <img
            v-for="(image, index) in product.images"
            :key="index"
            :src="image"
            :alt="product.name"
            @click="currentImage = image"
            :class="{ active: currentImage === image }"
            class="thumbnail"
          />
        </div>
      </div>

      <!-- Información del producto -->
      <div class="product-info">
        <h1 class="product-name">{{ product.name }}</h1>
        <p class="product-category">{{ product.category }}</p>

        <!-- Precio -->
        <div class="price-section">
          <div class="price">
            <span v-if="product.originalPrice" class="original-price">
              {{ product.originalPrice }}€
            </span>
            <span class="current-price">{{ product.price }}€</span>
          </div>
          <div v-if="product.discount" class="save-amount">
            Ahorras {{ (product.originalPrice! - product.price).toFixed(2) }}€
          </div>
        </div>

        <!-- Descripción -->
        <div class="description">
          <h3>Descripción</h3>
          <p>{{ product.description }}</p>
        </div>

        <!-- Especificaciones -->
        <div class="specifications">
          <h3>Especificaciones</h3>
          <div class="specs-grid">
            <div class="spec-item">
              <span class="spec-label">Autor:</span>
              <span class="spec-value">{{ product.author }}</span>
            </div>
            <div class="spec-item">
              <span class="spec-label">Editorial:</span>
              <span class="spec-value">{{ product.publisher }}</span>
            </div>
            <div class="spec-item">
              <span class="spec-label">Año:</span>
              <span class="spec-value">{{ product.year }}</span>
            </div>
            <div class="spec-item">
              <span class="spec-label">Páginas:</span>
              <span class="spec-value">{{ product.pages }}</span>
            </div>
          </div>
        </div>

        <!-- Cantidad y botones -->
        <div class="actions">
          <div class="quantity-selector">
            <label for="quantity">Cantidad:</label>
            <div class="quantity-controls">
              <button @click="quantity > 1 ? quantity-- : null" class="qty-btn">-</button>
              <input v-model.number="quantity" type="number" min="1" id="quantity" />
              <button @click="quantity++" class="qty-btn">+</button>
            </div>
          </div>

          <button
            @click="addToCart"
            :disabled="!product.available"
            class="add-to-cart-btn"
          >
            🛒 {{ product.available ? 'Agregar al Carrito' : 'No Disponible' }}
          </button>

          <button @click="addToWishlist" class="wishlist-btn">
            {{ isInWishlist ? '❤️' : '🤍' }}
          </button>
        </div>

        <!-- Información adicional -->
        <div class="additional-info">
          <div class="info-item">
            <span class="info-icon">🚚</span>
            <div>
              <p class="info-title">Envío gratis</p>
              <p class="info-text">En compras mayores a 50€</p>
            </div>
          </div>
          <div class="info-item">
            <span class="info-icon">🔄</span>
            <div>
              <p class="info-title">Devoluciones fáciles</p>
              <p class="info-text">Devuelve en 30 días</p>
            </div>
          </div>
          <div class="info-item">
            <span class="info-icon">🛡️</span>
            <div>
              <p class="info-title">Garantía</p>
              <p class="info-text">Producto original garantizado</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Reseñas -->
    <div class="reviews-section">
      <h2>Reseñas de clientes</h2>
      <div class="reviews-list">
        <div v-for="review in reviews" :key="review.id" class="review-item">
          <div class="review-header">
            <div class="reviewer-info">
              <h4 class="reviewer-name">{{ review.name }}</h4>
            </div>
            <span class="review-date">{{ review.date }}</span>
          </div>
          <p class="review-text">{{ review.text }}</p>
        </div>
      </div>
    </div>

    <!-- Productos relacionados -->
    <div class="related-products">
      <h2>Productos relacionados</h2>
      <div class="products-grid">
        <div v-for="relProduct in relatedProducts" :key="relProduct.id" class="product-card">
          <img :src="relProduct.image" :alt="relProduct.name" />
          <h4>{{ relProduct.name }}</h4>
          <p class="price">{{ relProduct.price }}€</p>
          <button class="view-btn" @click="viewProduct(relProduct.id)">Ver detalles</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useCartStore } from '@/stores/cartStore';
import api from '../api/axios';

const router = useRouter();
const route = useRoute();
const cartStore = useCartStore();

const quantity = ref(1);
const isInWishlist = ref(false);
const currentImage = ref('https://images.unsplash.com/photo-1612036782180-69db8e541e1f?w=600&h=800&fit=crop');
const loading = ref(true);
const error = ref<string | null>(null);

interface Product {
  id: number;
  name: string;
  category: string;
  price: number;
  originalPrice?: number;
  discount?: number;
  rating: number;
  reviews: number;
  available: boolean;
  description: string;
  author: string;
  publisher: string;
  year: number;
  pages: number;
  images: string[];
}

const product = ref<Product>({
  id: 1,
  name: 'One Piece Vol. 100 - Edición Especial',
  category: 'Manga',
  price: 12.99,
  originalPrice: 14.99,
  discount: 10,
  rating: 5,
  reviews: 145,
  available: true,
  description: 'El volumen 100 de One Piece es un hito histórico en la serie. Esta edición especial incluye extras exclusivos, póster desplegable y cubierta holográfica. Una debe para cualquier fan de One Piece.',
  author: 'Eiichiro Oda',
  publisher: 'Planeta Manga',
  year: 2024,
  pages: 192,
  images: [
    'https://images.unsplash.com/photo-1612036782180-69db8e541e1f?w=600&h=800&fit=crop',
    'https://images.unsplash.com/photo-1594743315886-a18d195ce546?w=600&h=800&fit=crop',
    'https://images.unsplash.com/photo-1535016120754-fd45c1d1ff97?w=600&h=800&fit=crop'
  ]
});

// Cargar producto desde la API basado en el ID de la ruta
const loadProduct = async () => {
  try {
    loading.value = true;
    error.value = null;
    const productId = route.params.id;
    const response = await api.get(`/api/products/${productId}`);
    
    product.value = {
      ...response.data,
      images: response.data.images || [response.data.image]
    };
    
    if (product.value.images.length > 0) {
      currentImage.value = product.value.images[0];
    }
  } catch (err) {
    console.error('Error cargando el producto:', err);
    error.value = 'No se pudo cargar el producto. Por favor, intenta más tarde.';
  } finally {
    loading.value = false;
  }
};

const reviews = [
  {
    id: 1,
    name: 'Juan García',
    rating: 5,
    date: 'Hace 2 días',
    text: 'Excelente calidad y entrega rápida. La edición especial es aún mejor de lo que esperaba.'
  },
  {
    id: 2,
    name: 'María López',
    rating: 5,
    date: 'Hace 1 semana',
    text: 'Perfecto para un fan de One Piece. El póster desplegable es hermoso.'
  },
  {
    id: 3,
    name: 'Carlos Rodríguez',
    rating: 4,
    date: 'Hace 2 semanas',
    text: 'Muy bueno, aunque llegó con un pequeño daño en la esquina. El servicio al cliente fue muy atento.'
  }
];

const relatedProducts = [
  {
    id: 2,
    name: 'One Piece Vol. 99',
    price: 12.99,
    image: 'https://images.unsplash.com/photo-1612036782180-69db8e541e1f?w=400&h=500&fit=crop'
  },
  {
    id: 3,
    name: 'Attack on Titan Vol. 1',
    price: 11.99,
    image: 'https://images.unsplash.com/photo-1594743315886-a18d195ce546?w=400&h=500&fit=crop'
  },
  {
    id: 4,
    name: 'Naruto Vol. 72',
    price: 10.99,
    image: 'https://images.unsplash.com/photo-1535016120754-fd45c1d1ff97?w=400&h=500&fit=crop'
  },
  {
    id: 5,
    name: 'Demon Slayer Vol. 1',
    price: 13.99,
    image: 'https://images.unsplash.com/photo-1612036782180-69db8e541e1f?w=400&h=500&fit=crop'
  }
];

onMounted(() => {
  loadProduct();
});

const addToCart = () => {
  cartStore.addToCart({
    id: product.value.id,
    name: product.value.name,
    category: product.value.category,
    price: product.value.price,
    image: product.value.images[0]
  }, quantity.value);
  alert(`✅ Agregado ${quantity.value} unidad(es) de ${product.value.name} al carrito`);
};

const addToWishlist = () => {
  isInWishlist.value = !isInWishlist.value;
  alert(isInWishlist.value ? '❤️ Agregado a favoritos' : '🤍 Removido de favoritos');
};

const viewProduct = (productId: number) => {
  router.push(`/producto/${productId}`);
  window.scrollTo(0, 0); // Scroll al inicio de la página
};
</script>

<style scoped>
.product-detail-container {
  background-color: #f9fafb;
  padding: 30px 20px;
}

.loading-state,
.error-state {
  max-width: 1200px;
  margin: 100px auto;
  text-align: center;
  padding: 40px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.loading-state p,
.error-state p {
  font-size: 18px;
  color: #6b7280;
  margin: 0 0 20px 0;
}

.back-btn {
  padding: 10px 20px;
  background-color: #dc2626;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background-color: #b91c1c;
  transform: translateY(-2px);
}

.product-detail-wrapper {
  max-width: 1200px;
  margin: 0 auto 60px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  background-color: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* Galería */
.product-gallery {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.main-image {
  position: relative;
  width: 100%;
  height: 500px;
  background-color: #f3f4f6;
  border-radius: 8px;
  overflow: hidden;
}

.main-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.discount-badge {
  position: absolute;
  top: 15px;
  left: 15px;
  background-color: #dc2626;
  color: white;
  padding: 8px 12px;
  border-radius: 6px;
  font-weight: bold;
  font-size: 14px;
}

.out-of-stock-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  background-color: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 8px 12px;
  border-radius: 6px;
  font-weight: bold;
  font-size: 14px;
}

.thumbnail-images {
  display: flex;
  gap: 10px;
  overflow-x: auto;
}

.thumbnail {
  width: 80px;
  height: 80px;
  border-radius: 6px;
  border: 2px solid #e5e7eb;
  cursor: pointer;
  object-fit: cover;
  transition: all 0.3s ease;
}

.thumbnail:hover,
.thumbnail.active {
  border-color: #dc2626;
}

/* Info del producto */
.product-info {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.product-name {
  font-size: 28px;
  font-weight: bold;
  color: #1f2937;
  margin: 0;
}

.product-category {
  color: #9ca3af;
  font-size: 14px;
  margin: 0;
}

.product-rating {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stars {
  display: flex;
  gap: 4px;
  font-size: 18px;
}

.rating-count {
  color: #6b7280;
  font-size: 14px;
}

.price-section {
  border-top: 2px solid #e5e7eb;
  border-bottom: 2px solid #e5e7eb;
  padding: 20px 0;
}

.price {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.original-price {
  color: #9ca3af;
  text-decoration: line-through;
  font-size: 18px;
}

.current-price {
  color: #dc2626;
  font-size: 32px;
  font-weight: bold;
}

.save-amount {
  color: #10b981;
  font-weight: 600;
  font-size: 14px;
}

.description h3,
.specifications h3 {
  font-size: 18px;
  font-weight: bold;
  color: #1f2937;
  margin: 0 0 10px 0;
}

.description p {
  color: #6b7280;
  line-height: 1.6;
  margin: 0;
}

.specifications {
  background-color: #f9fafb;
  padding: 15px;
  border-radius: 8px;
}

.specs-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.spec-item {
  display: flex;
  gap: 8px;
}

.spec-label {
  font-weight: 600;
  color: #374151;
  min-width: 80px;
}

.spec-value {
  color: #6b7280;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.quantity-selector {
  display: flex;
  align-items: center;
  gap: 15px;
}

.quantity-selector label {
  font-weight: 600;
  color: #374151;
}

.quantity-controls {
  display: flex;
  align-items: center;
  border: 2px solid #e5e7eb;
  border-radius: 6px;
  overflow: hidden;
}

.qty-btn {
  padding: 8px 12px;
  border: none;
  background-color: #f3f4f6;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.3s ease;
}

.qty-btn:hover {
  background-color: #e5e7eb;
}

#quantity {
  width: 50px;
  border: none;
  text-align: center;
  font-size: 16px;
  font-weight: bold;
}

.add-to-cart-btn {
  padding: 15px;
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.add-to-cart-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(220, 38, 38, 0.3);
}

.add-to-cart-btn:disabled {
  background-color: #d1d5db;
  cursor: not-allowed;
}

.wishlist-btn {
  padding: 12px;
  border: 2px solid #e5e7eb;
  background-color: white;
  border-radius: 8px;
  font-size: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.wishlist-btn:hover {
  border-color: #dc2626;
  background-color: #fef2f2;
}

.additional-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px;
  background-color: #f9fafb;
  border-radius: 8px;
}

.info-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.info-title {
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 4px 0;
  font-size: 14px;
}

.info-text {
  color: #6b7280;
  margin: 0;
  font-size: 13px;
}

/* Reseñas */
.reviews-section {
  max-width: 1200px;
  margin: 0 auto 60px;
}

.reviews-section h2 {
  font-size: 24px;
  font-weight: bold;
  color: #1f2937;
  margin: 0 0 30px 0;
}

.reviews-list {
  display: grid;
  gap: 20px;
}

.review-item {
  background-color: white;
  padding: 25px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.reviewer-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.reviewer-name {
  font-weight: 600;
  color: #1f2937;
  margin: 0;
  font-size: 16px;
}

.review-rating {
  display: flex;
  gap: 4px;
  font-size: 16px;
}

.review-date {
  color: #9ca3af;
  font-size: 13px;
}

.review-text {
  color: #6b7280;
  line-height: 1.6;
  margin: 0;
}

/* Productos relacionados */
.related-products {
  max-width: 1200px;
  margin: 0 auto;
}

.related-products h2 {
  font-size: 24px;
  font-weight: bold;
  color: #1f2937;
  margin: 0 0 30px 0;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.product-card {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.product-card:hover {
  transform: translateY(-5px);
}

.product-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.product-card h4 {
  font-size: 14px;
  font-weight: bold;
  color: #1f2937;
  margin: 12px;
  line-height: 1.3;
}

.product-card .price {
  color: #dc2626;
  font-weight: bold;
  margin: 0 12px;
}

.view-btn {
  width: calc(100% - 24px);
  margin: 12px;
  padding: 10px;
  background-color: #dc2626;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.view-btn:hover {
  background-color: #b91c1c;
}

@media (max-width: 768px) {
  .product-detail-wrapper {
    grid-template-columns: 1fr;
    gap: 30px;
    padding: 20px;
  }

  .product-name {
    font-size: 22px;
  }

  .current-price {
    font-size: 24px;
  }

  .products-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  }
}
</style>