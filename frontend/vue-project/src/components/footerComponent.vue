<template>
  <footer class="footer">
    <div class="footer-container">
      <!-- Información de la tienda -->
      <div class="footer-section about">
        <h3 class="footer-title">Rasengan Comics</h3>
        <p class="footer-text">Tu tienda de confianza para comics, mangas, TCG y mucho más.</p>
        
        <!-- Redes Sociales integradas -->
        <div class="social-section">
          <h4 class="footer-subtitle">Síguenos</h4>
          <div class="social-links">
            <a href="https://www.instagram.com/rasengan_comics?igsh=MTNmNDE5eG1xejdoZw==" 
               class="social-link" 
               aria-label="Instagram"
               target="_blank"
               rel="noopener noreferrer">
              <img src="@/assets/Instagram_icono.webp" alt="Instagram" class="social-icon">
            </a>
          </div>
        </div>
      </div>

      <!-- Información de contacto -->
      <div class="footer-section contact">
        <h4 class="footer-subtitle">Contacto</h4>
        <ul class="footer-list">
          <li>
            <span class="icon">📞</span>
            <a href="tel:+34123456789" class="footer-link">+34 123 456 789</a>
          </li>
          <li>
            <span class="icon">📧</span>
            <a href="mailto:info@rasengacomics.com" class="footer-link">info@rasengacomics.com</a>
          </li>
          <li>
            <span class="icon">📍</span>
            <span class="footer-text-inline">Calle Margarita Córcoles<br>Andújar, Jaén, España</span>
          </li>
        </ul>
      </div>

      <!-- Horario -->
      <div class="footer-section schedule">
        <h4 class="footer-subtitle">Horario</h4>
        <ul class="footer-list schedule-list">
          <li>
            <span class="day">Lunes - Viernes</span>
            <span class="time">17:30 - 21:00</span>
          </li>
          <li>
            <span class="day">Sábados</span>
            <span class="time">9:30 - 14:00</span>
          </li>
          <li>
            <span class="day">Domingos</span>
            <span class="time closed">Cerrado</span>
          </li>
        </ul>
      </div>
    </div>

    <!-- Línea divisoria -->
    <div class="footer-divider"></div>

    <!-- Pie de página inferior -->
    <div class="footer-bottom">
      <p class="footer-copyright">&copy; 2025 Rasengan Comics. Todos los derechos reservados.</p>
      <div class="footer-policies">
        <a href="#" class="footer-policy">Política de Privacidad</a>
        <span class="policy-separator">|</span>
        <a href="#" class="footer-policy">Términos y Condiciones</a>
        <span class="policy-separator">|</span>
        <a href="#" class="footer-policy">Cookies</a>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
</script>

<style scoped>
.footer {
  background-color: #1f2937;
  color: #e5e7eb;
  font-family: Arial, sans-serif;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 50px 20px;
  display: grid;
  grid-template-columns: 2fr 1.5fr 1.5fr;
  gap: 40px;
}

.footer-section {
  display: flex;
  flex-direction: column;
}

/* Sección Acerca de */
.footer-section.about {
  padding-right: 20px;
}

.footer-title {
  font-size: 26px;
  font-weight: bold;
  color: #dc2626;
  margin: 0 0 12px 0;
}

.footer-subtitle {
  font-size: 18px;
  font-weight: 600;
  color: #ffffff;
  margin: 0 0 16px 0;
  letter-spacing: 0.5px;
}

.footer-text {
  color: #d1d5db;
  margin: 0 0 20px 0;
  line-height: 1.7;
  font-size: 15px;
}

/* Redes sociales */
.social-section {
  margin-top: 24px;
}

.social-links {
  display: flex;
  gap: 12px;
}

.social-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 44px;
  height: 44px;
  color: white;
  text-decoration: none;
  border-radius: 50%;
  transition: all 0.3s ease;
  font-size: 18px;
}

.social-link:hover {
  background-color: #b91c1c;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.4);
}

.social-icon {
  width: 44px;
  height: 44px;
  object-fit: contain;
}

/* Listas */
.footer-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-list li {
  margin-bottom: 14px;
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.icon {
  font-size: 16px;
  flex-shrink: 0;
  margin-top: 2px;
}

.footer-link {
  color: #e5e5e5;
  text-decoration: none;
  transition: color 0.3s ease;
  font-size: 15px;
}

.footer-link:hover {
  color: #dc2626;
}

.footer-text-inline {
  color: #e5e5e5;
  font-size: 15px;
  line-height: 1.6;
}

/* Horario */
.schedule-list li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #374151;
}

.schedule-list li:last-child {
  border-bottom: none;
}

.day {
  color: #e5e5e5;
  font-weight: 500;
  font-size: 15px;
}

.time {
  color: #9ca3af;
  font-size: 14px;
}

.time.closed {
  color: #ef4444;
  font-style: italic;
}

/* Divisor */
.footer-divider {
  height: 1px;
  background: linear-gradient(to right, transparent, #374151, transparent);
  margin: 0 40px;
}

/* Footer inferior */
.footer-bottom {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
}

.footer-copyright {
  color: #9ca3af;
  margin: 0;
  font-size: 14px;
}

.footer-policies {
  display: flex;
  gap: 12px;
  align-items: center;
  flex-wrap: wrap;
}

.footer-policy {
  color: #9ca3af;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s ease;
  white-space: nowrap;
}

.footer-policy:hover {
  color: #dc2626;
}

.policy-separator {
  color: #4b5563;
}

/* Responsive */
@media (max-width: 992px) {
  .footer-container {
    grid-template-columns: 1fr 1fr;
    gap: 30px;
  }
  
  .footer-section.about {
    grid-column: 1 / -1;
    padding-right: 0;
  }
}

@media (max-width: 640px) {
  .footer-container {
    grid-template-columns: 1fr;
    gap: 30px;
    padding: 40px 20px;
  }

  .footer-section.about {
    grid-column: auto;
  }

  .footer-bottom {
    flex-direction: column;
    text-align: center;
    padding: 20px;
  }

  .footer-policies {
    justify-content: center;
  }

  .schedule-list li {
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
  }
}
</style>