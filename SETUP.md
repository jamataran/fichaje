# 📋 Resumen de Configuración del Monorepo

## ✅ Estado Actual

- Turborepo: Configurado y funcionando
- Backend: Java 11 + Spring Boot 2.5.1 + Lombok 1.18.30
- Maven Wrapper: 3.9.6
- Docker Compose: entornos dev, test y prod bajo `deploy/`

## 🔧 Configuración Final

### Java/Maven
- Java: 11 (Temurin recomendado)
- Maven: wrapper incluido (3.9.6)
- Lombok: 1.18.30
- Spring Boot: 2.5.1

### Dockerfiles
- Backend build: `maven:3.9-eclipse-temurin-11`
- Backend runtime: `eclipse-temurin:11-jre`
- Frontend: Node 16 (Dockerfile en `apps/fichaje-fe`)

## 🚀 Comandos Principales

### Desarrollo Local
```bash
# Infraestructura (MySQL:3307, phpMyAdmin:8081, MailHog:8025)
cp deploy/dev/.env.example deploy/dev/.env
pnpm docker:dev:up

# Backend en modo dev (perfil dev)
cd apps/fichaje-be
pnpm dev

# Logs de la infraestructura
pnpm docker:dev:logs
```

### Build y Tests
```bash
# Build de todo el monorepo
pnpm build

# Build solo del backend
pnpm turbo run build --filter=@fichaje/be

# Tests del backend con BD temporal (MySQL en 3308)
cd apps/fichaje-be
pnpm test:with-db
```

### Producción
```bash
# Compose de producción (usa imágenes construidas)
cp deploy/prod/.env.example deploy/prod/.env
pnpm docker:prod:up
pnpm docker:prod:logs
pnpm docker:prod:down
```

## 📁 Estructura de Docker Compose

```
/deploy
├── dev/
│   ├── compose.yaml         # Desarrollo local
│   └── .env.example
├── prod/
│   ├── compose.yaml         # Producción
│   └── .env.example
└── overrides/
    └── logging.yaml         # Overrides opcionales (logs)

/apps/fichaje-be/docker-compose.test.yml  # BD efímera para tests
```

## 🔍 Perfiles de Spring Boot
- `application-dev.properties` → localhost:3307 (dev)
- `application-test.properties` → localhost:3308 (test)
- `application.properties` → producción (lee variables de entorno)

## 📊 Puertos

| Servicio | Dev | Test | Prod |
|----------|-----|------|------|
| MySQL    | 3307| 3308 | 3306 |
| Backend  | 8080| —    | 8080 |
| Frontend | 4200| —    | 80   |
| phpMyAdmin | 8081 | — | 81  |
| MailHog UI | 8025 | — | —   |

## 📚 Más documentación
- Orquestación con Docker: ver `DOCKER.md` (rutas, comandos y tips)

## CI/CD
- Los workflows actuales deben ajustarse a Java 11. Si quieres, puedo actualizar CI/CD para usar Java 11 y la nueva estructura `deploy/`.
