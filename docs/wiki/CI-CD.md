# CI/CD y publicación de imágenes Docker

Este monorepo usa GitHub Actions para:

1) CI (build por app con Turborepo) en PRs y ramas de GitFlow
2) Publicación de imágenes Docker en GitHub Container Registry (GHCR) al empujar a ramas principales y al crear tags semánticos

No hay CD a infraestructura desde GitHub; Portainer u otros orquestadores consumirán las imágenes desde GHCR.

## Workflows

- .github/workflows/ci.yml
  - Dispara en: PRs a main/master/develop y push a main/master/develop/feature/**/hotfix/**/release/**
  - Usa pnpm + Turborepo y Java 11
  - Pasos:
    - Setup Node/pnpm/Java, instalar dependencias
    - `pnpm turbo run build`

- .github/workflows/cd.yml (Publish Docker images)
  - Dispara en: push a main/master/develop, tags `v*` y manual (`workflow_dispatch`)
  - Publica 2 imágenes en GHCR:
    - ghcr.io/<owner>/fichaje-frontend
    - ghcr.io/<owner>/fichaje-backend
  - Tags generados (docker/metadata-action):
    - Para branches: `<branch>` (p.ej. `develop`)
    - Para tags semver: `vX.Y.Z`, `X.Y`, `X`
    - Para main/master: además `latest`
    - Siempre: `sha-<sha corto>`
  - Optimización: con `dorny/paths-filter` solo construye la imagen del app que cambió (en ramas). Para tags `v*`, construye ambas.

## Variables/secretos

- Usa GITHUB_TOKEN (proporcionado por Actions) para publicar en GHCR.
- FE_API_URL (Repository Variables) opcional para la build del frontend (incrusta la URL de API en la imagen). Por defecto: `http://localhost:8080`.
  - Ajusta en Settings > Variables > Actions > New variable: FE_API_URL

## Dockerfiles

- apps/fichaje-fe/Dockerfile
  - Build con Node 16 y serve con NGINX
  - ARG `apiURL` propagado como ENV `apiURL` durante el build
  - Copia `dist/angular-fichajesPi` (coincide con `angular.json`)
- apps/fichaje-be/Dockerfile
  - Build multi-stage con Maven Temurin 11 y runtime Temurin 11 JRE

## Consumo desde Portainer/Compose

- Autenticación a GHCR:
  - Crea un PAT con scope `read:packages` si usas máquinas fuera de GitHub Actions
  - `docker login ghcr.io` con ese PAT
  - Haz públicos los paquetes (Settings del repositorio > Packages > cambia a Public) para evitar autenticación en entornos públicos

- Nombres de imagen:
  - Frontend: `ghcr.io/<owner>/fichaje-frontend:<tag>`
  - Backend: `ghcr.io/<owner>/fichaje-backend:<tag>`

- Tags recomendados por flujo:
  - Entorno de pruebas: `develop`
  - Producción: `vX.Y.Z` o `latest` (main/master)

## Notas y buenas prácticas

- La build de CI omite tests backend por velocidad. Si añades tests de integración con DB, considera un job separado con contenedores de test.
- Para cambiar la URL del API del FE por entorno, usa `FE_API_URL` al publicar la imagen correspondiente.
- Revisa que los paquetes en GHCR estén marcados como Public si el proyecto es OSS y quieres pulls sin credenciales.
- Para despliegue, crea un docker-compose.prod.yml que use estas imágenes y variables de entorno propias del backend.

