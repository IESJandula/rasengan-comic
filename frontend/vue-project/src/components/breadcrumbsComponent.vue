<template>
  <nav v-if="breadcrumbs.length > 0" class="breadcrumbs" aria-label="Migas de pan">
    <ol class="breadcrumbs-list">
      <li v-for="(crumb, index) in breadcrumbs" :key="`${crumb.label}-${index}`" class="breadcrumbs-item">
        <router-link
          v-if="!crumb.isCurrent"
          :to="crumb.to"
          class="breadcrumbs-link"
        >
          {{ crumb.label }}
        </router-link>
        <span v-else class="breadcrumbs-current" aria-current="page">{{ crumb.label }}</span>
        <span v-if="index < breadcrumbs.length - 1" class="breadcrumbs-separator" aria-hidden="true">/</span>
      </li>
    </ol>
  </nav>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

type Crumb = {
  label: string
  to: string
  isCurrent: boolean
}

const route = useRoute()

const labelMap: Record<string, string> = {
  Home: 'Inicio',
  Catalogo: 'Catalogo',
  Login: 'Iniciar sesion',
  Register: 'Registro',
  Producto: 'Producto',
  Eventos: 'Eventos',
  Perfil: 'Perfil',
  Carrito: 'Carrito',
  Admin: 'Admin',
  Tienda: 'Tienda',
  Reservas: 'Reservas',
  Legalidad: 'Legalidad',
  Terminos: 'Terminos y condiciones',
  Privacidad: 'Politica de privacidad',
  Cookies: 'Politica de cookies',
}

const breadcrumbs = computed<Crumb[]>(() => {
  if (route.path === '/') {
    return []
  }

  const crumbs: Crumb[] = [{ label: 'Inicio', to: '/', isCurrent: false }]

  route.matched.forEach((matched, idx) => {
    const isLast = idx === route.matched.length - 1
    const name = matched.name ? String(matched.name) : ''

    if (!name || name === 'Home') {
      return
    }

    let label = labelMap[name] || name

    if (name === 'Producto' && route.params.id) {
      label = `Producto ${route.params.id}`
    }

    crumbs.push({
      label,
      to: route.path,
      isCurrent: isLast,
    })
  })

  const lastIndex = crumbs.length - 1
  if (lastIndex >= 0 && crumbs[lastIndex]) {
    crumbs[lastIndex].isCurrent = true
  }

  return crumbs
})
</script>

<style scoped>
.breadcrumbs {
  width: 100%;
  background: #f7f7f7;
  border-bottom: 1px solid #e6e6e6;
}

.breadcrumbs-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 10px 20px;
  list-style: none;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
}

.breadcrumbs-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 0.92rem;
}

.breadcrumbs-link {
  color: #b91c1c;
  text-decoration: none;
  font-weight: 600;
}

.breadcrumbs-link:hover {
  text-decoration: underline;
}

.breadcrumbs-current {
  color: #333;
  font-weight: 700;
}

.breadcrumbs-separator {
  color: #9ca3af;
}

@media (max-width: 768px) {
  .breadcrumbs-list {
    padding: 8px 14px;
    font-size: 0.86rem;
  }
}
</style>
