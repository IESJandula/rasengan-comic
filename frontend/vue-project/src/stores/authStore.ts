import { defineStore } from 'pinia'
import { ref } from 'vue'
import { auth } from '@/firebase'
import { signInWithEmailAndPassword, signOut, onAuthStateChanged, createUserWithEmailAndPassword } from 'firebase/auth'
import type { User } from 'firebase/auth'

interface AuthUser {
  email: string | null
  name?: string
  avatar?: string
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<AuthUser | null>(null)
  const isAuthenticated = ref(false)
  const loading = ref(true)

  // Iniciar sesión con Firebase
  const loginFirebase = async (email: string, password: string) => {
    try {
      const res = await signInWithEmailAndPassword(auth, email, password)
      user.value = {
        email: res.user.email,
        name: res.user.email?.split('@')[0] ?? ''
      }
      isAuthenticated.value = true
      return true
    } catch (err) {
      console.error(err)
      return false
    }
  }

  const logoutFirebase = async () => {
    await signOut(auth)
    user.value = null
    isAuthenticated.value = false
  }

  // Alias y funciones usadas por componentes
  const logout = async () => {
    await logoutFirebase()
  }

  const register = async (fullname: string, email: string, password: string) => {
    try {
      const res = await createUserWithEmailAndPassword(auth, email, password)
      user.value = {
        email: res.user.email,
        name: fullname ?? (res.user.email?.split('@')[0] ?? '')
      }
      isAuthenticated.value = true
      return true
    } catch (err) {
      console.error('register error', err)
      return false
    }
  }

  const loadFromStorage = () => {
    // Mantener compatibilidad: inicializa el listener de Firebase
    initFirebase()
  }

  // Mantener sesión al refrescar
  const initFirebase = () => {
    onAuthStateChanged(auth, (firebaseUser: User | null) => {
      if (firebaseUser) {
        user.value = {
          email: firebaseUser.email,
          name: firebaseUser.email?.split('@')[0] ?? ''
        }
        isAuthenticated.value = true
      } else {
        user.value = null
        isAuthenticated.value = false
      }
      loading.value = false
    })
  }

  return {
    user,
    isAuthenticated,
    loading,
    loginFirebase,
    logoutFirebase,
    logout,
    register,
    loadFromStorage,
    initFirebase
  }
})
