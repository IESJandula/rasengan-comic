import { createRouter, createWebHistory } from 'vue-router'
import type { RouteLocationNormalizedLoaded } from 'vue-router'
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
  },
  {
    path: '/catalogo',
    name: 'Catalogo',
    component: catalogoComponent,
    props: (route: RouteLocationNormalizedLoaded) => ({ category: route.query.category }),
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginComponent,
  },
  {
    path: '/registro',
    name: 'Register',
    component: RegisterComponent,
  },
  {
    path: '/producto/:id',
    name: 'Producto',
    component: productoComponent,
    props: true,
  },
  {
    path: '/eventos',
    name: 'Eventos',
    component: eventosComponent,
  },
  {
    path: '/perfil',
    name: 'Perfil',
    component: perfilComponent,
  },
  {
    path: '/carrito',
    name: 'Carrito',
    component: carritoComponent,
  },
  {
    path: '/admin',
    name: 'Admin',
    component: adminComponent,
  },
  {
    path: '/tienda',
    name: 'Tienda',
    component: tiendaComponent,
  },
  {
    path: '/reservas',
    name: 'Reservas',
    component: () => import('@/components/reservasComponent.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/mensaje',
    component: () => import('../components/conexionSB.vue')
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
        component: terminosYCondicionesComponent
      },
      {
        path: 'privacidad',
        name: 'Privacidad',
        component: politicaPrivacidadComponent
      },
      {
        path: 'cookies',
        name: 'Cookies',
        component: politicaCookiesComponent
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

router.afterEach(() => {
  requestAnimationFrame(() => {
    resetScrollableContainers()
  })
})

export default router
