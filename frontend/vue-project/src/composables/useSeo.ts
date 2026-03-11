const SITE_NAME = 'Rasengan Comics'
const SITE_URL = 'https://rasengacomics.com'
const DEFAULT_IMAGE = `${SITE_URL}/og-image.jpg`

interface SeoOptions {
  title?: string
  description?: string
  image?: string
  url?: string
  type?: 'website' | 'product' | 'article'
  structuredData?: object | null
}

function setMeta(name: string, content: string, attr: 'name' | 'property' = 'name') {
  let el = document.querySelector(`meta[${attr}="${name}"]`) as HTMLMetaElement | null
  if (!el) {
    el = document.createElement('meta')
    el.setAttribute(attr, name)
    document.head.appendChild(el)
  }
  el.setAttribute('content', content)
}

function setLink(rel: string, href: string) {
  let el = document.querySelector(`link[rel="${rel}"]`) as HTMLLinkElement | null
  if (!el) {
    el = document.createElement('link')
    el.setAttribute('rel', rel)
    document.head.appendChild(el)
  }
  el.setAttribute('href', href)
}

function setStructuredData(data: object | null) {
  const existingScript = document.querySelector('script[data-seo="ld-json"]')
  if (existingScript) existingScript.remove()
  if (!data) return
  const script = document.createElement('script')
  script.type = 'application/ld+json'
  script.setAttribute('data-seo', 'ld-json')
  script.textContent = JSON.stringify(data)
  document.head.appendChild(script)
}

export function useSeo(options: SeoOptions = {}) {
  const {
    title,
    description = 'Tienda especializada en manga, TCG (Yu-Gi-Oh!, Pokémon, Magic), cómics americanos y merchandising. Envíos a toda España.',
    image = DEFAULT_IMAGE,
    url = SITE_URL + window.location.pathname,
    type = 'website',
    structuredData = null,
  } = options

  const fullTitle = title ? `${title} | ${SITE_NAME}` : `${SITE_NAME} | Manga, TCG y Cómics`
  document.title = fullTitle

  // Basic
  setMeta('description', description)

  // Open Graph
  setMeta('og:type', type === 'product' ? 'product' : type === 'article' ? 'article' : 'website', 'property')
  setMeta('og:site_name', SITE_NAME, 'property')
  setMeta('og:title', fullTitle, 'property')
  setMeta('og:description', description, 'property')
  setMeta('og:image', image, 'property')
  setMeta('og:image:width', '1200', 'property')
  setMeta('og:image:height', '630', 'property')
  setMeta('og:url', url, 'property')
  setMeta('og:locale', 'es_ES', 'property')

  // Twitter Card
  setMeta('twitter:card', 'summary_large_image')
  setMeta('twitter:title', fullTitle)
  setMeta('twitter:description', description)
  setMeta('twitter:image', image)

  // Canonical
  setLink('canonical', url)

  // Structured Data (JSON-LD)
  setStructuredData(structuredData)
}

/** JSON-LD presets reutilizables */
export const seoJsonLd = {
  store() {
    return {
      '@context': 'https://schema.org',
      '@type': 'Store',
      name: SITE_NAME,
      url: SITE_URL,
      description:
        'Tienda especializada en manga, TCG (Yu-Gi-Oh!, Pokémon, Magic), cómics americanos y merchandising.',
      currenciesAccepted: 'EUR',
      priceRange: '€€',
      image: DEFAULT_IMAGE,
      sameAs: [],
    }
  },

  product(p: {
    id: number
    name: string
    description: string
    price: number
    image: string
    category: string
    author?: string
    publisher?: string
    available: boolean
  }) {
    return {
      '@context': 'https://schema.org',
      '@type': 'Product',
      name: p.name,
      description: p.description,
      image: p.image,
      url: `${SITE_URL}/producto/${p.id}`,
      category: p.category,
      brand: p.publisher
        ? { '@type': 'Organization', name: p.publisher }
        : p.author
          ? { '@type': 'Person', name: p.author }
          : undefined,
      offers: {
        '@type': 'Offer',
        priceCurrency: 'EUR',
        price: p.price.toFixed(2),
        availability: p.available
          ? 'https://schema.org/InStock'
          : 'https://schema.org/OutOfStock',
        seller: { '@type': 'Organization', name: SITE_NAME },
      },
    }
  },

  breadcrumbs(items: { name: string; url: string }[]) {
    return {
      '@context': 'https://schema.org',
      '@type': 'BreadcrumbList',
      itemListElement: items.map((item, i) => ({
        '@type': 'ListItem',
        position: i + 1,
        name: item.name,
        item: item.url,
      })),
    }
  },
}
