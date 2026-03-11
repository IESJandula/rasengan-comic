<template>
  <button
    v-if="isVisible"
    class="scroll-top-btn"
    aria-label="Subir arriba"
    @click="scrollToTop"
  >
    ↑
  </button>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'

const isVisible = ref(false)

const getScrollTop = () => {
  const doc = document.documentElement.scrollTop
  const body = document.body.scrollTop
  const win = window.scrollY

  return Math.max(doc, body, win)
}

const resetScrollableContainers = () => {
  const scrollables = document.querySelectorAll<HTMLElement>('*')

  scrollables.forEach((element) => {
    const style = window.getComputedStyle(element)
    const overflowY = style.overflowY
    const isScrollable = (overflowY === 'auto' || overflowY === 'scroll') && element.scrollTop > 0

    if (isScrollable) {
      element.scrollTo({ top: 0, behavior: 'smooth' })
    }
  })
}

const toggleVisibility = () => {
  isVisible.value = getScrollTop() > 300
}

const scrollToTop = () => {
  resetScrollableContainers()

  document.documentElement.scrollTo({
    top: 0,
    behavior: 'smooth'
  })

  document.body.scrollTo({
    top: 0,
    behavior: 'smooth'
  })

  window.scrollTo({
    top: 0,
    behavior: 'smooth'
  })
}

onMounted(() => {
  window.addEventListener('scroll', toggleVisibility, true)
  toggleVisibility()
})

onBeforeUnmount(() => {
  window.removeEventListener('scroll', toggleVisibility, true)
})
</script>

<style scoped>
.scroll-top-btn {
  position: fixed;
  right: 25px;
  bottom: 24px;
  width: 66px;
  height: 66px;
  border: none;
  border-radius: 9999px;
  cursor: pointer;
  font-size: 30px;
  font-weight: 700;
  color: #fff;
  background: #111827;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
  z-index: 999;
}

.scroll-top-btn:hover {
  background: #1f2937;
}
</style>
