/**
 * Configuración de producción. La API se sirve bajo /api en el mismo origen
 * (nginx hace de proxy hacia el backend, ver Frontend/nginx.conf).
 */
export const environment = {
  production: true,
  apiUrl: '/api',
  /** true solo en Render: el backend gratuito se duerme y se muestra la pantalla de espera. */
  servidorGratuito: false,
};
