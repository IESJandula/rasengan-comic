<template>
  <div class="legal-container">
    <h1>Términos y Condiciones</h1>
    <p>Última actualización: {{ fecha }}</p>

    <section>
      <h2>1. Aceptación de los términos</h2>
      <p>
        Al acceder y utilizar este sitio web, aceptas cumplir con los presentes
        términos y condiciones.
      </p>
    </section>

    <section>
      <h2>2. Uso del sitio</h2>
      <p>
        El usuario se compromete a utilizar el sitio de manera lícita y respetuosa,
        sin infringir derechos de terceros.
      </p>
    </section>

    <section>
      <h2>3. Propiedad intelectual</h2>
      <p>
        Todo el contenido de este sitio (textos, imágenes, logos, código) es propiedad
        del titular y está protegido por las leyes aplicables.
      </p>
    </section>

    <section>
      <h2>4. Limitación de responsabilidad</h2>
      <p>
        No nos hacemos responsables por daños derivados del uso indebido del sitio.
      </p>
    </section>

    <section>
      <h2>5. Modificaciones</h2>
      <p>
        Nos reservamos el derecho de modificar estos términos en cualquier momento.
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
h1, h2 {
  margin-bottom: 10px;
}
section {
  margin-bottom: 20px;
}
</style>