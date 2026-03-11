import { createRouter, createWebHistory } from 'vue-router'
import type { RouteLocationNormalizedLoaded } from 'vue-router'
import { useSeo } from '@/composables/useSeo'
import inicioComponent from '@/components/homeComponent.vue'
import catalogoComponent from '@/components/catalogoComponent.vue'
import LoginComponent from '@/components/loginComponent.vue'
import RegisterComponent from '@/components/registerComponent.vue'
import productoComponent from '@/components/productoComponent.vue'
import eventosComponent from '@/components/eventosComponent.vue'
import perfilComponent from '@/components/perfilComponent.vue'
import carritoComponent from '@/components/carritoComponent.vue'
import adminComponent from '@/components/adminComponent.vue'
import tiendaComponent from '@/components/tiendaComponent.vue'
import legalidadComponent from '@/components/legalidad.vue'
import terminosYCondicionesComponent from '@/components/terminosYCondicionesComponent.vue'
import politicaPrivacidadComponent from '@/components/politicaPrivacidadComponent.vue'
import politicaCookiesComponent from '@/components/politicaCookiesComponent.vue'

const resetScrollableContainers = () => {
  const scrollables = document.querySelectorAll<HTMLElement>('*')

  scrollables.forEach((element) => {
    const style = window.getComputedStyle(element)
    const overflowY = style.overflowY
    const isScrollable = (overflowY === 'auto' || overflowY === 'scroll') && element.scrollTop > 0

    if (isScrollable) {
      element.scrollTop = 0
    }
  })

  document.documentElement.scrollTop = 0
  document.body.scrollTop = 0
  window.scrollTo(0, 0)
}

const routes = [
  {
    path: '/',
    name: 'Home',
    component: inicioComponent,
    meta: {
      title: 'Inicio',
      description: 'Bienvenido a Rasengan Comics, tu tienda especializada en manga, TCG (Yu-Gi-Oh!, Pokémon, Magic) y cómics americanos. Envíos a toda España.',
    },
  },
  {
    path: '/catalogo',
    name: 'Catalogo',
    component: catalogoComponent,
    props: (route: RouteLocationNormalizedLoaded) => ({ category: route.query.category }),
    meta: {
      title: 'Catálogo de Productos',
      description: 'Explora nuestro catálogo completo de manga, TCG, cómics americanos y merchandising. Filtra por categoría, precio y más.',
    },
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginComponent,
    meta: {
      title: 'Iniciar Sesión',
      description: 'Accede a tu cuenta de Rasengan Comics para gestionar tus pedidos, reservas y lista de deseos.',
    },
  },
  {
    path: '/registro',
    name: 'Register',
    component: RegisterComponent,
    meta: {
      title: 'Crear Cuenta',
      description: 'Regístrate en Rasengan Comics y disfruta de ventajas exclusivas: seguimiento de pedidos, descuentos y acceso anticipado a novedades.',
    },
  },
  {
    path: '/producto/:id',
    name: 'Producto',
    component: productoComponent,
    props: true,
    // SEO dinámico aplicado desde productoComponent.vue al cargar el producto
  },
  {
    path: '/eventos',
    name: 'Eventos',
    component: eventosComponent,
    meta: {
      title: 'Eventos y Torneos',
      description: 'Descubre los próximos eventos y torneos de TCG en Rasengan Comics. Compite, gana premios y diviértete con la comunidad.',
    },
  },
  {
    path: '/perfil',
    name: 'Perfil',
    component: perfilComponent,
    meta: {
      title: 'Mi Perfil',
      description: 'Gestiona tu cuenta, revisa tu historial de pedidos y actualiza tus datos personales.',
    },
  },
  {
    path: '/carrito',
    name: 'Carrito',
    component: carritoComponent,
    meta: {
      title: 'Carrito de Compra',
      description: 'Revisa los productos en tu carrito de compra y finaliza tu pedido.',
    },
  },
  {
    path: '/admin',
    name: 'Admin',
    component: adminComponent,
    meta: {
      title: 'Panel de Administración',
      description: 'Panel de administración de Rasengan Comics.',
      robots: 'noindex, nofollow',
    },
  },
  {
    path: '/tienda',
    name: 'Tienda',
    component: tiendaComponent,
    meta: {
      title: 'Nuestra Tienda',
      description: 'Visita la tienda física de Rasengan Comics. Encuentra toda la información sobre horarios, ubicación y contacto.',
    },
  },
  {
    path: '/reservas',
    name: 'Reservas',
    component: () => import('@/components/reservasComponent.vue'),
    meta: {
      requiresAuth: true,
      title: 'Mis Reservas',
      description: 'Gestiona tus reservas de productos en Rasengan Comics.',
    },
  },
  {
    path: '/mensaje',
    component: () => import('../components/conexionSB.vue'),
  },
  {
    path: '/legalidad',
    name: 'Legalidad',
    component: legalidadComponent,
    redirect: { name: 'Privacidad' },
    children: [
      {
        path: 'terminos',
        name: 'Terminos',
        component: terminosYCondicionesComponent,
        meta: {
          title: 'Términos y Condiciones',
          description: 'Lee los términos y condiciones de uso de Rasengan Comics antes de realizar una compra.',
        },
      },
      {
        path: 'privacidad',
        name: 'Privacidad',
        component: politicaPrivacidadComponent,
        meta: {
          title: 'Política de Privacidad',
          description: 'Información sobre cómo Rasengan Comics recoge, usa y protege tus datos personales.',
        },
      },
      {
        path: 'cookies',
        name: 'Cookies',
        component: politicaCookiesComponent,
        meta: {
          title: 'Política de Cookies',
          description: 'Información sobre el uso de cookies en Rasengan Comics.',
        },
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    }

    return { top: 0, left: 0 }
  }
})

router.beforeEach((to) => {
  // No aplicar SEO automático en la página de producto (lo gestiona el componente)
  if (to.name === 'Producto') return

  const meta = to.meta as { title?: string; description?: string; robots?: string } | undefined

  useSeo({
    title: meta?.title,
    description: meta?.description,
  })

  // noindex para páginas que no deben indexarse (admin, etc.)
  const robotsMeta = document.querySelector('meta[name="robots"]') as HTMLMetaElement | null
  if (robotsMeta) {
    robotsMeta.content = meta?.robots ?? 'index, follow'
  }
})

router.afterEach(() => {
  requestAnimationFrame(() => {
    resetScrollableContainers()
  })
})

export default router
