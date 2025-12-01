<template>
  <div class="tienda-container">
    <div class="tienda-wrapper">
      <h1 class="tienda-title">TIENDA</h1>

      <!-- Categorías -->
      <div class="categories-grid">
        <!-- JUEGOS DE MESA -->
        <div class="category-card">
          <h2 class="category-title">JUEGOS DE MESA</h2>
          <div class="subcategories">
            <router-link to="/catalogo?category=Juegos de mesa&sub=Estrategia" class="subcategory-link">
              Estrategia
            </router-link>
            <router-link to="/catalogo?category=Juegos de mesa&sub=Familiar" class="subcategory-link">
              Familiar
            </router-link>
            <router-link to="/catalogo?category=Juegos de mesa&sub=Party" class="subcategory-link">
              Party Games
            </router-link>
            <router-link to="/catalogo?category=Juegos de mesa&sub=Cooperativos" class="subcategory-link">
              Cooperativos
            </router-link>
            <router-link to="/catalogo?category=Juegos de mesa&sub=Rol" class="subcategory-link">
              Juegos de Rol
            </router-link>
          </div>
        </div>

        <!-- TCG -->
        <div class="category-card">
          <h2 class="category-title">TCG</h2>
          <div class="subcategories">
            <router-link to="/catalogo?category=TCG&sub=Yu-Gi-Oh" class="subcategory-link">
              Yu-Gi-Oh!
            </router-link>
            <router-link to="/catalogo?category=TCG&sub=Magic" class="subcategory-link">
              Magic: The Gathering
            </router-link>
            <router-link to="/catalogo?category=TCG&sub=Pokemon" class="subcategory-link">
              Pokémon TCG
            </router-link>
            <router-link to="/catalogo?category=TCG&sub=One Piece" class="subcategory-link">
              One Piece Card Game
            </router-link>
            <router-link to="/catalogo?category=TCG&sub=Digimon" class="subcategory-link">
              Digimon
            </router-link>
            <router-link to="/catalogo?category=TCG&sub=Dragon Ball" class="subcategory-link">
              Dragon Ball Super
            </router-link>
          </div>
        </div>

        <!-- COMICS -->
        <div class="category-card">
          <h2 class="category-title">COMICS</h2>
          <div class="subcategories">
            <router-link to="/catalogo?category=Comics&sub=Marvel" class="subcategory-link">
              Marvel
            </router-link>
            <router-link to="/catalogo?category=Comics&sub=DC" class="subcategory-link">
              DC Comics
            </router-link>
            <router-link to="/catalogo?category=Comics&sub=Image" class="subcategory-link">
              Image Comics
            </router-link>
            <router-link to="/catalogo?category=Comics&sub=Dark Horse" class="subcategory-link">
              Dark Horse
            </router-link>
            <router-link to="/catalogo?category=Comics&sub=Independientes" class="subcategory-link">
              Independientes
            </router-link>
          </div>
        </div>

        <!-- MANGA -->
        <div class="category-card">
          <h2 class="category-title">MANGA</h2>
          <div class="subcategories">
            <router-link to="/catalogo?category=Manga&sub=Shonen" class="subcategory-link">
              Shōnen
            </router-link>
            <router-link to="/catalogo?category=Manga&sub=Shojo" class="subcategory-link">
              Shōjo
            </router-link>
            <router-link to="/catalogo?category=Manga&sub=Seinen" class="subcategory-link">
              Seinen
            </router-link>
            <router-link to="/catalogo?category=Manga&sub=Josei" class="subcategory-link">
              Josei
            </router-link>
            <router-link to="/catalogo?category=Manga&sub=Kodomo" class="subcategory-link">
              Kodomo
            </router-link>
          </div>
        </div>

        <!-- FIGURAS -->
        <div class="category-card">
          <h2 class="category-title">FIGURAS</h2>
          <div class="subcategories">
            <router-link to="/catalogo?category=Figuras&sub=Nendoroid" class="subcategory-link">
              Nendoroid
            </router-link>
            <router-link to="/catalogo?category=Figuras&sub=Funko Pop" class="subcategory-link">
              Funko Pop
            </router-link>
            <router-link to="/catalogo?category=Figuras&sub=Scale Figures" class="subcategory-link">
              Scale Figures
            </router-link>
            <router-link to="/catalogo?category=Figuras&sub=Estatuas" class="subcategory-link">
              Estatuas
            </router-link>
            <router-link to="/catalogo?category=Figuras&sub=Bustos" class="subcategory-link">
              Bustos
            </router-link>
          </div>
        </div>

        <!-- ACCESORIOS -->
        <div class="category-card">
          <h2 class="category-title">ACCESORIOS</h2>
          <div class="subcategories">
            <router-link to="/catalogo?category=Accesorios&sub=Fundas" class="subcategory-link">
              Fundas & Sleeves
            </router-link>
            <router-link to="/catalogo?category=Accesorios&sub=Deck Box" class="subcategory-link">
              Deck Boxes
            </router-link>
            <router-link to="/catalogo?category=Accesorios&sub=Playmat" class="subcategory-link">
              Playmats
            </router-link>
            <router-link to="/catalogo?category=Accesorios&sub=Dados" class="subcategory-link">
              Dados
            </router-link>
            <router-link to="/catalogo?category=Accesorios&sub=Carpetas" class="subcategory-link">
              Carpetas & Binders
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'

onMounted(() => {
  document.title = 'Tienda - Rasengan Comics'
})
</script>

<style scoped>
.tienda-container {
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
  min-height: 100vh;
  padding: 40px 20px;
}

.tienda-wrapper {
  max-width: 1400px;
  margin: 0 auto;
}

.tienda-title {
  font-size: 48px;
  font-weight: bold;
  color: #1f2937;
  text-align: center;
  margin-bottom: 50px;
  letter-spacing: 4px;
  position: relative;
}

.tienda-title::after {
  content: '';
  position: absolute;
  bottom: -10px;
  left: 50%;
  transform: translateX(-50%);
  width: 100px;
  height: 4px;
  background-color: #dc2626;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
}

.category-card {
  background-color: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.category-card:hover {
  border-color: #dc2626;
  box-shadow: 0 8px 24px rgba(220, 38, 38, 0.2);
  transform: translateY(-5px);
}

.category-title {
  font-size: 24px;
  font-weight: bold;
  color: #dc2626;
  margin: 0 0 20px 0;
  padding-bottom: 15px;
  border-bottom: 2px solid #fecaca;
  letter-spacing: 2px;
}

.subcategories {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.subcategory-link {
  padding: 12px 16px;
  background-color: #f9fafb;
  color: #374151;
  text-decoration: none;
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
}

.subcategory-link:hover {
  background-color: #dc2626;
  color: white;
  border-left-color: #991b1b;
  transform: translateX(5px);
}

@media (max-width: 1024px) {
  .categories-grid {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
  }

  .tienda-title {
    font-size: 36px;
  }

  .category-card {
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .tienda-container {
    padding: 20px 10px;
  }

  .categories-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .tienda-title {
    font-size: 28px;
    margin-bottom: 30px;
  }

  .category-title {
    font-size: 20px;
  }

  .subcategory-link {
    padding: 10px 12px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .tienda-title {
    font-size: 24px;
    letter-spacing: 2px;
  }

  .category-card {
    padding: 15px;
  }

  .category-title {
    font-size: 18px;
  }
}
</style>