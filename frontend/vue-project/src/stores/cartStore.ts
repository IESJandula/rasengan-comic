import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface CartItem {
  id: number
  name: string
  category: string
  price: number
  quantity: number
  image: string
}

export const useCartStore = defineStore('cart', () => {
  const items = ref<CartItem[]>([])

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
  }

  // Incrementar cantidad de un producto
  const incrementQuantity = (itemId: number) => {
    const item = items.value.find(i => i.id === itemId)
    if (item) {
      item.quantity++
    }
  }

  // Decrementar cantidad de un producto
  const decrementQuantity = (itemId: number) => {
    const item = items.value.find(i => i.id === itemId)
    if (item && item.quantity > 1) {
      item.quantity--
    }
  }

  // Remover un producto del carrito
  const removeItem = (itemId: number) => {
    items.value = items.value.filter(i => i.id !== itemId)
  }

  // Limpiar todo el carrito
  const clearCart = () => {
    items.value = []
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
    getItemQuantity
  }
})
