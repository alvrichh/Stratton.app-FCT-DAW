# StrattonApp - Frontend (Angular 17)

Interfaz web de StrattonApp. Consume la API REST del backend (`../Backend`).

## Desarrollo

```bash
npm install
npm start          # http://localhost:4200 (usa http://localhost:8080/api, ver src/environments/environment.development.ts)
```

## Build de producción

```bash
npm run build      # genera dist/strattonapp-front/browser (la API se espera en /api, ver nginx.conf)
```

## Tests

```bash
npm test           # Karma + Jasmine en ChromeHeadless (CHROME_BIN si Chrome no está en el PATH)
```

## Estructura

- `src/app/core/`: interceptor JWT (`auth.interceptor.ts`), guards de rutas (`auth.guard.ts`), alertas y directivas.
- `src/app/auth.service.ts`: login/registro y lectura del token.
- `src/app/pages/clientes/`: listado, alta, edición y detalle de clientes (con gestión de suministros/CUPS).
- `src/app/pages/empleados/`: gestión de empleados (solo ADMIN) y perfil.
- `src/app/services/catalogo.service.ts`: comercializadoras y asesorías.
- `src/environments/`: URL de la API por entorno.
