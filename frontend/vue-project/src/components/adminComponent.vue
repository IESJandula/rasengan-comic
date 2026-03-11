<template>
  <!-- Protección: Solo admin puede ver esto -->
  <div v-if="isAdmin" class="admin-container">
    <!-- Sidebar -->
    <aside class="admin-sidebar">
      <h2 class="admin-title">Admin Panel</h2>
      <nav class="admin-nav">
        <button
          v-for="tab in tabs"
          :key="tab"
          @click="activeTab = tab"
          :class="['nav-btn', { active: activeTab === tab }]"
        >
          {{ tab }}
        </button>
      </nav>
    </aside>

    <!-- Main Content -->
    <main class="admin-main">
      <!-- Reservas -->
      <div v-if="activeTab === 'Reservas'" class="admin-section">
        <h1>Gestión de Reservas de Clientes</h1>
        
        <!-- Filtros de reservas -->
        <div class="reservas-filters">
          <button 
            v-for="filter in reservasFilters" 
            :key="filter.value"
            @click="activeReservaFilter = filter.value"
            :class="['filter-btn', { active: activeReservaFilter === filter.value }]"
          >
            {{ filter.label }} ({{ getReservaCountByStatus(filter.value) }})
          </button>
        </div>

        <!-- Lista de Reservas -->
        <div class="reservas-admin-list">
          <div 
            v-for="reserva in filteredReservasAdmin" 
            :key="reserva.id"
            class="reserva-admin-card"
          >
            <div class="reserva-admin-header">
              <div class="reserva-info-left">
                <span class="reserva-id">#{{ reserva.id }}</span>
                <span :class="['status-badge', reserva.estado]">
                  {{ getStatusLabel(reserva.estado) }}
                </span>
              </div>
              <div class="reserva-info-right">
                <span class="reserva-date">{{ formatDate(reserva.fechaReserva) }}</span>
              </div>
            </div>

            <div class="reserva-admin-body">
              <div class="cliente-info">
                <div class="cliente-details">
                  <h4>{{ reserva.cliente.nombre }}</h4>
                  <p>{{ reserva.cliente.email }}</p>
                  <p>Tel: {{ reserva.cliente.telefono }}</p>
                </div>
              </div>

              <div class="producto-reservado">
                <div class="producto-info">
                  <h4>{{ reserva.producto.nombre }}</h4>
                  <p class="categoria">{{ reserva.producto.categoria }}</p>
                  <p class="cantidad">Cantidad: {{ reserva.cantidad }} unidades</p>
                  <p class="precio">{{ formatPrice(reserva.precioUnitario) }} x {{ reserva.cantidad }} = <strong>{{ formatPrice(reserva.total) }}</strong></p>
                </div>
              </div>

              <div v-if="reserva.fechaDisponibilidad" class="disponibilidad-info">
                <strong>📅 Disponible desde:</strong> {{ formatDate(reserva.fechaDisponibilidad) }}
              </div>

              <div v-if="reserva.notas" class="notas-admin">
                <strong>📝 Notas del cliente:</strong> {{ reserva.notas }}
              </div>
            </div>

            <div class="reserva-admin-actions">
              <button 
                v-if="reserva.estado === 'pendiente'" 
                @click="marcarDisponible(reserva)" 
                class="btn-success"
              >
                ✓ Marcar Disponible
              </button>
              <button 
                v-if="reserva.estado === 'disponible'" 
                @click="marcarEntregado(reserva)" 
                class="btn-primary"
              >
                📦 Marcar como Recogido
              </button>
              <button 
                @click="contactarCliente(reserva)" 
                class="btn-contact"
              >
                📧 Contactar Cliente
              </button>
              <button 
                @click="editarReserva(reserva)" 
                class="btn-edit"
              >
                ✏️ Editar
              </button>
              <button 
                v-if="reserva.estado !== 'recogido' && reserva.estado !== 'cancelada'" 
                @click="cancelarReservaAdmin(reserva)" 
                class="btn-cancel"
              >
                ✗ Cancelar
              </button>
            </div>
          </div>

          <div v-if="filteredReservasAdmin.length === 0" class="empty-reservas">
            <p>No hay reservas {{ activeReservaFilter !== 'todas' ? activeReservaFilter : '' }}</p>
          </div>
        </div>
      </div>

      <!-- Productos -->
      <div v-if="activeTab === 'Productos'" class="admin-section">
        <h1>Gestión de Productos</h1>
        
        <button @click="openProductForm()" class="add-btn">Añadir Producto</button>

        <!-- Formulario Producto -->
        <div v-if="showProductForm" class="form-modal">
          <div class="form-content">
            <h2>{{ editingProduct ? 'Editar' : 'Nuevo' }} Producto</h2>
            <form @submit.prevent="saveProduct">
              <div class="form-group">
                <label for="productName">📝 Nombre del Producto</label>
                <input id="productName" v-model="productForm.name" placeholder="Ej: One Piece Vol. 100" required />
              </div>
              <div class="form-group">
                <label for="productCategory">📂 Categoría</label>
                <select id="productCategory" v-model="productForm.category" required>
                  <option value="">Selecciona una categoría</option>
                  <option v-for="cat in productCategories" :key="cat" :value="cat">{{ cat }}</option>
                  <option
                    v-if="productForm.category && !productCategories.includes(productForm.category)"
                    :value="productForm.category"
                  >
                    {{ productForm.category }}
                  </option>
                </select>
              </div>
              <div class="form-group">
                <label for="productSubcategory">🏷️ Subcategoría (opcional)</label>
                <select
                  id="productSubcategory"
                  v-model="productForm.subcategory"
                  :disabled="!productForm.category"
                >
                  <option value="">Sin subcategoría</option>
                  <option v-for="sub in availableProductSubcategories" :key="sub" :value="sub">{{ sub }}</option>
                  <option
                    v-if="productForm.subcategory && !availableProductSubcategories.includes(productForm.subcategory)"
                    :value="productForm.subcategory"
                  >
                    {{ productForm.subcategory }}
                  </option>
                </select>
              </div>
              <div class="form-group">
                <label for="productPrice">💰 Precio</label>
                <input id="productPrice" v-model.number="productForm.price" type="number" placeholder="0.00" step="0.01" required />
              </div>
              <div class="form-group">
                <label for="productOriginalPrice">🏷️ Precio Original (opcional)</label>
                <input id="productOriginalPrice" v-model.number="productForm.originalPrice" type="number" placeholder="0.00" step="0.01" />
              </div>
              <div class="form-group">
                <label for="productDiscount">📉 Descuento (%)</label>
                <input id="productDiscount" v-model.number="productForm.discount" type="number" placeholder="0" min="0" max="100" />
              </div>
              <div class="form-group">
                <label for="productStock">📦 Stock Disponible</label>
                <input id="productStock" v-model.number="productForm.stock" type="number" placeholder="0" min="0" required />
              </div>
              
              <div class="checkbox-group">
                <label><input type="checkbox" v-model="productForm.available" /> ✅ Disponible</label>
                <label><input type="checkbox" v-model="productForm.isNew" /> 🆕 Producto Nuevo</label>
                <label><input type="checkbox" v-model="productForm.isReserve" /> 📅 Es Reserva</label>
              </div>
              
              <!-- Upload imagen -->
              <div class="image-upload-section">
                <label>📸 Imagen del Producto</label>
                <input 
                  type="file" 
                  @change="handleImageUpload" 
                  accept="image/*"
                  :disabled="uploadingImage"
                />
                <p v-if="uploadingImage" class="uploading-text">⏳ Subiendo imagen...</p>
                <div v-else-if="imagePreview" class="image-preview">
                  <div class="preview-container">
                    <img :src="imagePreview" :alt="productForm.name || 'Preview'" />
                  </div>
                </div>
                <div v-else class="no-image-placeholder">
                  <p>❌ Sin imagen seleccionada</p>
                </div>
              </div>
              
              <div class="form-buttons">
                <button type="submit" class="save-btn" :disabled="uploadingImage">Guardar</button>
                <button type="button" @click="closeProductForm()" class="cancel-btn">Cancelar</button>
              </div>
            </form>
          </div>
        </div>

        <!-- Lista de Productos -->
        <div class="items-list">
          <div v-for="product in products" :key="product.id" class="item-card">
            <div class="item-details">
              <h3>{{ product.name }}</h3>
              <p> ${{ product.price }}</p>
              <p :class="['stock-indicator', product.stock === 0 ? 'out-of-stock' : product.stock < 10 ? 'low-stock' : 'in-stock']">
                 Stock: {{ product.stock }}
              </p>
            </div>
            <div class="item-actions">
              <button @click="editProductHandler(product)" class="edit-btn">Editar</button>
              <button @click="deleteProductHandler(product.id)" class="delete-btn">Borrar</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Eventos -->
      <div v-if="activeTab === 'Eventos'" class="admin-section">
        <h1>Gestión de Eventos</h1>
        
        <button @click="openEventForm()" class="add-btn">Crear Evento</button>

        <!-- Formulario Evento -->
        <div v-if="showEventForm" class="form-modal">
          <div class="form-content">
            <h2>{{ editingEvent ? 'Editar' : 'Nuevo' }} Evento</h2>
            <form @submit.prevent="saveEvent">
              <div class="form-group">
                <label for="eventName">🎯 Nombre del Evento</label>
                <input id="eventName" v-model="eventForm.name" placeholder="Ej: Torneo de Yu-Gi-Oh" required />
              </div>
              <div class="form-group">
                <label for="eventDate">📅 Fecha</label>
                <input id="eventDate" v-model="eventForm.date" type="date" required />
              </div>
              <div class="form-group">
                <label for="eventTime">⏰ Hora</label>
                <input id="eventTime" v-model="eventForm.time" type="time" required />
              </div>
              <div class="form-group">
                <label for="eventDescription">📝 Descripción</label>
                <textarea id="eventDescription" v-model="eventForm.description" placeholder="Describe el evento con detalle..."></textarea>
              </div>
              <div class="form-group">
                <label for="eventType">🎪 Tipo de Evento</label>
                <select id="eventType" v-model="eventForm.type">
                  <option value="tournament">🏆 Torneo</option>
                  <option value="workshop">📚 Taller</option>
                  <option value="special">✨ Especial</option>
                </select>
              </div>
              <div class="form-buttons">
                <button type="submit" class="save-btn">Guardar</button>
                <button type="button" @click="closeEventForm()" class="cancel-btn">Cancelar</button>
              </div>
            </form>
          </div>
        </div>

        <!-- Lista de Eventos -->
        <div class="items-list">
          <div v-for="event in events" :key="event.id" class="item-card">
            <div class="item-details">
              <h3>{{ event.name }}</h3>
              <p>{{ event.date }} - {{ event.time }}</p>
              <p>{{ event.description }}</p>
            </div>
            <div class="item-actions">
              <button @click="editEventHandler(event)" class="edit-btn">Editar</button>
              <button @click="deleteEventHandler(event.id)" class="delete-btn">Borrar</button>
            </div>
          </div>
        </div>
      </div>

      <!-- Descuentos -->
      <div v-if="activeTab === 'Descuentos'" class="admin-section">
        <div class="discounts-section-admin">
          <div class="section-header">
            <h1>🎟️ Gestión de Códigos de Descuento</h1>
            <button @click="openDiscountModal()" class="create-discount-btn">
              <span class="btn-icon">+</span>
              Crear Código de Descuento
            </button>
          </div>

          <div v-if="discountsLoading" class="discounts-loading">
            <div class="loading-spinner"></div>
            <p>Cargando códigos de descuento...</p>
          </div>

          <div v-else-if="discounts.length === 0" class="discounts-empty">
            <div class="empty-icon">🎟️</div>
            <h4>No hay códigos de descuento</h4>
            <p>Crea tu primer código de descuento para empezar</p>
          </div>

          <div v-else class="discounts-list-admin">
            <div v-for="discount in discounts" :key="discount.id" class="discount-card-admin">
              <div class="discount-header-admin">
                <div class="discount-code-badge-admin">🎟️ {{ discount.code }}</div>
                <div class="discount-status-admin">
                  <span :class="['status-badge-admin', discount.activo ? 'active' : 'inactive']">
                    {{ discount.activo ? '✓ Activo' : '✗ Inactivo' }}
                  </span>
                </div>
                <div class="discount-actions-admin">
                  <button @click="editDiscount(discount)" class="action-btn-admin discount-edit-btn" title="Editar">
                    Editar
                  </button>
                  <button @click="deleteDiscount(discount.id)" class="action-btn-admin discount-delete-btn" title="Eliminar">
                    Borrar
                  </button>
                </div>
              </div>
              <div class="discount-info-admin">
                <div class="discount-item-admin">
                  <span class="label">💰 Tipo:</span>
                  <span class="value">{{ discount.type === 'percentage' ? 'Porcentaje' : 'Cantidad Fija' }}</span>
                </div>
                <div class="discount-item-admin">
                  <span class="label">💵 Valor:</span>
                  <span class="value discount-value-highlight">{{ discount.type === 'percentage' ? discount.value + '%' : discount.value + '€' }}</span>
                </div>
                <div class="discount-item-admin">
                  <span class="label">🎯 Alcance:</span>
                  <span class="value">{{ getScopeText(discount) }}</span>
                </div>
                <div v-if="discount.scopeValue" class="discount-item-admin">
                  <span class="label">📌 Aplica a:</span>
                  <span class="value">{{ discount.scopeValue }}</span>
                </div>
                <div class="discount-item-admin full-width">
                  <span class="label">📅 Vigencia:</span>
                  <span class="value">{{ formatDate(discount.startDate) }} - {{ formatDate(discount.endDate) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Modal Gestión de Descuentos -->
        <div v-if="showDiscountModal" class="modal-overlay-admin" @click="showDiscountModal = false">
          <div class="modal-content-admin discount-modal-admin" @click.stop>
            <div class="modal-header-admin">
              <h2>
                <span class="modal-icon">🎟️</span>
                {{ discountForm.id ? 'Editar Código de Descuento' : 'Crear Código de Descuento' }}
              </h2>
              <button @click="closeDiscountModal" class="modal-close-admin">✕</button>
            </div>
            <div class="modal-body-admin">
              <!-- Código -->
              <div class="form-group-admin">
                <label class="required">🎟️ Código de Descuento</label>
                <input 
                  v-model="discountForm.code" 
                  type="text" 
                  placeholder="Ej: VERANO2024" 
                  class="uppercase-input"
                  maxlength="20"
                />
                <small class="form-hint">El código debe ser único y sin espacios</small>
              </div>

              <!-- Tipo y Valor -->
              <div class="form-row-admin">
                <div class="form-group-admin half">
                  <label class="required">💰 Tipo de Descuento</label>
                  <select v-model="discountForm.type" class="select-input-admin">
                    <option value="percentage">Porcentaje (%)</option>
                    <option value="fixed">Cantidad Fija (€)</option>
                  </select>
                </div>
                <div class="form-group-admin half">
                  <label class="required">💵 Valor del Descuento</label>
                  <div class="input-with-suffix-admin">
                    <input 
                      v-model.number="discountForm.value" 
                      type="number" 
                      min="0" 
                      :max="discountForm.type === 'percentage' ? 100 : 9999"
                      step="0.01"
                    />
                    <span class="input-suffix-admin">{{ discountForm.type === 'percentage' ? '%' : '€' }}</span>
                  </div>
                </div>
              </div>

              <!-- Fechas -->
              <div class="form-row-admin">
                <div class="form-group-admin half">
                  <label class="required">📅 Fecha de Inicio</label>
                  <input 
                    v-model="discountForm.startDate" 
                    type="date" 
                    class="date-input-admin"
                  />
                </div>
                <div class="form-group-admin half">
                  <label class="required">📅 Fecha de Fin</label>
                  <input 
                    v-model="discountForm.endDate" 
                    type="date" 
                    class="date-input-admin"
                  />
                </div>
              </div>

              <!-- Alcance -->
              <div class="form-group-admin">
                <label class="required">🎯 Alcance del Descuento</label>
                <select v-model="discountForm.scope" class="select-input-admin">
                  <option value="global">🌍 Global (Todos los productos)</option>
                  <option value="category">📦 Por Categoría</option>
                  <option value="subcategory">📂 Por Subcategoría</option>
                  <option value="product">🏷️ Producto Específico</option>
                </select>
              </div>

              <!-- Selección según alcance -->
              <div v-if="discountForm.scope === 'category'" class="form-group-admin">
                <label class="required">Seleccionar Categoría</label>
                <select v-model="discountForm.scopeValue" class="select-input-admin">
                  <option value="">-- Selecciona una categoría --</option>
                  <option v-for="cat in categories" :key="cat" :value="cat">{{ cat }}</option>
                </select>
              </div>

              <div v-if="discountForm.scope === 'subcategory'" class="form-group-admin">
                <label class="required">Subcategoría</label>
                <input 
                  v-model="discountForm.scopeValue" 
                  type="text" 
                  placeholder="Ej: Yu-Gi-Oh, Shonen, etc."
                />
              </div>

              <div v-if="discountForm.scope === 'product'" class="form-group-admin">
                <label class="required">Producto</label>
                <input 
                  v-model="discountForm.scopeValue" 
                  type="text" 
                  placeholder="Buscar producto por nombre o ID"
                />
                <small class="form-hint">Puedes ingresar el ID del producto</small>
              </div>
            </div>
            <div class="modal-footer-admin">
              <button @click="closeDiscountModal" class="btn-cancel-admin">Cancelar</button>
              <button @click="saveDiscount" class="btn-save-admin">
                {{ discountForm.id ? 'Actualizar' : 'Crear' }} Código
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Envios -->
      <div v-if="activeTab === 'Envios'" class="admin-section">
        <h1>Gestion de Envios</h1>

        <div class="shipments-summary">
          <div class="shipment-kpi">
            <span class="kpi-label">Pendientes de gestion</span>
            <strong class="kpi-value">{{ enviosPendientesCount }}</strong>
          </div>
          <div class="shipment-kpi">
            <span class="kpi-label">A domicilio pendientes</span>
            <strong class="kpi-value">{{ enviosDomicilioPendientesCount }}</strong>
          </div>
          <div class="shipment-kpi">
            <span class="kpi-label">Recogida en tienda pendientes</span>
            <strong class="kpi-value">{{ recogidasTiendaPendientesCount }}</strong>
          </div>
          <div class="shipment-kpi">
            <span class="kpi-label">Pedidos listados</span>
            <strong class="kpi-value">{{ enviosAdmin.length }}</strong>
          </div>
        </div>

        <div v-if="enviosAdmin.length === 0" class="empty-reservas">
          <p>No hay pedidos pendientes de gestion en envios.</p>
        </div>

        <div v-else class="shipments-list">
          <section class="shipment-group">
            <h2>Envio a domicilio</h2>
            <div v-if="enviosDomicilioAdmin.length === 0" class="empty-reservas">
              <p>No hay pedidos pendientes para envio a domicilio.</p>
            </div>
            <article v-for="envio in enviosDomicilioAdmin" :key="`dom-${envio.id}`" class="shipment-card">
              <header class="shipment-header">
                <div>
                  <h3>Pedido #{{ envio.id }}</h3>
                  <p class="shipment-date">{{ formatDate(envio.fechaPedido) }}</p>
                </div>
                <span :class="['status-badge', envio.estadoBadge]">{{ envio.estado }}</span>
              </header>

              <div class="shipment-grid">
                <div>
                  <h4>Destinatario</h4>
                  <p>{{ envio.destinatario }}</p>
                  <p>{{ envio.email }}</p>
                  <p>{{ envio.telefono }}</p>
                </div>

                <div>
                  <h4>Direccion de envio</h4>
                  <p>{{ envio.direccion.calle }}</p>
                  <p>{{ envio.direccion.codigoPostal }} {{ envio.direccion.ciudad }}</p>
                  <p>{{ envio.direccion.pais }}</p>
                </div>

                <div>
                  <h4>Productos a enviar</h4>
                  <ul class="shipment-products">
                    <li v-for="producto in envio.productos" :key="`${envio.id}-${producto.nombre}`">
                      {{ producto.cantidad }} x {{ producto.nombre }}
                    </li>
                  </ul>
                  <p class="shipment-total">Total: {{ formatPrice(envio.total) }}</p>
                </div>
              </div>

              <footer class="shipment-actions">
                <button
                  v-if="!['ENVIADO', 'ENTREGADO', 'RECOGIDO', 'COMPLETADO'].includes(envio.estado.toUpperCase())"
                  @click="marcarPedidoEnviado(envio.id)"
                  class="btn-primary"
                >
                  Marcar como enviado
                </button>
              </footer>
            </article>
          </section>

          <section class="shipment-group">
            <h2>Recogida en tienda</h2>
            <div v-if="recogidasTiendaAdmin.length === 0" class="empty-reservas">
              <p>No hay pedidos pendientes para recogida en tienda.</p>
            </div>
            <article v-for="envio in recogidasTiendaAdmin" :key="`store-${envio.id}`" class="shipment-card">
              <header class="shipment-header">
                <div>
                  <h3>Pedido #{{ envio.id }}</h3>
                  <p class="shipment-date">{{ formatDate(envio.fechaPedido) }}</p>
                </div>
                <span :class="['status-badge', envio.estadoBadge]">{{ envio.estado }}</span>
              </header>

              <div class="shipment-grid">
                <div>
                  <h4>Cliente</h4>
                  <p>{{ envio.destinatario }}</p>
                  <p>{{ envio.email }}</p>
                  <p>{{ envio.telefono }}</p>
                </div>

                <div>
                  <h4>Punto de recogida</h4>
                  <p>Calle Ejemplo 123, Madrid</p>
                  <p>Lun-Vie: 10:00-20:00</p>
                  <p>Sab: 10:00-14:00</p>
                </div>

                <div>
                  <h4>Productos para recoger</h4>
                  <ul class="shipment-products">
                    <li v-for="producto in envio.productos" :key="`${envio.id}-${producto.nombre}`">
                      {{ producto.cantidad }} x {{ producto.nombre }}
                    </li>
                  </ul>
                  <p class="shipment-total">Total: {{ formatPrice(envio.total) }}</p>
                </div>
              </div>

              <footer class="shipment-actions">
                <button
                  v-if="!['CANCELADO', 'ENTREGADO', 'RECOGIDO', 'COMPLETADO'].includes(envio.estado.toUpperCase())"
                  @click="marcarPedidoRecogido(envio.id)"
                  class="btn-primary"
                >
                  Marcar como recogido
                </button>
              </footer>
            </article>
          </section>
        </div>
      </div>

      <!-- Carrusel Home -->
      <div v-if="activeTab === 'Carrusel Home'" class="admin-section">
        <h1>Gestion del Carrusel Home</h1>
        <p class="carousel-admin-subtitle">
          Sube o cambia las imagenes del carrusel principal de la home. Estos cambios se guardan en la base de datos y son visibles para todos los visitantes.
        </p>
        <div class="carousel-admin-help">
          <strong>Proporcion recomendada:</strong> 2.74:1
          <span>Ideal: 1920 x 700 px. Minimo recomendado: 1600 x 584 px.</span>
          <span>Manten el texto y los elementos importantes centrados para que se vean bien en desktop y movil.</span>
        </div>

        <div class="carousel-admin-grid">
          <article v-for="(slide, index) in carouselSlides" :key="`carousel-slide-${index}`" class="carousel-admin-card">
            <h3>Slide {{ index + 1 }}</h3>

            <div class="carousel-admin-preview-wrap">
              <img
                v-if="slide"
                :src="slide"
                :alt="`Slide ${index + 1}`"
                class="carousel-admin-preview"
              />
              <div v-else class="carousel-admin-empty">Sin imagen configurada</div>
            </div>

            <div class="carousel-admin-actions">
              <input
                type="file"
                accept="image/*"
                @change="handleCarouselImageUpload($event, index)"
                :disabled="carouselUploadingIndex === index"
              />
              <button
                type="button"
                class="btn-cancel"
                @click="clearCarouselSlide(index)"
                :disabled="carouselUploadingIndex === index"
              >
                Limpiar slide
              </button>
            </div>

            <p v-if="carouselUploadingIndex === index" class="uploading-text">Subiendo imagen...</p>
          </article>
        </div>

        <div class="carousel-admin-footer">
          <button class="save-btn" @click="saveCarouselConfig" :disabled="carouselUploadingIndex !== null">
            Guardar carrusel
          </button>
        </div>
      </div>

      <!-- Reportes -->
      <div v-if="activeTab === 'Reportes'" class="admin-section">
        <h1>📊 Generador de Reportes</h1>
        
        <div class="reports-container">
          <div class="report-card">
            <div class="report-header">
              <h3>📦 Exportar Productos</h3>
              <p>Descarga todos los productos en formato Excel</p>
            </div>
            <div class="report-options">
              <div class="option-group">
                <h4>📋 Seleccionar columnas a incluir:</h4>
                <div class="checkbox-list">
                  <label><input type="checkbox" v-model="productExportOptions.name" /> Nombre</label>
                  <label><input type="checkbox" v-model="productExportOptions.category" /> Categoría</label>
                  <label><input type="checkbox" v-model="productExportOptions.price" /> Precio</label>
                  <label><input type="checkbox" v-model="productExportOptions.stock" /> Stock</label>
                  <label><input type="checkbox" v-model="productExportOptions.discount" /> Descuento</label>
                  <label><input type="checkbox" v-model="productExportOptions.available" /> Disponible</label>
                </div>
              </div>
              <button @click="exportProductsToExcel" class="export-btn">
                📥 Descargar Excel
              </button>
            </div>
          </div>

          <div class="report-card">
            <div class="report-header">
              <h3>📊 Estadísticas Rápidas</h3>
              <p>Información general del inventario</p>
            </div>
            <div class="stats-info">
              <div class="stat-item">
                <span class="stat-label">Total de productos:</span>
                <span class="stat-value">{{ products.length }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">Productos en stock:</span>
                <span class="stat-value">{{ products.filter(p => p.stock > 0).length }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">Productos agotados:</span>
                <span class="stat-value">{{ products.filter(p => p.stock === 0).length }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-label">Valor total en stock:</span>
                <span class="stat-value">{{ formatPrice(totalStockValue) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>

  <!-- No autorizado -->
  <div v-else class="unauthorized">
    <h1>Acceso Denegado</h1>
    <p>Solo los administradores pueden acceder a este panel.</p>
    <router-link to="/" class="back-btn">Volver al inicio</router-link>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useAuthStore } from '@/stores/authStore'
import { useRouter } from 'vue-router'
import api from '@/api/axios'
import * as XLSX from 'xlsx'

interface Product {
  id: number
  name: string
  category: string
  subcategory?: string
  price: number
  originalPrice?: number | null
  discount?: number
  image: string
  available: boolean
  stock: number
  rating: number
  reviews: number
  isReserve: boolean
  isNew: boolean
}

interface Event {
  id: number
  name: string
  date: string
  time: string
  description: string
  type: 'tournament' | 'workshop' | 'special'
}

interface Discount {
  id: number
  code: string
  percentage: number
  description: string
  expiryDate: string
}

interface User {
  id: number
  name: string
  email: string
  avatar: string
  joinDate: string
}

interface Cliente {
  nombre: string
  email: string
  telefono: string
  avatar: string
}

interface ProductoReservado {
  nombre: string
  categoria: string
  imagen: string
}

interface ReservaAdmin {
  id: string
  pedidoId: number
  cliente: Cliente
  producto: ProductoReservado
  estado: 'pendiente' | 'disponible' | 'recogido' | 'cancelada'
  cantidad: number
  precioUnitario: number
  total: number
  fechaReserva: string
  fechaDisponibilidad?: string
  notas?: string
}

interface EnvioAdmin {
  id: number
  tipoEntrega: 'domicilio' | 'tienda'
  estado: string
  estadoBadge: 'pendiente' | 'disponible' | 'recogido' | 'cancelada'
  fechaPedido: string
  destinatario: string
  email: string
  telefono: string
  direccion: {
    calle: string
    ciudad: string
    codigoPostal: string
    pais: string
  }
  productos: Array<{
    nombre: string
    cantidad: number
  }>
  total: number
}

const authStore = useAuthStore()
const router = useRouter()

const resolveBackendImageUrl = (image?: string): string => {
  if (!image) return ''

  if (
    image.startsWith('http://') ||
    image.startsWith('https://') ||
    image.startsWith('data:') ||
    image.startsWith('blob:')
  ) {
    return image
  }

  const baseUrl = String(api.defaults.baseURL || '').replace(/\/+$/, '')

  if (image.startsWith('/')) {
    return `${baseUrl}${image}`
  }

  if (image.startsWith('uploads/')) {
    return `${baseUrl}/${image}`
  }

  return image
}

// Verificar si es admin
const isAdmin = computed(() => {
  return authStore.user?.email === 'admin@rasengacomics.com'
})

// Si no es admin, redirigir
if (!isAdmin.value) {
  router.push('/')
}

const activeTab = ref('Reservas')
const tabs = ['Reservas', 'Envios', 'Carrusel Home', 'Productos', 'Eventos', 'Descuentos', 'Reportes']

// Reservas Admin
const activeReservaFilter = ref('todas')
const reservasFilters = [
  { label: 'Todas', value: 'todas' },
  { label: 'Pendientes', value: 'pendiente' },
  { label: 'Disponibles', value: 'disponible' },
  { label: 'Recogidas', value: 'recogido' },
  { label: 'Canceladas', value: 'cancelada' }
]

const reservasAdmin = ref<ReservaAdmin[]>([])

const filteredReservasAdmin = computed(() => {
  if (activeReservaFilter.value === 'todas') {
    return reservasAdmin.value
  }
  return reservasAdmin.value.filter(r => r.estado === activeReservaFilter.value)
})

const reservasActivas = computed(() => {
  return reservasAdmin.value.filter(r => r.estado === 'pendiente' || r.estado === 'disponible').length
})

const enviosAdmin = ref<EnvioAdmin[]>([])
const carouselSlides = ref<string[]>(['', '', ''])
const carouselUploadingIndex = ref<number | null>(null)
const estadosFinalizadosEnvio = ['CANCELADO', 'ENTREGADO', 'RECOGIDO', 'COMPLETADO']

const enviosDomicilioAdmin = computed(() => {
  return enviosAdmin.value.filter((envio) => envio.tipoEntrega === 'domicilio')
})

const recogidasTiendaAdmin = computed(() => {
  return enviosAdmin.value.filter((envio) => envio.tipoEntrega === 'tienda')
})

const enviosPendientesCount = computed(() => {
  return enviosAdmin.value.filter((envio) => {
    const estado = envio.estado.toUpperCase()
    return !estadosFinalizadosEnvio.includes(estado)
  }).length
})

const enviosDomicilioPendientesCount = computed(() => {
  return enviosDomicilioAdmin.value.filter((envio) => {
    const estado = envio.estado.toUpperCase()
    return !estadosFinalizadosEnvio.includes(estado)
  }).length
})

const recogidasTiendaPendientesCount = computed(() => {
  return recogidasTiendaAdmin.value.filter((envio) => {
    const estado = envio.estado.toUpperCase()
    return !estadosFinalizadosEnvio.includes(estado)
  }).length
})

// Estadísticas desde la base de datos
const loadProductos = async () => {
  try {
    const response = await api.get('/api/products')
    products.value = response.data.map((p: any) => ({
      id: p.id,
      name: p.name,
      category: p.category,
      price: p.price || 0,
      stock: p.stock || 0,
      discount: p.discount || 0,
      description: p.description || '',
      image: p.image && p.image.trim()
        ? resolveBackendImageUrl(p.image)
        : 'https://via.placeholder.com/150?text=Sin+imagen'
    }))
    console.log('✅ Productos cargados:', products.value.length)
    console.log('📦 Primeros productos:', products.value.slice(0, 3))
  } catch (err) {
    console.error('❌ Error al cargar productos:', err)
    products.value = []
  }
}

const loadEventos = async () => {
  try {
    const response = await api.get('/eventos')
    events.value = response.data.map((e: any) => ({
      id: e.id,
      name: e.nombre,
      date: e.fecha ? new Date(e.fecha).toISOString().split('T')[0] : '',
      time: e.fecha ? new Date(e.fecha).toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' }) : '',
      description: e.descripcion || '',
      type: e.tipo || 'tournament'
    }))
    console.log('✅ Eventos cargados:', events.value.length)
  } catch (err) {
    console.error('❌ Error al cargar eventos:', err)
  }
}

const loadReservas = async () => {
  try {
    // Cargar todos los pedidos
    const response = await api.get('/pedidos')
    const pedidos = Array.isArray(response.data) ? response.data : []
    const reservasArray: ReservaAdmin[] = []

    const isItemReserva = (item: any, estadoPedido: string): boolean => {
      if (item?.reserva === true) {
        return true
      }

      const estadoNormalizado = (estadoPedido || '').toUpperCase()
      return (
        estadoNormalizado === 'PENDIENTE' ||
        estadoNormalizado === 'DISPONIBLE' ||
        estadoNormalizado === 'RECOGIDO' ||
        estadoNormalizado === 'CANCELADO'
      )
    }

    // Procesar cada pedido para extraer items de reserva
    pedidos.forEach((pedido: any) => {
      const items = Array.isArray(pedido?.items) ? pedido.items : []
      const estadoPedido = (pedido?.estado || 'PENDIENTE').toUpperCase()

      items
        .filter((item: any) => isItemReserva(item, estadoPedido))
        .forEach((item: any, index: number) => {
          // Determinar estado de la reserva basado en el estado del pedido
          let estadoReserva: 'pendiente' | 'disponible' | 'recogido' | 'cancelada' = 'pendiente'
          if (estadoPedido === 'CANCELADO') {
            estadoReserva = 'cancelada'
          } else if (estadoPedido === 'RECOGIDO' || estadoPedido === 'COMPLETADO' || estadoPedido === 'ENTREGADO') {
            estadoReserva = 'recogido'
          } else if (estadoPedido === 'DISPONIBLE' || estadoPedido === 'PAGADO') {
            // Si el pedido está pagado hace más de 30 días, marcar como disponible
            const fechaPedido = new Date(pedido.fechaPedido)
            const fechaActual = new Date()
            const diasTranscurridos = (fechaActual.getTime() - fechaPedido.getTime()) / (1000 * 60 * 60 * 24)
            estadoReserva = diasTranscurridos >= 30 ? 'disponible' : 'pendiente'
          }

          const fechaDisp = new Date(pedido.fechaPedido)
          fechaDisp.setDate(fechaDisp.getDate() + 30)

          reservasArray.push({
            id: `${pedido.id}-${item?.productoId || index}`,
            pedidoId: pedido.id,
            cliente: {
              nombre: pedido.usuarioNombre || pedido.usuarioUid || 'Cliente',
              email: pedido.usuarioEmail || pedido.usuarioUid || 'sin@email.com',
              telefono: pedido.usuarioTelefono || 'No informado',
              avatar: 'https://ui-avatars.com/api/?name=' + encodeURIComponent(pedido.usuarioNombre || pedido.usuarioUid || 'Cliente') + '&background=dc2626&color=fff'
            },
            producto: {
              nombre: item?.nombre || 'Producto en reserva',
              categoria: item?.categoria || 'Producto',
              imagen: item?.imagen || 'https://images.unsplash.com/photo-1612036782180-69db8e541e1f?w=200'
            },
            estado: estadoReserva,
            cantidad: item?.cantidad || 1,
            precioUnitario: item?.precio || 0,
            total: (item?.precio || 0) * (item?.cantidad || 1),
            fechaReserva: pedido.fechaPedido,
            fechaDisponibilidad: fechaDisp.toISOString(),
            notas: `Pedido #${pedido.id}`
          })
        })
    })

    reservasAdmin.value = reservasArray
    console.log('✅ Reservas de productos cargadas:', reservasAdmin.value.length)
  } catch (err) {
    console.error('❌ Error al cargar reservas:', err)
    reservasAdmin.value = []
  }
}

const loadEnvios = async (): Promise<void> => {
  try {
    const response = await api.get('/pedidos')
    const pedidos = Array.isArray(response.data) ? response.data : []

    enviosAdmin.value = pedidos
      .filter((pedido: any) => {
        const tieneItems = Array.isArray(pedido?.items) && pedido.items.length > 0
        const estado = (pedido?.estado || '').toUpperCase()
        const esFinalizado = estadosFinalizadosEnvio.includes(estado)
        return tieneItems && !esFinalizado
      })
      .map((pedido: any) => {
        const estadoUpper = String(pedido?.estado || 'PENDIENTE').toUpperCase()
        const metodoEntregaRaw = String(pedido?.metodoEntrega || '').trim().toLowerCase()
        const tieneDireccion = Boolean(
          pedido?.usuarioCalle && pedido?.usuarioCiudad && pedido?.usuarioCodigoPostal && pedido?.usuarioPais
        )
        const tipoEntrega: EnvioAdmin['tipoEntrega'] = metodoEntregaRaw === 'tienda'
          ? 'tienda'
          : metodoEntregaRaw === 'envio'
            ? 'domicilio'
            : (tieneDireccion ? 'domicilio' : 'tienda')

        let estadoBadge: EnvioAdmin['estadoBadge'] = 'pendiente'
        if (['ENVIADO', 'DISPONIBLE'].includes(estadoUpper)) {
          estadoBadge = 'disponible'
        } else if (['ENTREGADO', 'RECOGIDO', 'COMPLETADO'].includes(estadoUpper)) {
          estadoBadge = 'recogido'
        } else if (estadoUpper === 'CANCELADO') {
          estadoBadge = 'cancelada'
        }

        return {
          id: pedido.id,
          tipoEntrega,
          estado: pedido.estado || 'PENDIENTE',
          estadoBadge,
          fechaPedido: pedido.fechaPedido,
          destinatario: pedido.usuarioNombre || pedido.usuarioUid || 'Cliente',
          email: pedido.usuarioEmail || 'Sin email',
          telefono: pedido.usuarioTelefono || 'No informado',
          direccion: {
            calle: pedido.usuarioCalle || 'No aplica (recogida en tienda)',
            ciudad: pedido.usuarioCiudad || '-',
            codigoPostal: pedido.usuarioCodigoPostal || '-',
            pais: pedido.usuarioPais || '-',
          },
          productos: (pedido.items || []).map((item: any) => ({
            nombre: item?.nombre || 'Producto',
            cantidad: item?.cantidad || 1,
          })),
          total: Number(pedido.total || 0),
        } as EnvioAdmin
      })
  } catch (error) {
    console.error('Error al cargar envios:', error)
    enviosAdmin.value = []
  }
}

onMounted(() => {
  loadProductos()
  loadEventos()
  loadReservas()
  loadEnvios()
  loadDescuentos()
  void loadCarouselConfig()
})

const loadCarouselConfig = async (): Promise<void> => {
  const defaults = ['', '', '']
  try {
    const response = await api.get('/api/home-carousel')
    const apiSlides = response.data?.slides
    if (!Array.isArray(apiSlides)) {
      carouselSlides.value = defaults
      return
    }
    carouselSlides.value = defaults.map((_, idx) => {
      const value = apiSlides[idx]
      return typeof value === 'string' ? value : ''
    })
  } catch (error) {
    console.error('Error al cargar la configuracion del carrusel:', error)
    carouselSlides.value = defaults
  }
}

const saveCarouselConfig = async (): Promise<void> => {
  try {
    const payload = { slides: carouselSlides.value }
    const response = await api.put('/api/home-carousel', payload)
    const apiSlides = response.data?.slides
    if (Array.isArray(apiSlides)) {
      carouselSlides.value = ['', '', ''].map((_, idx) => {
        const value = apiSlides[idx]
        return typeof value === 'string' ? value : ''
      })
    }
    alert('Carrusel de home actualizado correctamente')
  } catch (error) {
    console.error('Error al guardar configuracion del carrusel:', error)
    alert('No se pudo guardar la configuracion del carrusel')
  }
}

const clearCarouselSlide = (index: number): void => {
  carouselSlides.value[index] = ''
}

const handleCarouselImageUpload = async (event: any, index: number): Promise<void> => {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]

  if (!file) {
    return
  }

  if (!file.type.startsWith('image/')) {
    alert('Por favor selecciona un archivo de imagen valido')
    input.value = ''
    return
  }

  if (file.size > 5 * 1024 * 1024) {
    alert('La imagen debe tener un tamaño maximo de 5MB')
    input.value = ''
    return
  }

  const formData = new FormData()
  formData.append('file', file)
  carouselUploadingIndex.value = index

  try {
    const response = await api.post('/api/upload/image', formData)
    if (!response.data?.url) {
      throw new Error('No se recibio URL de imagen')
    }
    carouselSlides.value[index] = resolveBackendImageUrl(response.data.url)
  } catch (error: any) {
    console.error('Error al subir imagen del carrusel:', error)
    const errorMsg = error.response?.data?.message || error.message || 'Error desconocido'
    alert(`No se pudo subir la imagen: ${errorMsg}`)
  } finally {
    carouselUploadingIndex.value = null
    input.value = ''
  }
}

const getReservaCountByStatus = (status: string): number => {
  if (status === 'todas') return reservasAdmin.value.length
  return reservasAdmin.value.filter(r => r.estado === status).length
}

const formatDate = (dateString: string): string => {
  const date = new Date(dateString)
  return date.toLocaleDateString('es-ES', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatPrice = (price: number): string => {
  return `${price.toFixed(2)}€`
}

const getStatusLabel = (estado: string): string => {
  const labels: Record<string, string> = {
    pendiente: '⏳ Pendiente',
    disponible: '✓ Disponible',
    recogido: '✓ Recogido',
    cancelada: '✗ Cancelada'
  }
  return labels[estado] || estado
}

const marcarDisponible = async (reserva: ReservaAdmin): Promise<void> => {
  if (!confirm(`¿Marcar como disponible la reserva #${reserva.id}?`)) {
    return
  }

  try {
    await api.put(`/pedidos/${reserva.pedidoId}/estado`, {
      estado: 'DISPONIBLE'
    })

    reserva.estado = 'disponible'
    reserva.fechaDisponibilidad = new Date().toISOString()
    alert(`Reserva marcada como disponible. Se notificará a ${reserva.cliente.nombre}`)
    
    // Recargar las reservas
    await loadReservas()
  } catch (error) {
    console.error('❌ Error al marcar como disponible:', error)
    alert('Error al actualizar el estado. Por favor, intenta de nuevo.')
  }
}

const marcarEntregado = async (reserva: ReservaAdmin): Promise<void> => {
  if (!confirm(`¿Confirmar que ${reserva.cliente.nombre} ha recogido la reserva #${reserva.id}?`)) {
    return
  }

  try {
    await api.put(`/pedidos/${reserva.pedidoId}/estado`, {
      estado: 'RECOGIDO'
    })

    reserva.estado = 'recogido'
    alert('Reserva marcada como recogida')
    
    // Recargar las reservas
    await loadReservas()
  } catch (error) {
    console.error('❌ Error al marcar como recogido:', error)
    alert('Error al actualizar el estado. Por favor, intenta de nuevo.')
  }
}

const contactarCliente = (reserva: ReservaAdmin): void => {
  const mensaje = `Hola ${reserva.cliente.nombre}, te contactamos sobre tu reserva #${reserva.id}`
  alert(`Abrir email para: ${reserva.cliente.email}\n\nMensaje: ${mensaje}`)
}

const editarReserva = (reserva: ReservaAdmin): void => {
  alert(`Editar reserva #${reserva.id} (funcionalidad en desarrollo)`)
}

const cancelarReservaAdmin = async (reserva: ReservaAdmin): Promise<void> => {
  if (!confirm(`¿Cancelar la reserva #${reserva.id}? Se notificará a ${reserva.cliente.nombre}`)) {
    return
  }

  try {
    await api.put(`/pedidos/${reserva.pedidoId}/estado`, {
      estado: 'CANCELADO'
    })

    reserva.estado = 'cancelada'
    alert('Reserva cancelada')
    
    // Recargar las reservas
    await loadReservas()
  } catch (error) {
    console.error('❌ Error al cancelar reserva:', error)
    alert('Error al cancelar. Por favor, intenta de nuevo.')
  }
}

const marcarPedidoEnviado = async (pedidoId: number): Promise<void> => {
  if (!confirm(`Marcar pedido #${pedidoId} como enviado?`)) {
    return
  }

  try {
    await api.put(`/pedidos/${pedidoId}/estado`, {
      estado: 'ENVIADO',
    })
    await loadEnvios()
    alert(`Pedido #${pedidoId} marcado como enviado`)
  } catch (error) {
    console.error('Error al actualizar estado del envio:', error)
    alert('No se pudo actualizar el estado del envio')
  }
}

const marcarPedidoRecogido = async (pedidoId: number): Promise<void> => {
  if (!confirm(`Marcar pedido #${pedidoId} como recogido?`)) {
    return
  }

  try {
    await api.put(`/pedidos/${pedidoId}/estado`, {
      estado: 'RECOGIDO',
    })
    await loadEnvios()
    alert(`Pedido #${pedidoId} marcado como recogido`)
  } catch (error) {
    console.error('Error al actualizar estado de recogida:', error)
    alert('No se pudo actualizar el estado de recogida')
  }
}

// Productos
const showProductForm = ref(false)
const editingProduct = ref<Product | null>(null)
const productCategories = ['TCG', 'Manga', 'Comics', 'Figuras', 'Juegos de mesa', 'Accesorios', 'Merchandising']
const productSubcategoriesByCategory: Record<string, string[]> = {
  TCG: ['Yu-Gi-Oh', 'Magic', 'Pokemon', 'One Piece', 'Digimon', 'Dragon Ball'],
  Manga: ['Shonen', 'Seinen', 'Shojo', 'Josei', 'Kodomo'],
  Comics: ['Marvel', 'DC', 'Image', 'Dark Horse', 'Independientes'],
  Figuras: ['Nendoroid', 'Funko Pop', 'Scale Figures', 'Estatuas', 'Bustos'],
  'Juegos de mesa': ['Estrategia', 'Familiar', 'Party', 'Cooperativos', 'Rol'],
  Accesorios: ['Fundas', 'Deck Box', 'Playmat', 'Dados', 'Carpetas'],
  Merchandising: ['Ropa', 'Pósters', 'Llaveros', 'Tazas', 'Otros']
}

const availableProductSubcategories = computed(() => {
  return productSubcategoriesByCategory[productForm.value.category] || []
})

const productForm = ref({
  name: '',
  category: '',
  subcategory: '',
  price: 0,
  originalPrice: null as number | null,
  discount: 0,
  image: '',
  available: true,
  stock: 0,
  rating: 0.0,
  reviews: 0,
  isReserve: false,
  isNew: false
})
const imagePreview = ref<string | null>(null)
const uploadingImage = ref(false)

watch(
  () => productForm.value.category,
  (newCategory) => {
    const options = productSubcategoriesByCategory[newCategory] || []
    if (options.length > 0 && productForm.value.subcategory && !options.includes(productForm.value.subcategory)) {
      productForm.value.subcategory = ''
    }
  }
)

const products = ref<Product[]>([])

// Función para resetear el formulario de productos
const resetProductForm = (): void => {
  productForm.value = {
    name: '',
    category: '',
    subcategory: '',
    price: 0,
    originalPrice: null,
    discount: 0,
    image: '',
    available: true,
    stock: 0,
    rating: 0.0,
    reviews: 0,
    isReserve: false,
    isNew: false
  }
  imagePreview.value = null
  editingProduct.value = null
}

// Función para abrir el formulario de nuevo producto
const openProductForm = (): void => {
  resetProductForm()
  showProductForm.value = true
}

// Función para cerrar el formulario
const closeProductForm = (): void => {
  showProductForm.value = false
  resetProductForm()
}

// Eventos
const showEventForm = ref(false)
const editingEvent = ref<Event | null>(null)
const eventForm = ref<Omit<Event, 'id'>>({ 
  name: '', 
  date: '', 
  time: '', 
  description: '', 
  type: 'tournament' 
})

const events = ref<Event[]>([])

// Función para resetear el formulario de eventos
const resetEventForm = (): void => {
  eventForm.value = { 
    name: '', 
    date: '', 
    time: '', 
    description: '', 
    type: 'tournament' 
  }
  editingEvent.value = null
}

// Función para abrir formulario de nuevo evento
const openEventForm = (): void => {
  resetEventForm()
  showEventForm.value = true
}

// Función para cerrar formulario de evento
const closeEventForm = (): void => {
  showEventForm.value = false
  resetEventForm()
}

// Descuentos
const showDiscountModal = ref(false)
const discounts = ref<any[]>([])
const discountsLoading = ref(false)
const discountForm = ref({
  id: null as number | null,
  code: '',
  type: 'percentage',
  value: 0,
  scope: 'global',
  scopeValue: '',
  startDate: '',
  endDate: ''
})
const categories = ['TCG', 'Manga', 'Cómics', 'Merchandising', 'Accesorios']

// Usuarios
// Métodos Productos
const saveProduct = async (): Promise<void> => {
  try {
    // Validaciones
    if (!productForm.value.name?.trim()) {
      alert('⚠️ El nombre del producto es obligatorio')
      return
    }
    
    if (!productForm.value.category?.trim()) {
      alert('⚠️ La categoría es obligatoria')
      return
    }
    
    if (!productForm.value.price || productForm.value.price <= 0) {
      alert('⚠️ El precio debe ser mayor a 0')
      return
    }
    
    if (!productForm.value.image) {
      alert('⚠️ Debes subir una imagen del producto')
      return
    }
    
    console.log('💾 Guardando producto...', productForm.value)
    
    if (editingProduct.value) {
      // Actualizar producto existente
      await api.put(`/api/products/${editingProduct.value.id}`, productForm.value)
      console.log('✅ Producto actualizado')
      alert('✅ Producto actualizado correctamente')
    } else {
      // Crear nuevo producto
      await api.post('/api/products', productForm.value)
      console.log('✅ Producto creado')
      alert('✅ Producto creado correctamente')
    }
    closeProductForm()
    await loadProductos()
  } catch (err: any) {
    console.error('❌ Error al guardar producto:', err)
    const errorMsg = err.response?.data?.message || err.message || 'Error desconocido'
    alert(`❌ Error al guardar el producto: ${errorMsg}`)
  }
}

const editProductHandler = (product: Product): void => {
  editingProduct.value = product
  productForm.value = {
    name: product.name,
    category: product.category,
    subcategory: product.subcategory || '',
    price: product.price,
    originalPrice: product.originalPrice || null,
    discount: product.discount || 0,
    image: product.image,
    available: product.available !== undefined ? product.available : true,
    stock: product.stock || 0,
    rating: product.rating || 0.0,
    reviews: product.reviews || 0,
    isReserve: product.isReserve || false,
    isNew: product.isNew || false
  }
  imagePreview.value = product.image || null
  showProductForm.value = true
}

const deleteProductHandler = async (id: number): Promise<void> => {
  if (confirm('¿Está seguro de que desea eliminar este producto?')) {
    try {
      await api.delete(`/api/products/${id}`)
      alert('Producto eliminado correctamente')
      await loadProductos()
    } catch (err) {
      console.error('❌ Error al eliminar producto:', err)
      alert('Error al eliminar el producto')
    }
  }
}

const handleImageUpload = async (event: any): Promise<void> => {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  
  if (!file) return
  
  // Validar tipo
  if (!file.type.startsWith('image/')) {
    alert('⚠️ Por favor selecciona un archivo de imagen')
    return
  }
  
  // Validar tamaño (máx 5MB)
  if (file.size > 5 * 1024 * 1024) {
    alert('⚠️ La imagen debe tener un tamaño máximo de 5MB')
    return
  }
  
  uploadingImage.value = true
  const formData = new FormData()
  formData.append('file', file)
  
  try {
    console.log('⏳ Subiendo imagen...', { nombre: file.name, tipo: file.type, tamaño: file.size })
    const response = await api.post('/api/upload/image', formData)
    
    console.log('✅ Respuesta del servidor:', response.data)
    
    if (response.data?.url) {
      const normalizedUrl = resolveBackendImageUrl(response.data.url)
      productForm.value.image = normalizedUrl
      imagePreview.value = normalizedUrl
      console.log('✅ Imagen subida correctamente:', normalizedUrl)
    } else {
      console.error('❌ No hay URL en la respuesta:', response.data)
      alert('❌ Error: El servidor no devolvió la URL de la imagen')
    }
  } catch (err: any) {
    console.error('❌ Error al subir imagen:', err)
    const errorMsg = err.response?.data?.message || err.message || 'Error desconocido'
    alert(`❌ Error al subir la imagen: ${errorMsg}`)
  } finally {
    uploadingImage.value = false
  }
}

// Métodos Eventos
const saveEvent = async (): Promise<void> => {
  try {
    if (editingEvent.value) {
      // Actualizar evento existente
      const eventoData = {
        nombre: eventForm.value.name,
        fecha: eventForm.value.date + 'T' + eventForm.value.time,
        descripcion: eventForm.value.description,
        tipo: eventForm.value.type
      }
      await api.put(`/eventos/${editingEvent.value.id}`, eventoData)
      alert('Evento actualizado correctamente')
    } else {
      // Crear nuevo evento
      const eventoData = {
        nombre: eventForm.value.name,
        fecha: eventForm.value.date + 'T' + eventForm.value.time,
        descripcion: eventForm.value.description,
        tipo: eventForm.value.type
      }
      await api.post('/eventos', eventoData)
      alert('Evento creado correctamente')
    }
    closeEventForm()
    await loadEventos()
  } catch (err) {
    console.error('❌ Error al guardar evento:', err)
    alert('Error al guardar el evento')
  }
}

const editEventHandler = (event: Event): void => {
  editingEvent.value = event
  eventForm.value = { ...event }
  showEventForm.value = true
}

const deleteEventHandler = async (id: number): Promise<void> => {
  if (confirm('¿Está seguro de que desea eliminar este evento?')) {
    try {
      await api.delete(`/eventos/${id}`)
      alert('Evento eliminado correctamente')
      await loadEventos()
    } catch (err) {
      console.error('❌ Error al eliminar evento:', err)
      alert('Error al eliminar el evento')
    }
  }
}

// Métodos Descuentos
const loadDescuentos = async () => {
  discountsLoading.value = true
  try {
    const response = await api.get('/api/discounts')
    discounts.value = response.data
    console.log('✅ Descuentos cargados:', discounts.value.length)
  } catch (err) {
    console.error('❌ Error al cargar descuentos:', err)
  } finally {
    discountsLoading.value = false
  }
}

// Abrir modal para crear descuento
const openDiscountModal = () => {
  discountForm.value = {
    id: null,
    code: '',
    type: 'percentage',
    value: 0,
    scope: 'global',
    scopeValue: '',
    startDate: '',
    endDate: ''
  }
  showDiscountModal.value = true
}

// Editar descuento
const editDiscount = (discount: any) => {
  // Convertir fechas ISO 8601 a formato YYYY-MM-DD para el input date
  const convertToDateFormat = (dateString: string): string => {
    const date = new Date(dateString)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  }

  discountForm.value = {
    id: discount.id,
    code: discount.code,
    type: discount.type,
    value: discount.value,
    scope: discount.scope,
    scopeValue: discount.scopeValue || '',
    startDate: convertToDateFormat(discount.startDate),
    endDate: convertToDateFormat(discount.endDate)
  }
  showDiscountModal.value = true
}

// Cerrar modal
const closeDiscountModal = () => {
  showDiscountModal.value = false
  discountForm.value = {
    id: null,
    code: '',
    type: 'percentage',
    value: 0,
    scope: 'global',
    scopeValue: '',
    startDate: '',
    endDate: ''
  }
}

// Guardar descuento
const saveDiscount = async () => {
  // Validaciones
  if (!discountForm.value.code.trim()) {
    alert('⚠️ El código de descuento es obligatorio')
    return
  }
  
  if (discountForm.value.value <= 0) {
    alert('⚠️ El valor del descuento debe ser mayor a 0')
    return
  }
  
  if (!discountForm.value.startDate || !discountForm.value.endDate) {
    alert('⚠️ Las fechas de inicio y fin son obligatorias')
    return
  }
  
  if (new Date(discountForm.value.startDate) > new Date(discountForm.value.endDate)) {
    alert('⚠️ La fecha de inicio debe ser anterior a la fecha de fin')
    return
  }
  
  if (discountForm.value.scope !== 'global' && !discountForm.value.scopeValue.trim()) {
    alert('⚠️ Debes seleccionar o especificar el alcance del descuento')
    return
  }

  try {
    const normalizedCode = discountForm.value.code.trim().toUpperCase()

    // Convertir fechas de YYYY-MM-DD a formato LocalDateTime ISO 8601
    const startDateTime = new Date(discountForm.value.startDate)
    startDateTime.setHours(0, 0, 0, 0)
    const endDateTime = new Date(discountForm.value.endDate)
    endDateTime.setHours(23, 59, 59, 999)

    const payload = {
      ...discountForm.value,
      code: normalizedCode,
      startDate: startDateTime.toISOString(),
      endDate: endDateTime.toISOString()
    }

    if (discountForm.value.id) {
      // Actualizar
      await api.put(`/api/discounts/${discountForm.value.id}`, payload)
      alert('✅ Código de descuento actualizado correctamente')
    } else {
      // Crear
      await api.post('/api/discounts', payload)
      alert('✅ Código de descuento creado correctamente')
    }
    
    closeDiscountModal()
    await loadDescuentos()
  } catch (error: any) {
    console.error('Error al guardar descuento:', error)
    const errorMsg = error.response?.data?.message || error.response?.data?.error || 'Error desconocido'
    alert('❌ Error al guardar el código de descuento: ' + errorMsg)
  }
}

// Eliminar descuento
const deleteDiscount = async (id: number) => {
  if (!confirm('¿Estás seguro de que deseas eliminar este código de descuento?')) {
    return
  }
  
  try {
    await api.delete(`/api/discounts/${id}`)
    alert('✅ Código de descuento eliminado correctamente')
    await loadDescuentos()
  } catch (error) {
    console.error('Error al eliminar descuento:', error)
    alert('❌ Error al eliminar el código de descuento')
  }
}

// Obtener texto del alcance
const getScopeText = (discount: any) => {
  switch (discount.scope) {
    case 'global':
      return 'Todos los productos'
    case 'category':
      return `Categoría: ${discount.scopeValue}`
    case 'subcategory':
      return `Subcategoría: ${discount.scopeValue}`
    case 'product':
      return `Producto ID: ${discount.scopeValue}`
    default:
      return 'No especificado'
  }
}

// Reportes - Exportación a Excel
const productExportOptions = ref({
  name: true,
  category: true,
  price: true,
  stock: true,
  discount: true,
  available: true
})

const totalStockValue = computed(() => {
  return products.value.reduce((total, product) => {
    return total + (product.price * (product.stock || 0))
  }, 0)
})

const exportProductsToExcel = async () => {
  try {
    // Preparar datos para exportar
    const dataToExport = products.value.map((product: any) => {
      const row: any = {}
      
      if (productExportOptions.value.name) row['Nombre'] = product.name
      if (productExportOptions.value.category) row['Categoría'] = product.category
      if (productExportOptions.value.price) row['Precio (€)'] = product.price
      if (productExportOptions.value.stock) row['Stock'] = product.stock || 0
      if (productExportOptions.value.discount) row['Descuento (%)'] = product.discount || 0
      if (productExportOptions.value.available) row['Disponible'] = product.available ? 'Sí' : 'No'
      
      return row
    })

    // Crear libro de trabajo
    const worksheet = XLSX.utils.json_to_sheet(dataToExport)
    const workbook = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Productos')

    // Ajustar ancho de columnas
    const colWidths = Object.keys(dataToExport[0] || {}).map(() => 15)
    worksheet['!cols'] = colWidths.map(width => ({ wch: width }))

    // Descargar archivo
    const fileName = `Productos_${new Date().toISOString().split('T')[0]}.xlsx`
    XLSX.writeFile(workbook, fileName)

    alert('✅ Archivo Excel descargado correctamente')
  } catch (error) {
    console.error('Error al exportar productos:', error)
    alert('❌ Error al descargar el archivo')
  }
}

// Métodos Usuarios
</script>

<style scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.admin-container {
  display: flex;
  min-height: 100vh;
  background-color: #f9fafb;
}

.admin-sidebar {
  width: 250px;
  background-color: #1f2937;
  color: white;
  padding: 30px 20px;
  position: sticky;
  top: 0;
  height: 100vh;
  overflow-y: auto;
}

.admin-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 30px;
  color: #dc2626;
}

.admin-nav {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.nav-btn {
  padding: 12px 15px;
  background: none;
  border: none;
  color: #d1d5db;
  text-align: left;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.3s ease;
  font-weight: 500;
}

.nav-btn:hover {
  background-color: #374151;
  color: white;
}

.nav-btn.active {
  background-color: #dc2626;
  color: white;
}

.admin-main {
  flex: 1;
  padding: 40px;
  overflow-y: auto;
}

.admin-section {
  background-color: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.admin-section h1 {
  color: #1f2937;
  margin-bottom: 25px;
  border-bottom: 2px solid #dc2626;
  padding-bottom: 15px;
}

/* Reservas Admin Styles */
.reservas-filters {
  display: flex;
  gap: 10px;
  margin-bottom: 25px;
  flex-wrap: wrap;
}

.filter-btn {
  padding: 10px 18px;
  border: 2px solid #e5e7eb;
  background: white;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 600;
  color: #6b7280;
  font-size: 14px;
}

.filter-btn:hover {
  border-color: #dc2626;
  color: #dc2626;
}

.filter-btn.active {
  background-color: #dc2626;
  color: white;
  border-color: #dc2626;
}

.reservas-admin-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.reserva-admin-card {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  overflow: hidden;
  transition: box-shadow 0.3s ease;
}

.reserva-admin-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.reserva-admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #f9fafb;
  border-bottom: 1px solid #e5e7eb;
}

.reserva-info-left {
  display: flex;
  gap: 15px;
  align-items: center;
}

.reserva-id {
  font-weight: bold;
  color: #1f2937;
  font-size: 16px;
}

.status-badge {
  display: inline-block;
  padding: 5px 14px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
}

.status-badge.pendiente {
  background-color: #fef3c7;
  color: #92400e;
}

.status-badge.disponible {
  background-color: #d1fae5;
  color: #065f46;
}

.status-badge.recogido {
  background-color: #dbeafe;
  color: #1e40af;
}

.status-badge.cancelada {
  background-color: #fee2e2;
  color: #991b1b;
}

.reserva-info-right {
  color: #6b7280;
  font-size: 14px;
}

.reserva-admin-body {
  padding: 20px;
  display: grid;
  gap: 20px;
}

.cliente-info {
  display: flex;
  gap: 15px;
  align-items: center;
  padding: 15px;
  background-color: #f9fafb;
  border-radius: 8px;
}

.cliente-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #dc2626;
}

.cliente-details h4 {
  color: #1f2937;
  margin-bottom: 5px;
  font-size: 16px;
}

.cliente-details p {
  color: #6b7280;
  font-size: 13px;
  margin: 2px 0;
}

.producto-reservado {
  display: flex;
  gap: 15px;
  align-items: center;
  padding: 15px;
  background-color: #fffbeb;
  border-radius: 8px;
  border-left: 4px solid #f59e0b;
}

.producto-img {
  width: 80px;
  height: 110px;
  object-fit: cover;
  border-radius: 6px;
  border: 2px solid #e5e7eb;
}

.producto-info h4 {
  color: #1f2937;
  margin-bottom: 8px;
  font-size: 15px;
}

.producto-info .categoria {
  display: inline-block;
  background: #e5e7eb;
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  color: #4b5563;
  margin-bottom: 5px;
}

.producto-info .cantidad {
  color: #6b7280;
  font-size: 13px;
  margin: 5px 0;
}

.producto-info .precio {
  color: #1f2937;
  font-size: 14px;
  margin-top: 8px;
}

.producto-info .precio strong {
  color: #dc2626;
  font-size: 16px;
}

.disponibilidad-info {
  padding: 12px 15px;
  background-color: #f0fdf4;
  border-radius: 6px;
  color: #166534;
  font-size: 14px;
  border-left: 4px solid #22c55e;
}

.notas-admin {
  padding: 12px 15px;
  background-color: #eff6ff;
  border-radius: 6px;
  color: #1e40af;
  font-size: 14px;
  border-left: 4px solid #3b82f6;
}

.reserva-admin-actions {
  display: flex;
  gap: 10px;
  padding: 15px 20px;
  border-top: 1px solid #e5e7eb;
  background-color: #fafafa;
  flex-wrap: wrap;
}

.reserva-admin-actions button {
  padding: 10px 18px;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 13px;
}

.btn-success {
  background-color: #22c55e;
  color: white;
}

.btn-success:hover {
  background-color: #16a34a;
}

.btn-primary {
  background-color: #3b82f6;
  color: white;
}

.btn-primary:hover {
  background-color: #2563eb;
}

.btn-contact {
  background-color: #8b5cf6;
  color: white;
}

.btn-contact:hover {
  background-color: #7c3aed;
}

.btn-edit {
  background-color: #fbbf24;
  color: #1f2937;
}

.btn-edit:hover {
  background-color: #f59e0b;
}

.btn-cancel {
  background-color: #fee2e2;
  color: #991b1b;
}

.btn-cancel:hover {
  background-color: #fecaca;
}

.empty-reservas {
  text-align: center;
  padding: 60px 20px;
  color: #6b7280;
  font-size: 16px;
}

.carousel-admin-subtitle {
  color: #6b7280;
  margin-bottom: 20px;
}

.carousel-admin-help {
  display: grid;
  gap: 6px;
  margin-bottom: 20px;
  padding: 14px 16px;
  border-radius: 10px;
  border: 1px solid #fecaca;
  background: #fef2f2;
  color: #7f1d1d;
  font-size: 14px;
}

.carousel-admin-help strong {
  color: #991b1b;
}

.carousel-admin-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 16px;
}

.carousel-admin-card {
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 14px;
  background: #ffffff;
}

.carousel-admin-card h3 {
  margin-bottom: 10px;
  color: #1f2937;
}

.carousel-admin-preview-wrap {
  height: 160px;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  background: #f9fafb;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  margin-bottom: 10px;
}

.carousel-admin-preview {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.carousel-admin-empty {
  color: #9ca3af;
  font-size: 13px;
}

.carousel-admin-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.carousel-admin-footer {
  margin-top: 18px;
  display: flex;
  justify-content: flex-end;
}

/* Estilos existentes */
.add-btn {
  padding: 12px 24px;
  background-color: #dc2626;
  color: white;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  margin-bottom: 20px;
  transition: background-color 0.3s ease;
}

.add-btn:hover {
  background-color: #b91c1c;
}

.form-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  overflow-y: auto;
  padding: 20px;
}

.form-content {
  background-color: white;
  padding: 20px;
  border-radius: 12px;
  width: 100%;
  max-width: 450px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
  max-height: 90vh;
  overflow-y: auto;
}

.form-content h2 {
  color: #1f2937;
  margin: 0 0 15px 0;
  font-size: 18px;
}

.form-content form {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.form-content input,
.form-content textarea,
.form-content select {
  padding: 9px;
  border: 2px solid #e5e7eb;
  border-radius: 6px;
  font-size: 13px;
  font-family: inherit;
}

.form-content input[type="file"] {
  padding: 6px;
  cursor: pointer;
  font-size: 12px;
}

.form-content input:disabled {
  background-color: #f3f4f6;
  cursor: not-allowed;
  opacity: 0.6;
}

.form-content input:focus,
.form-content textarea:focus,
.form-content select:focus {
  outline: none;
  border-color: #dc2626;
  box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1);
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.form-group label {
  font-weight: 600;
  color: #374151;
  font-size: 13px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.form-group input,
.form-group textarea,
.form-group select {
  padding: 9px !important;
  border: 2px solid #e5e7eb !important;
  border-radius: 6px !important;
  font-size: 13px !important;
  transition: all 0.3s ease;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  border-color: #dc2626 !important;
  box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1) !important;
}

.form-group textarea {
  resize: vertical;
  min-height: 70px;
  font-family: inherit;
}

.checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 12px;
  background-color: #f9fafb;
  border-radius: 8px;
  border-left: 4px solid #dc2626;
}

.checkbox-group label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-weight: 500;
  color: #1f2937;
  margin: 0;
  font-size: 13px;
}

.checkbox-group input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #dc2626;
}

.form-buttons {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.save-btn,
.cancel-btn {
  flex: 1;
  padding: 10px;
  border: none;
  border-radius: 6px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease;
  font-size: 13px;
}

.save-btn {
  background-color: #dc2626;
  color: white;
}

.save-btn:hover:not(:disabled) {
  background-color: #b91c1c;
}

.save-btn:disabled {
  background-color: #d1d5db;
  cursor: not-allowed;
}

.cancel-btn {
  background-color: #e5e7eb;
  color: #1f2937;
}

.cancel-btn:hover {
  background-color: #d1d5db;
}

.image-upload-section {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.image-upload-section label {
  font-weight: 600;
  color: #374151;
  font-size: 13px;
}

.uploading-text {
  color: #3b82f6;
  font-size: 12px;
  font-style: italic;
  margin: 0;
}

.image-preview {
  display: flex;
  justify-content: center;
  padding: 10px;
  background-color: #f9fafb;
  border-radius: 6px;
  border: 2px solid #e5e7eb;
}

.preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  max-width: 100%;
}

.preview-container img {
  max-width: 100%;
  max-height: 150px;
  border-radius: 6px;
  object-fit: contain;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.no-image-placeholder {
  padding: 15px 10px;
  background-color: #f3f4f6;
  border-radius: 6px;
  border: 2px dashed #d1d5db;
  text-align: center;
  color: #6b7280;
  font-size: 12px;
}

.no-image-placeholder p {
  margin: 0;
}

.items-list {
  display: grid;
  gap: 15px;
}

.item-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 15px;
  background-color: #f9fafb;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.item-image {
  width: 80px;
  height: 80px;
  border-radius: 6px;
  object-fit: cover;
}

.item-details {
  flex: 1;
}

.item-details h3 {
  color: #1f2937;
  margin-bottom: 5px;
}

.item-details p {
  color: #6b7280;
  font-size: 14px;
  margin: 3px 0;
}

.stock-indicator {
  font-weight: 600;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 13px;
  margin-top: 5px;
  display: inline-block;
}

.stock-indicator.in-stock {
  background-color: #d1fae5;
  color: #065f46;
}

.stock-indicator.low-stock {
  background-color: #fef3c7;
  color: #92400e;
}

.stock-indicator.out-of-stock {
  background-color: #fee2e2;
  color: #991b1b;
}

.expiry,
.join-date {
  color: #9ca3af;
  font-size: 12px;
}

.item-actions {
  display: flex;
  gap: 10px;
}

.edit-btn,
.delete-btn,
.view-btn {
  padding: 8px 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.edit-btn {
  background-color: #fbbf24;
  color: #1f2937;
}

.edit-btn:hover {
  background-color: #f59e0b;
}

.delete-btn {
  background-color: #ef4444;
  color: white;
}

.delete-btn:hover {
  background-color: #dc2626;
}

.view-btn {
  background-color: #3b82f6;
  color: white;
}

.view-btn:hover {
  background-color: #2563eb;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 25px;
  margin-top: 30px;
}

.stats-header {
  margin-bottom: 10px;
}

.stats-header h1 {
  font-size: 28px;
  color: #1f2937;
  margin: 0 0 5px 0;
}

.stats-subtitle {
  color: #6b7280;
  font-size: 14px;
  margin: 0;
}

.stat-card {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  border-left: 4px solid #dc2626;
  transition: all 0.3s ease;
  text-align: center;
  position: relative;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.12);
}

.stat-card::before {
  content: '';
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: linear-gradient(135deg, transparent 0%, rgba(220, 38, 38, 0.1) 100%);
  z-index: -1;
}

.stat-card-productos {
  border-left-color: #dc2626;
}

.stat-card-usuarios {
  border-left-color: #3b82f6;
}

.stat-card-eventos {
  border-left-color: #f59e0b;
}

.stat-card-reservas {
  border-left-color: #10b981;
}

.stat-icon {
  font-size: 32px;
  margin-bottom: 15px;
}

.stat-card h3 {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 10px 0;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-weight: 600;
}

.stat-number {
  font-size: 40px;
  font-weight: bold;
  color: #1f2937;
  margin: 10px 0;
  line-height: 1;
}

.stat-label {
  font-size: 12px;
  color: #9ca3af;
  margin: 10px 0 0 0;
  font-weight: 500;
}

.unauthorized {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: #f9fafb;
  text-align: center;
}

.unauthorized h1 {
  color: #1f2937;
  margin-bottom: 10px;
}

.unauthorized p {
  color: #6b7280;
  margin-bottom: 20px;
}

.back-btn {
  padding: 12px 30px;
  background-color: #dc2626;
  color: white;
  text-decoration: none;
  border-radius: 6px;
  font-weight: 600;
  transition: background-color 0.3s ease;
}

.back-btn:hover {
  background-color: #b91c1c;
}

@media (max-width: 1024px) {
  .admin-container {
    flex-direction: column;
  }

  .admin-sidebar {
    width: 100%;
    height: auto;
    position: sticky;
  }

  .admin-nav {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
  }

  .admin-main {
    padding: 20px;
  }

  .reserva-admin-actions {
    flex-direction: column;
  }

  .reserva-admin-actions button {
    width: 100%;
  }

  .carousel-admin-footer {
    justify-content: stretch;
  }

  .carousel-admin-footer .save-btn {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .admin-main {
    padding: 15px;
  }

  .admin-section {
    padding: 15px;
  }

  .item-card {
    flex-direction: column;
    text-align: center;
  }

  .form-content {
    width: 95%;
  }

  .reserva-admin-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .cliente-info,
  .producto-reservado {
    flex-direction: column;
    text-align: center;
  }

  .producto-img {
    width: 100%;
    height: 150px;
  }
}

/* ==================== Estilos para Gestión de Descuentos ==================== */

.discounts-section-admin {
  width: 100%;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.section-header h1 {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #dc2626 0%, #991b1b 100%);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 0;
}

.create-discount-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: linear-gradient(135deg, #dc2626 0%, #991b1b 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.3);
}

.create-discount-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(220, 38, 38, 0.4);
}

.btn-icon {
  font-size: 20px;
  font-weight: 700;
}

.discounts-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 20px;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f4f6;
  border-top-color: #dc2626;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.discounts-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80px 20px;
  text-align: center;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.discounts-empty h4 {
  font-size: 24px;
  color: #1f2937;
  margin-bottom: 10px;
}

.discounts-empty p {
  color: #6b7280;
  font-size: 16px;
}

.discounts-list-admin {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 24px;
}

.discount-card-admin {
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s ease;
}

.discount-card-admin:hover {
  border-color: #dc2626;
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.1);
  transform: translateY(-2px);
}

.discount-header-admin {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e5e7eb;
  flex-wrap: wrap;
  gap: 10px;
}

.discount-code-badge-admin {
  font-size: 18px;
  font-weight: 700;
  color: #dc2626;
  background: #fef2f2;
  padding: 8px 16px;
  border-radius: 8px;
  font-family: 'Courier New', monospace;
  letter-spacing: 1px;
}

.discount-status-admin {
  display: flex;
  align-items: center;
}

.status-badge-admin {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
}

.status-badge-admin.active {
  background: #dcfce7;
  color: #15803d;
}

.status-badge-admin.inactive {
  background: #fee2e2;
  color: #991b1b;
}

.discount-actions-admin {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.action-btn-admin {
  min-height: 36px;
  padding: 8px 12px;
  border: 2px solid #e5e7eb;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 600;
  line-height: 1;
  white-space: nowrap;
  transition: all 0.2s ease;
}

.action-btn-admin:hover {
  transform: translateY(-1px);
}

.action-btn-admin.discount-edit-btn:hover {
  border-color: #3b82f6;
  background: #eff6ff;
}

.action-btn-admin.discount-delete-btn:hover {
  border-color: #dc2626;
  background: #fef2f2;
}

.discount-info-admin {
  display: grid;
  gap: 12px;
}

.discount-item-admin {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.discount-item-admin.full-width {
  grid-column: 1 / -1;
}

.discount-item-admin .label {
  font-weight: 600;
  color: #6b7280;
  font-size: 14px;
}

.discount-item-admin .value {
  font-weight: 600;
  color: #1f2937;
  font-size: 15px;
}

.discount-value-highlight {
  color: #10b981 !important;
  font-size: 18px !important;
}

/* Modal de Descuentos */
.modal-overlay-admin {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content-admin {
  background: white;
  border-radius: 16px;
  max-width: 650px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header-admin {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 2px solid #e5e7eb;
}

.modal-header-admin h2 {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0;
  font-size: 22px;
  color: #1f2937;
}

.modal-icon {
  font-size: 28px;
}

.modal-close-admin {
  width: 36px;
  height: 36px;
  border: none;
  background: #f3f4f6;
  color: #6b7280;
  border-radius: 8px;
  cursor: pointer;
  font-size: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.modal-close-admin:hover {
  background: #e5e7eb;
  color: #1f2937;
}

.modal-body-admin {
  padding: 24px;
}

.form-group-admin {
  margin-bottom: 20px;
}

.form-group-admin.half {
  margin-bottom: 0;
}

.form-group-admin label {
  display: block;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
  font-size: 14px;
}

.form-group-admin label.required::after {
  content: ' *';
  color: #dc2626;
}

.form-group-admin input[type="text"],
.form-group-admin input[type="number"],
.form-group-admin input[type="date"],
.select-input-admin {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 15px;
  transition: all 0.2s ease;
  font-family: inherit;
}

.form-group-admin input:focus,
.select-input-admin:focus {
  outline: none;
  border-color: #dc2626;
  box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1);
}

.uppercase-input {
  text-transform: uppercase;
}

.form-hint {
  display: block;
  margin-top: 6px;
  font-size: 13px;
  color: #6b7280;
}

.form-row-admin {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.input-with-suffix-admin {
  position: relative;
}

.input-with-suffix-admin input {
  padding-right: 50px;
}

.input-suffix-admin {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  font-weight: 600;
  color: #6b7280;
  font-size: 15px;
  pointer-events: none;
}

.modal-footer-admin {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 2px solid #e5e7eb;
}

.btn-cancel-admin {
  padding: 12px 24px;
  border: 2px solid #e5e7eb;
  background: white;
  color: #6b7280;
  border-radius: 8px;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-cancel-admin:hover {
  border-color: #d1d5db;
  background: #f9fafb;
}

.btn-save-admin {
  padding: 12px 24px;
  border: none;
  background: linear-gradient(135deg, #dc2626 0%, #991b1b 100%);
  color: white;
  border-radius: 8px;
  font-weight: 600;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(220, 38, 38, 0.3);
}

.btn-save-admin:hover {
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.4);
  transform: translateY(-1px);
}

@media (max-width: 768px) {
  .discounts-list-admin {
    grid-template-columns: 1fr;
  }

  .form-row-admin {
    grid-template-columns: 1fr;
  }

  .discount-header-admin {
    flex-direction: column;
    align-items: flex-start;
  }
}

/* Reportes */
.shipments-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
  margin: 20px 0;
}

.shipment-kpi {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-left: 4px solid #dc2626;
  border-radius: 10px;
  padding: 14px 16px;
}

.kpi-label {
  display: block;
  color: #6b7280;
  font-size: 13px;
  margin-bottom: 6px;
}

.kpi-value {
  color: #111827;
  font-size: 26px;
}

.shipments-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.shipment-group h2 {
  margin: 0 0 12px;
  font-size: 22px;
  color: #111827;
}

.shipment-card {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 18px;
}

.shipment-header {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  align-items: flex-start;
  margin-bottom: 14px;
}

.shipment-header h3 {
  margin: 0;
}

.shipment-date {
  color: #6b7280;
  font-size: 13px;
  margin-top: 4px;
}

.shipment-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

.shipment-grid h4 {
  margin-bottom: 8px;
  color: #111827;
}

.shipment-grid p {
  margin: 3px 0;
  color: #374151;
}

.shipment-products {
  padding-left: 18px;
  margin: 0 0 8px 0;
}

.shipment-products li {
  margin: 4px 0;
}

.shipment-total {
  font-weight: 700;
}

.shipment-actions {
  margin-top: 14px;
  padding-top: 12px;
  border-top: 1px solid #f3f4f6;
}

.reports-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 25px;
  margin-top: 30px;
}

.report-card {
  background: white;
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.report-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  border-color: #dc2626;
}

.report-header {
  margin-bottom: 20px;
  border-bottom: 2px solid #f3f4f6;
  padding-bottom: 15px;
}

.report-header h3 {
  font-size: 18px;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.report-header p {
  color: #6b7280;
  font-size: 14px;
  margin: 0;
}

.report-options {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.option-group h4 {
  font-size: 14px;
  color: #374151;
  margin: 0 0 10px 0;
}

.checkbox-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.checkbox-list label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #4b5563;
}

.checkbox-list input[type="checkbox"] {
  width: 18px;
  height: 18px;
  accent-color: #dc2626;
  cursor: pointer;
}

.export-btn {
  padding: 12px 20px;
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.2);
}

.export-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(220, 38, 38, 0.3);
}

.export-btn:active {
  transform: translateY(0);
}

.stats-info {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: #f9fafb;
  border-radius: 8px;
  border-left: 4px solid #dc2626;
}

.stat-label {
  font-weight: 500;
  color: #6b7280;
  font-size: 13px;
}

.stat-value {
  font-weight: 700;
  color: #dc2626;
  font-size: 18px;
}

@media (max-width: 768px) {
  .reports-container {
    grid-template-columns: 1fr;
  }

  .checkbox-list {
    grid-template-columns: 1fr;
  }

  .stats-info {
    grid-template-columns: 1fr;
  }
}
</style>