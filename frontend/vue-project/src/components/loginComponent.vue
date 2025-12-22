<template>
  <div class="login-container">
    <div class="login-wrapper">
      <!-- Formulario de Login -->
      <div class="login-form-container">
        <h1 class="login-title">Rasengan Comics</h1>
        <p class="login-subtitle">Inicia sesión en tu cuenta</p>

        <form @submit.prevent="handleLogin" class="login-form">
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
          </div>

          <!-- Recordarme -->
          <label class="remember-me">
            <input v-model="rememberMe" type="checkbox" />
            <span>Recuérdame</span>
          </label>

          <!-- Mensaje de error -->
          <div v-if="error" class="error-message">
            {{ error }}
          </div>

          <!-- Botón Login -->
          <button type="submit" class="login-button" :disabled="loading">
            {{ loading ? 'Iniciando sesión...' : 'Iniciar Sesión' }}
          </button>
        </form>

        <!-- Enlaces -->
        <div class="login-links">
          <a href="#" @click.prevent="goToRegister" class="link">¿No tienes cuenta? Regístrate</a>
          <a href="#" class="link">¿Olvidaste tu contraseña?</a>
        </div>

        <!-- Divider -->
        <div class="divider">O continúa con</div>

        <!-- Login Social -->
        <div class="social-login">
          <button type="button" class="social-button google">
            <span>Google</span>
          </button>
          <button type="button" class="social-button facebook">
            <span>Facebook</span>
          </button>
        </div>
      </div>

      <!-- Imagen lateral -->
      <div class="login-image">
        <div class="image-overlay">
          <h2>Bienvenido a Rasengan Comics</h2>
          <p>Tu tienda favorita de comics, mangas y más</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
// authStore not used here, using firebase auth directly
import { signInWithEmailAndPassword } from "firebase/auth";
import { auth } from "@/firebase";


const router = useRouter();

const email = ref('');
const password = ref('');
const showPassword = ref(false);
const rememberMe = ref(false);
const loading = ref(false);
const error = ref('');

const handleLogin = async () => {
  error.value = '';
  loading.value = true;

  try {
    if (!email.value || !password.value) {
      error.value = 'Por favor completa todos los campos';
      return;
    }

    await signInWithEmailAndPassword(
      auth,
      email.value,
      password.value
    );

    // 🔐 Usuario autenticado correctamente
    router.push('/');

  } catch (caught) {
    type AuthError = { code?: string }
    const err = caught as AuthError
    switch (err.code) {
      case 'auth/user-not-found':
        error.value = 'El usuario no existe'
        break
      case 'auth/wrong-password':
        error.value = 'Contraseña incorrecta'
        break
      case 'auth/invalid-email':
        error.value = 'Email inválido'
        break
      default:
        error.value = 'Error al iniciar sesión'
    }
  } finally {
    loading.value = false;
  }
};


const goToRegister = () => {
  router.push('/registro');
};
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.login-wrapper {
  display: flex;
  width: 100%;
  max-width: 1000px;
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  overflow: hidden;
}

.login-form-container {
  flex: 1;
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.login-title {
  font-size: 32px;
  font-weight: bold;
  color: #1f2937;
  margin-bottom: 10px;
}

.login-title::before {
  content: '🔴 ';
  color: #dc2626;
}

.login-subtitle {
  color: #6b7280;
  font-size: 16px;
  margin-bottom: 40px;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
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
}

.remember-me {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #6b7280;
  font-size: 14px;
  margin-top: 5px;
}

.remember-me input {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #dc2626;
}

.error-message {
  background-color: #fee2e2;
  color: #991b1b;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  border-left: 4px solid #dc2626;
}

.login-button {
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

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(220, 38, 38, 0.3);
}

.login-button:active {
  transform: translateY(0);
}

.login-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.login-links {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
  gap: 10px;
  flex-wrap: wrap;
}

.link {
  color: #dc2626;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;
  cursor: pointer;
}

.link:hover {
  color: #b91c1c;
  text-decoration: underline;
}

.divider {
  text-align: center;
  color: #d1d5db;
  margin: 30px 0 20px 0;
  position: relative;
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

.social-login {
  display: flex;
  gap: 12px;
}

.social-button {
  flex: 1;
  padding: 12px;
  border: 2px solid #e5e7eb;
  background-color: white;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
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

.login-image {
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
  .login-wrapper {
    flex-direction: column;
  }

  .login-form-container {
    padding: 40px 30px;
  }

  .login-title {
    font-size: 28px;
  }

  .login-links {
    flex-direction: column;
  }

  .link {
    width: 100%;
    text-align: center;
  }
}

@media (min-width: 769px) {
  .login-image {
    display: block;
  }
}
</style>