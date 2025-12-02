<template>
    <div class="header-container">
        <img src="@/assets/img_cabecera.png" alt="Cabecera" class="header-image" />
    </div>
</template>

<style scoped>
.header-container{
  margin: 0;
  padding:0;
}
    .header-container {
        width: 100%;
        height: 300px;
        overflow: hidden;
        display: flex;
    }
    
    .header-image {
        height: 100%;
        margin: auto;
    }

    .header-container {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 300px;
  overflow: hidden;
  display: flex;
}

.header-image {
  height: 100%;
  margin: auto;
}

/* Ocultar en móvil */
@media (max-width: 768px) {
  .header-container {
    display: none;
  }
}
</style>

<script setup lang="ts">
</script>