<template>
  <div class="legalidad-container">
    <nav class="legalidad-nav">
      <h2 class="nav-title">Información Legal</h2>
      <ul class="nav-links">
        <li>
          <router-link :to="{name: 'Privacidad'}" class="nav-link">
            Política de Privacidad
          </router-link>
        </li>
        <li>
          <router-link :to="{name: 'Terminos'}" class="nav-link">
            Términos y Condiciones
          </router-link>
        </li>
        <li>
          <router-link :to="{name: 'Cookies'}" class="nav-link">
            Política de Cookies
          </router-link>
        </li>
      </ul>
    </nav>
    <div class="legalidad-content">
      <router-view />
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
.legalidad-container {
  min-height: 100vh;
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  gap: 30px;
}

.legalidad-nav {
  flex-shrink: 0;
  width: 280px;
  background-color: #f9fafb;
  padding: 25px;
  border-radius: 8px;
  height: fit-content;
  position: sticky;
  top: 20px;
}

.nav-title {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 20px 0;
  padding-bottom: 15px;
  border-bottom: 2px solid #dc2626;
}

.nav-links {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.nav-link {
  display: block;
  padding: 12px 16px;
  color: #4b5563;
  text-decoration: none;
  border-radius: 6px;
  transition: all 0.3s ease;
  font-size: 15px;
}

.nav-link:hover {
  background-color: #fff;
  color: #dc2626;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.nav-link.router-link-active {
  background-color: #dc2626;
  color: white;
  font-weight: 500;
}

.legalidad-content {
  flex: 1;
  min-width: 0;
}

@media (max-width: 768px) {
  .legalidad-container {
    flex-direction: column;
    padding: 10px;
  }

  .legalidad-nav {
    position: static;
    width: 100%;
  }

  .nav-links {
    flex-direction: row;
    flex-wrap: wrap;
  }

  .nav-link {
    padding: 10px 14px;
    font-size: 14px;
  }
}
</style>
