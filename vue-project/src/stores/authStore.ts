import { defineStore } from 'pinia'
import { ref } from 'vue'

interface User {
  email: string
  name: string
  avatar: string
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const isAuthenticated = ref(false)
  const token = ref('')

  // Usuarios "falsos" para demostración
  const fakeUsers = [
    { email: 'admin@rasengacomics.com', password: '123456', name: 'Admin' },
    { email: 'usuario@rasengacomics.com', password: '123456', name: 'Usuario' },
    { email: 'test@test.com', password: 'password123', name: 'Test User' }
  ]

  const login = (email: string, password: string) => {
    // Buscar usuario en la lista falsa
    const foundUser = fakeUsers.find(
      u => u.email === email && u.password === password
    )

    if (foundUser) {
      user.value = {
        email: foundUser.email,
        name: foundUser.name,
        avatar: `https://via.placeholder.com/100?text=${foundUser.name}`
      }
      isAuthenticated.value = true
      token.value = 'fake-token-' + Date.now()
      
      // Guardar en localStorage
      localStorage.setItem('auth_token', token.value)
      localStorage.setItem('user_data', JSON.stringify(user.value))
      
      return true
    }
    return false
  }

  const register = (fullname: string, email: string, password: string) => {
    // Verificar si el email ya existe
    const userExists = fakeUsers.some(u => u.email === email)
    
    if (userExists) {
      return false
    }

    // Agregar nuevo usuario a la lista falsa
    fakeUsers.push({
      email,
      password,
      name: fullname
    })

    // Iniciar sesión automáticamente
    login(email, password)
    return true
  }

  const logout = () => {
    user.value = null
    isAuthenticated.value = false
    token.value = ''
    localStorage.removeItem('auth_token')
    localStorage.removeItem('user_data')
  }

  const loadFromStorage = () => {
    const storedToken = localStorage.getItem('auth_token')
    const storedUser = localStorage.getItem('user_data')

    if (storedToken && storedUser) {
      token.value = storedToken
      user.value = JSON.parse(storedUser)
      isAuthenticated.value = true
    }
  }

  return {
    user,
    isAuthenticated,
    token,
    login,
    register,
    logout,
    loadFromStorage,
    fakeUsers
  }
})