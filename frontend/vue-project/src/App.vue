<template>
  <div>
    <headerComponent />
    <navComponent />
    <breadcrumbsComponent />
    <router-view />
    <scrollToTopButton />
    <footerComponent />
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/authStore'
import navComponent from './components/navComponent.vue'
import headerComponent from './components/headerComponent.vue'
import footerComponent from './components/footerComponent.vue'
import breadcrumbsComponent from './components/breadcrumbsComponent.vue'
import scrollToTopButton from './components/scrollToTopButton.vue'

const authStore = useAuthStore()

onMounted(() => {
  // Cargar datos de sesión al iniciar
  authStore.loadFromStorage()
})
</script>
<style>
/* IMPORTANTE: Sin "scoped" para que afecte globalmente */
html, body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  overflow-x: hidden;
}

#app {
  margin: 0;
  padding: 0;
  width: 100%;
  min-height: 100vh;
}

* {
  box-sizing: border-box;
}
</style>