# ---------- Etapa de construcción ----------
FROM node:20-alpine AS build
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci --no-audit --no-fund

COPY . .
# Build de producción: environment.ts apunta a /api (nginx hace de proxy al backend)
RUN npm run build

# ---------- Etapa de ejecución ----------
FROM nginx:stable-alpine

# Configuración de nginx con fallback SPA y proxy /api -> backend
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /app/dist/strattonapp-front/browser /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
