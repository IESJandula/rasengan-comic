<template>
  <div class="legal-container">
    <h1>Política de Cookies</h1>
    <p>Última actualización: {{ fecha }}</p>

    <section>
      <h2>¿Qué son las cookies?</h2>
      <p>
        Las cookies son pequeños archivos que se almacenan en tu dispositivo
        para mejorar la experiencia de navegación.
      </p>
    </section>

    <section>
      <h2>Tipos de cookies que utilizamos</h2>
      <ul>
        <li><strong>Cookies esenciales:</strong> necesarias para el funcionamiento del sitio.</li>
        <li><strong>Cookies de análisis:</strong> nos ayudan a entender el comportamiento del usuario.</li>
        <li><strong>Cookies de marketing:</strong> utilizadas para personalizar anuncios.</li>
      </ul>
    </section>

    <section>
      <h2>Gestión de cookies</h2>
      <p>
        Puedes configurar tu navegador para rechazar o eliminar cookies en cualquier momento.
      </p>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const fecha = ref(new Date().toLocaleDateString())
</script>

<style scoped>
.legal-container {
  max-width: 900px;
  margin: 40px auto;
  padding: 20px;
  line-height: 1.6;
}
</style>