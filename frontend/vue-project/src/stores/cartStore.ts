import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import api from '@/api/axios'
import { useAuthStore } from './authStore'

export interface CartItem {
  id: number
  name: string
  category: string
  price: number
  quantity: number
  image: string
}

const CART_STORAGE_KEY = 'rasengan_comic_cart'

// Cargar carrito desde localStorage
const loadCartFromStorage = (): CartItem[] => {
  try {
    const stored = localStorage.getItem(CART_STORAGE_KEY)
    return stored ? JSON.parse(stored) : []
  } catch (error) {
    console.error('Error al cargar carrito desde localStorage:', error)
    return []
  }
}

// Guardar carrito en localStorage
const saveCartToStorage = (items: CartItem[]) => {
  try {
    localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(items))
  } catch (error) {
    console.error('Error al guardar carrito en localStorage:', error)
  }
}

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>(loadCartFromStorage())

  // Computed para obtener el total de items
  const totalItems = computed(() => {
    return items.value.reduce((sum, item) => sum + item.quantity, 0)
  })

  // Computed para obtener el subtotal
  const subtotal = computed(() => {
    return items.value.reduce((sum, item) => sum + item.price * item.quantity, 0)
  })

  // Computed para calcular envío
  const shipping = computed(() => {
    return subtotal.value > 50 ? 0 : 10
  })

  // Computed para calcular impuestos dinámicos según la categoría
  // Comics y Manga: 10%, resto: 21%
  const taxes = computed(() => {
    return items.value.reduce((totalTaxes, item) => {
      const taxRate = (item.category === 'Comics' || item.category === 'Manga') ? 0.10 : 0.21
      return totalTaxes + (item.price * item.quantity * taxRate)
    }, 0)
  })

  // Computed para calcular el total
  const total = computed(() => {
    return subtotal.value + shipping.value + taxes.value
  })

  // Agregar producto al carrito
  const addToCart = (product: Omit<CartItem, 'quantity'>, quantity: number = 1) => {
    const existingItem = items.value.find(item => item.id === product.id)
    
    if (existingItem) {
      existingItem.quantity += quantity
    } else {
      items.value.push({
        ...product,
        quantity
      })
    }
    saveCartToStorage(items.value)
  }

  // Incrementar cantidad de un producto
  const incrementQuantity = (itemId: number) => {
    const item = items.value.find(i => i.id === itemId)
    if (item) {
      item.quantity++
      saveCartToStorage(items.value)
    }
  }

  // Decrementar cantidad de un producto
  const decrementQuantity = (itemId: number) => {
    const item = items.value.find(i => i.id === itemId)
    if (item && item.quantity > 1) {
      item.quantity--
      saveCartToStorage(items.value)
    }
  }

  // Remover un producto del carrito
  const removeItem = (itemId: number) => {
    items.value = items.value.filter(i => i.id !== itemId)
    saveCartToStorage(items.value)
  }

  // Limpiar todo el carrito
  const clearCart = () => {
    items.value = []
    saveCartToStorage(items.value)
  }

  // Verificar si un producto está en el carrito
  const isInCart = (productId: number) => {
    return items.value.some(item => item.id === productId)
  }

  // Obtener la cantidad de un producto en el carrito
  const getItemQuantity = (productId: number) => {
    const item = items.value.find(i => i.id === productId)
    return item ? item.quantity : 0
  }

  // Sincronizar carrito con el servidor cuando el usuario se loguea
  const syncCartWithServer = async () => {
    const authStore = useAuthStore()
    
    if (!authStore.user?.uid) {
      console.log('📌 Usuario no autenticado, no se sincroniza carrito')
      return
    }

    try {
      console.log('🔄 Sincronizando carrito con servidor...')
      
      // Obtener carrito del servidor
      const response = await api.get(`/carrito/${authStore.user.uid}`)
      const serverCart = response.data
      
      if (serverCart && serverCart.cantidadItems > 0) {
        // Si el servidor tiene items, los mostramos
        console.log('✅ Carrito cargado desde servidor:', serverCart)
        // Aquí se cargarían los items del servidor si la API retornara los items
        // Por ahora, mantenemos el carrito local que se sincronizará después
      } else {
        // Si el servidor no tiene items, guardamos los locales
        console.log('📌 Guardando carrito local en servidor...')
        await api.post('/carrito', {
          usuarioUid: authStore.user.uid,
          items: items.value
        })
      }
    } catch (err) {
      console.warn('⚠️ No se pudo sincronizar carrito con servidor:', err)
      // Si falla, solo mantenemos el carrito local
    }
  }

  // Limpiar carrito local cuando el usuario se deslogea
  const clearLocalCart = () => {
    // No limpiamos aquí, solo limpiamos cuando se logea un usuario diferente
  }

  return {
    items,
    totalItems,
    subtotal,
    shipping,
    taxes,
    total,
    addToCart,
    incrementQuantity,
    decrementQuantity,
    removeItem,
    clearCart,
    isInCart,
    getItemQuantity,
    syncCartWithServer,
    clearLocalCart
  }
})
