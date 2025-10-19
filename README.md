![Fichaje logo](packages/brand/assets/logo.svg)

# Fichaje

[![Monorepo](https://img.shields.io/badge/monorepo-turborepo-6A5ACD)](https://turbo.build/repo)
[![Package Manager](https://img.shields.io/badge/pnpm-%E2%89%A58-blueviolet)](https://pnpm.io/)
[![Frontend](https://img.shields.io/badge/Angular-13-red)](https://angular.io/)
[![Backend](https://img.shields.io/badge/Spring%20Boot-2.5-brightgreen)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-11-orange)](https://adoptium.net/temurin/releases/)
[![Docker](https://img.shields.io/badge/Docker-Compose-informational)](deploy/)
[![CodeQL](https://github.com/jamataran/fichaje/actions/workflows/codeql.yml/badge.svg)](https://github.com/jamataran/fichaje/actions/workflows/codeql.yml)

Fichaje es una aplicación para el registro de jornada de empleados en empresas. Permite gestionar usuarios, fichajes (entradas/salidas), calendarios, vacaciones e incidencias desde una interfaz web sencilla, con un backend robusto y base de datos MySQL.

**Repositorio actual**: https://github.com/jamataran/fichaje  
**Repositorio original**: https://github.com/alejandroferrin/fichajespi  
**Wiki del proyecto**: https://github.com/jamataran/fichaje/wiki

---

## Tabla de contenidos
- [Requisitos](#requisitos)
- [Instalación con Docker Compose](#instalación-con-docker-compose)
  - [Usando imágenes preconstruidas](#usando-imágenes-preconstruidas)
  - [Construyendo localmente](#construyendo-localmente)
- [Acceso y credenciales](#acceso-y-credenciales)
- [Desarrollo](#desarrollo)
- [Enlaces útiles](#enlaces-útiles)

## Requisitos
- Docker y Docker Compose
- Máquina con al menos 2 vCPU, 2 GB RAM y 10 GB de disco libres

## Instalación con Docker Compose

La orquestación vive en `deploy/prod/`. Necesitas crear un archivo `.env` con tus variables.

1) Crea el archivo de entorno de producción y ajústalo:

```bash
cp deploy/prod/.env.example deploy/prod/.env
# Edita deploy/prod/.env: credenciales MySQL, JWT_SECRET y SMTP
```

### Usando imágenes preconstruidas

Si tienes acceso a imágenes publicadas en GHCR:

```bash
# Las imágenes por defecto apuntan a:
# ghcr.io/jamataran/fichaje-frontend:latest
# ghcr.io/jamataran/fichaje-backend:latest

docker compose -f deploy/prod/compose.yaml --env-file deploy/prod/.env up -d
```

### Construyendo localmente

Si prefieres construir las imágenes tú mismo:

```bash
# Backend
docker build -t fichaje-backend:local apps/fichaje-be

# Frontend
docker build -t fichaje-frontend:local apps/fichaje-fe
```

Actualiza `deploy/prod/.env` para usar estas imágenes locales:

```
FRONTEND_IMAGE=fichaje-frontend:local
BACKEND_IMAGE=fichaje-backend:local
```

Y levanta el stack:

```bash
docker compose -f deploy/prod/compose.yaml --env-file deploy/prod/.env up -d
```

Una vez arriba, podrás acceder a:
- **Frontend**: http://localhost
- **Backend (API)**: http://localhost:8080
- **phpMyAdmin**: http://localhost:81

Para ver logs o parar:

```bash
docker compose -f deploy/prod/compose.yaml --env-file deploy/prod/.env logs -f
docker compose -f deploy/prod/compose.yaml --env-file deploy/prod/.env down
```

## Acceso y credenciales

El sistema crea un usuario por defecto tras la instalación (cámbialo tras el primer acceso):

- **Usuario**: `fichajesPi000`
- **Contraseña**: `fichajesPi000`

## Desarrollo

Si quieres contribuir o desarrollar localmente, consulta **[SETUP.md](SETUP.md)** donde encontrarás:

- Cómo levantar el entorno completo con **un solo comando** usando Turborepo
- Estructura del monorepo (apps, packages, deploy)
- Comandos para desarrollo, tests y builds
- Puertos y servicios disponibles en local

**Inicio rápido para desarrolladores**:

```bash
# 1. Instalar dependencias
pnpm install

# 2. Preparar entorno de desarrollo
cp deploy/dev/.env.example deploy/dev/.env

# 3. Levantar todo (infraestructura + backend + frontend)
pnpm dev:stack
```

Esto arrancará:
- MySQL (puerto 3307)
- phpMyAdmin (puerto 8081)
- MailHog para captura de emails (puerto 8025)
- Backend Spring Boot (puerto 8080)
- Frontend Angular (puerto 4200)

## Enlaces útiles
- **Guía de desarrollo**: [SETUP.md](SETUP.md) - Estructura del monorepo y comandos Turborepo
- **Orquestación Docker**: [DOCKER.md](DOCKER.md) - Detalles de compose dev/test/prod
- **CI/CD**: consulta la **Wiki** ➜ https://github.com/jamataran/fichaje/wiki/CI-CD
- **Proyecto original**: https://github.com/alejandroferrin/fichajespi
