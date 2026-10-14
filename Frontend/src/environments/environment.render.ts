/**
 * Configuración para Render: el frontend es un sitio estático y llama directamente al backend
 * (CORS permitido en el backend). El backend del plan gratuito se duerme tras 15 minutos sin
 * uso, así que la aplicación muestra una pantalla de espera mientras despierta.
 */
export const environment = {
  production: true,
  apiUrl: 'https://strattonapp-back.onrender.com/api',
  servidorGratuito: true,
};
