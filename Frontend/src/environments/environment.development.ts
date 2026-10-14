/**
 * Configuración de desarrollo (ng serve): el backend corre en localhost:8080.
 */
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  /** true solo en Render: el backend gratuito se duerme y se muestra la pantalla de espera. */
  servidorGratuito: false,
};
