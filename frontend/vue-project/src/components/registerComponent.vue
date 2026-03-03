<template>
  <div class="register-container">
    <div class="register-wrapper">
      <!-- Imagen lateral -->
      <div class="register-image">
        <div class="image-overlay">
          <h2>Únete a Rasengan</h2>
          <p>Acceso a ofertas exclusivas y tu carrito personal</p>
        </div>
      </div>

      <!-- Formulario de Registro -->
      <div class="register-form-container">
        <h1 class="register-title">Registrate:</h1>

        <form @submit.prevent="handleRegister" class="register-form">
          <!-- Nombre completo -->
          <div class="form-group">
            <label for="fullname" class="form-label">Nombre Completo</label>
            <input
              id="fullname"
              v-model="fullname"
              type="text"
              placeholder="Juan Pérez"
              class="form-input"
              required
            />
            <span v-if="errors.fullname" class="field-error">{{ errors.fullname }}</span>
          </div>

          <!-- Email -->
          <div class="form-group">
            <label for="email" class="form-label">Email</label>
            <input
              id="email"
              v-model="email"
              type="email"
              placeholder="tu@email.com"
              class="form-input"
              required
            />
            <span v-if="errors.email" class="field-error">{{ errors.email }}</span>
          </div>

          <!-- Contraseña -->
          <div class="form-group">
            <label for="password" class="form-label">Contraseña</label>
            <div class="password-input-container">
              <input
                id="password"
                v-model="password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="••••••••"
                class="form-input"
                required
              />
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="password-toggle"
              >
                {{ showPassword ? '👁️' : '👁️‍🗨️' }}
              </button>
            </div>
            <span v-if="errors.password" class="field-error">{{ errors.password }}</span>
            <div class="password-strength">
              <div :class="['strength-bar', passwordStrength]"></div>
              <span class="strength-text">{{ getPasswordStrengthText() }}</span>
            </div>
          </div>

          <!-- Confirmar contraseña -->
          <div class="form-group">
            <label for="confirmPassword" class="form-label">Confirmar Contraseña</label>
            <div class="password-input-container">
              <input
                id="confirmPassword"
                v-model="confirmPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                placeholder="••••••••"
                class="form-input"
                required
              />
              <button
                type="button"
                @click="showConfirmPassword = !showConfirmPassword"
                class="password-toggle"
              >
                {{ showConfirmPassword ? '👁️' : '👁️‍🗨️' }}
              </button>
            </div>
            <span v-if="errors.confirmPassword" class="field-error">{{ errors.confirmPassword }}</span>
          </div>

          <!-- Términos y Condiciones -->
          <label class="terms-checkbox">
            <input v-model="acceptTerms" type="checkbox" required />
            <span>
              Acepto los
              <a href="#" class="terms-link">términos y condiciones</a>
              y la
              <a href="#" class="terms-link">política de privacidad</a>
            </span>
          </label>

          <!-- Mensaje de error general -->
          <div v-if="generalError" class="error-message">
            {{ generalError }}
          </div>

          <!-- Mensaje de éxito -->
          <div v-if="successMessage" class="success-message">
            {{ successMessage }}
          </div>

          <!-- Botón Registro -->
          <button type="submit" class="register-button" :disabled="loading">
            {{ loading ? 'Creando cuenta...' : 'Crear Cuenta' }}
          </button>
        </form>

        <!-- Enlaces -->
        <div class="register-links">
          <p>
            ¿Ya tienes cuenta?
            <a href="#" @click.prevent="goToLogin" class="link">Inicia sesión aquí</a>
          </p>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { useCartStore } from '@/stores/cartStore'

const router = useRouter()
const authStore = useAuthStore()
const cartStore = useCartStore()

const fullname = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const acceptTerms = ref(false)
const loading = ref(false)
const generalError = ref('')
const successMessage = ref('')

const errors = ref({
  fullname: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const passwordStrength = computed(() => {
  const pwd = password.value
  if (pwd.length < 6) return 'weak'
  if (pwd.length < 10) return 'medium'
  if (/[A-Z]/.test(pwd) && /[0-9]/.test(pwd) && /[^a-zA-Z0-9]/.test(pwd)) return 'strong'
  return 'medium'
})

const getPasswordStrengthText = () => {
  const strength = passwordStrength.value
  if (strength === 'weak') return 'Débil'
  if (strength === 'medium') return 'Moderada'
  return 'Fuerte'
}

const validateForm = () => {
  errors.value = {
    fullname: '',
    email: '',
    password: '',
    confirmPassword: ''
  }
  generalError.value = ''

  let isValid = true

  if (!fullname.value.trim()) {
    errors.value.fullname = 'El nombre es requerido'
    isValid = false
  }

  if (!email.value.includes('@')) {
    errors.value.email = 'Email inválido'
    isValid = false
  }

  if (password.value.length < 8) {
    errors.value.password = 'La contraseña debe tener al menos 8 caracteres'
    isValid = false
  }

  if (password.value !== confirmPassword.value) {
    errors.value.confirmPassword = 'Las contraseñas no coinciden'
    isValid = false
  }

  if (!acceptTerms.value) {
    generalError.value = 'Debes aceptar los términos y condiciones'
    isValid = false
  }

  return isValid
}

const handleRegister = async () => {
  if (!validateForm()) {
    return
  }

  generalError.value = ''
  successMessage.value = ''
  loading.value = true

  try {
    await new Promise((resolve) => setTimeout(resolve, 1500))

    if (await authStore.register(fullname.value, email.value, password.value)) {
      // Sincronizar carrito después del registro
      await cartStore.syncCartWithServer()
      
      successMessage.value = '¡Cuenta creada exitosamente! Redirigiendo...'
      setTimeout(() => {
        router.push('/')
      }, 2000)
    } else {
      generalError.value = 'Este email ya está registrado'
    }
  } catch {
    generalError.value = 'Error al crear la cuenta. Intenta nuevamente.'
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.register-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: clamp(12px, 3vw, 24px);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.register-wrapper {
  display: flex;
  width: 100%;
  max-width: 1000px;
  background-color: white;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.35);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.register-form-container {
  flex: 1;
  padding: clamp(28px, 5vw, 60px) clamp(24px, 4vw, 40px);
  display: flex;
  flex-direction: column;
  justify-content: center;
  background: linear-gradient(180deg, #ffffff 0%, #fcfcff 100%);
}

.register-title {
  font-size: 32px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 18px;
  text-align: center;
  width: 100%;
  max-width: 420px;
  margin-left: auto;
  margin-right: auto;
}

.register-subtitle {
  color: #6b7280;
  font-size: 16px;
  margin-bottom: 40px;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 100%;
  max-width: 420px;
  margin: 0 auto;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-weight: 600;
  color: #374151;
  font-size: 14px;
}

.form-input {
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.25s ease;
  font-family: inherit;
  background-color: #ffffff;
}

.form-input:hover {
  border-color: #d1d5db;
}

.form-input:focus {
  outline: none;
  border-color: #dc2626;
  box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1);
}

.password-input-container {
  position: relative;
  display: flex;
  align-items: center;
}

.password-input-container .form-input {
  width: 100%;
  padding-right: 45px;
}

.password-toggle {
  position: absolute;
  right: 15px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 18px;
  padding: 5px;
  border-radius: 6px;
  transition: background-color 0.2s ease;
}

.password-toggle:hover {
  background-color: #f3f4f6;
}

.field-error {
  color: #dc2626;
  font-size: 12px;
  margin-top: 2px;
}

.password-strength {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 6px;
}

.strength-bar {
  height: 4px;
  flex: 1;
  border-radius: 2px;
  transition: background-color 0.3s ease;
}

.strength-bar.weak {
  background-color: #dc2626;
}

.strength-bar.medium {
  background-color: #f59e0b;
}

.strength-bar.strong {
  background-color: #10b981;
}

.strength-text {
  font-size: 12px;
  color: #6b7280;
}

.terms-checkbox {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  cursor: pointer;
  color: #6b7280;
  font-size: 13px;
  line-height: 1.5;
}

.terms-checkbox input {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #dc2626;
  margin-top: 2px;
  flex-shrink: 0;
}

.terms-link {
  color: #dc2626;
  text-decoration: none;
  font-weight: 600;
}

.terms-link:hover {
  text-decoration: underline;
}

.error-message {
  background-color: #fee2e2;
  color: #991b1b;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 13px;
  border-left: 4px solid #dc2626;
}

.success-message {
  background-color: #dcfce7;
  color: #166534;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 13px;
  border-left: 4px solid #10b981;
}

.register-button {
  padding: 12px 24px;
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 0.2px;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 10px;
  box-shadow: 0 6px 16px rgba(220, 38, 38, 0.2);
}

.register-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(220, 38, 38, 0.3);
}

.register-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.register-links {
  text-align: center;
  margin: 24px auto 0;
  font-size: 14px;
  color: #6b7280;
  width: 100%;
  max-width: 420px;
}

.link {
  color: #dc2626;
  text-decoration: none;
  font-weight: 600;
  cursor: pointer;
}

.link:hover {
  text-decoration: underline;
}

.register-image {
  flex: 1;
  position: relative;
  overflow: hidden;
  display: none;
}

.image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(220, 38, 38, 0.8) 0%, rgba(185, 28, 28, 0.8) 100%);
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 40px;
  color: white;
}

.image-overlay h2 {
  font-size: 28px;
  margin-bottom: 10px;
}

.image-overlay p {
  font-size: 16px;
  opacity: 0.9;
}

@media (max-width: 768px) {
  .register-wrapper {
    flex-direction: column;
  }

  .register-form-container {
    padding: 40px 30px;
  }

  .register-title {
    font-size: 28px;
  }
}

@media (min-width: 769px) {
  .register-image {
    display: block;
  }
}
</style>