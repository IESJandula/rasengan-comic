import axios from 'axios'
import { auth } from '@/firebase'

const api = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 30000
})

api.interceptors.request.use(async (config) => {
  const user = auth.currentUser
  if (user) {
    const token = await user.getIdToken()
    config.headers = config.headers ?? {}
    ;(config.headers as Record<string, string>).Authorization = `Bearer ${token}`
  }
  
  // No establecer Content-Type para FormData - dejar que el navegador lo haga
  if (!(config.data instanceof FormData)) {
    config.headers['Content-Type'] = 'application/json'
  }
  
  return config
})

export default api
