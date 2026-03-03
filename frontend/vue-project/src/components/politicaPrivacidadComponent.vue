<template>
  <div class="legal-container">
    <h1>Política de Privacidad</h1>
    <p>Última actualización: {{ fecha }}</p>

    <section>
      <h2>1. Información que recopilamos</h2>
      <p>
        Podemos recopilar información personal como nombre, correo electrónico
        y datos de uso del sitio.
      </p>
    </section>

    <section>
      <h2>2. Uso de la información</h2>
      <p>
        Utilizamos la información para mejorar nuestros servicios y comunicarnos
        con los usuarios.
      </p>
    </section>

    <section>
      <h2>3. Protección de datos</h2>
      <p>
        Implementamos medidas de seguridad para proteger la información personal.
      </p>
    </section>

    <section>
      <h2>4. Derechos del usuario</h2>
      <p>
        El usuario puede solicitar acceso, modificación o eliminación de sus datos.
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