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
        <h1 class="register-title">Crear Cuenta</h1>
        <p class="register-subtitle">Completa el formulario para registrarte</p>

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

          <!-- Teléfono -->
          <div class="form-group">
            <label for="phone" class="form-label">Teléfono (Opcional)</label>
            <input
              id="phone"
              v-model="phone"
              type="tel"
              placeholder="+34 123 456 789"
              class="form-input"
            />
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

          <!-- Newsletter -->
          <label class="newsletter-checkbox">
            <input v-model="subscribeNewsletter" type="checkbox" />
            <span>Deseo recibir ofertas y promociones por email</span>
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

        <!-- Divider -->
        <div class="divider">O regístrate con</div>

        <!-- Registro Social -->
        <div class="social-register">
          <button type="button" class="social-button google">
            <span>Google</span>
          </button>
          <button type="button" class="social-button facebook">
            <span>Facebook</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = useRouter()
const authStore = useAuthStore()

const fullname = ref('')
const email = ref('')
const phone = ref('')
const password = ref('')
const confirmPassword = ref('')
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const acceptTerms = ref(false)
const subscribeNewsletter = ref(false)
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
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.register-wrapper {
  display: flex;
  width: 100%;
  max-width: 1000px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.register-form-container {
  flex: 1;
  padding: 40px 30px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  max-height: 100vh;
  overflow-y: auto;
}

.register-title {
  font-size: 28px;
  font-weight: bold;
  color: #1f2937;
  margin-bottom: 10px;
}

.register-title::before {
  content: '🔴 ';
  color: #dc2626;
}

.register-subtitle {
  color: #6b7280;
  font-size: 14px;
  margin-bottom: 30px;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-label {
  font-weight: 600;
  color: #374151;
  font-size: 14px;
}

.form-input {
  padding: 10px 12px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
  font-family: inherit;
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
  padding-right: 40px;
}

.password-toggle {
  position: absolute;
  right: 12px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  padding: 5px;
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

.terms-checkbox,
.newsletter-checkbox {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  cursor: pointer;
  color: #6b7280;
  font-size: 13px;
  line-height: 1.5;
}

.terms-checkbox input,
.newsletter-checkbox input {
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
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 10px;
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
  margin-top: 15px;
  font-size: 14px;
  color: #6b7280;
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

.divider {
  text-align: center;
  color: #d1d5db;
  margin: 20px 0 15px 0;
  position: relative;
  font-size: 13px;
}

.divider::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  width: 100%;
  height: 1px;
  background-color: #e5e7eb;
  z-index: -1;
}

.divider {
  background-color: white;
  padding: 0 10px;
  display: inline-block;
  width: 100%;
}

.social-register {
  display: flex;
  gap: 12px;
}

.social-button {
  flex: 1;
  padding: 10px;
  border: 2px solid #e5e7eb;
  background-color: white;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 13px;
}

.social-button:hover {
  border-color: #dc2626;
  background-color: #fef2f2;
}

.social-button.google {
  color: #1f2937;
}

.social-button.facebook {
  color: #1877f2;
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
    padding: 30px 20px;
    max-height: none;
  }

  .register-title {
    font-size: 24px;
  }
}

@media (min-width: 769px) {
  .register-image {
    display: block;
  }
}

.register-form-container::-webkit-scrollbar {
  width: 6px;
}

.register-form-container::-webkit-scrollbar-track {
  background: #f1f5f9;
}

.register-form-container::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.register-form-container::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}
</style>