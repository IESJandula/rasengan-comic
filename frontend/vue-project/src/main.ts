import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/authStore' // <- importar la store

const app = createApp(App)

app.use(createPinia())
app.use(router)

app.mount('#app')

// Inicializar Firebase para mantener sesión
const authStore = useAuthStore()
authStore.initFirebase()
