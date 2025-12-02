<script setup>
import { ref, onMounted } from 'vue'
import api from '../api/axios'  // 👈 Importas tu Axios configurado

const mensaje = ref("Cargando...")

onMounted(async () => {
  try {
    const res = await api.get("/hola") // 👈 ya no pones el URL completo
    mensaje.value = res.data
  } catch (e) {
    mensaje.value = "Error al conectar"
  }
})
</script>

<template>
  <h1>{{ mensaje }}</h1>
</template>
