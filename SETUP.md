# 📋 Configuración del Monorepo FichajesPi

## ✅ Estado Actual

- **Turborepo**: Configurado y funcionando con orquestación real de tareas
- **Backend**: Java 11 + Spring Boot 2.5.1 + Lombok 1.18.30
- **Frontend**: Angular 13 + Bootstrap 5
- **Maven Wrapper**: 3.9.6
- **Docker Compose**: entornos dev, test y prod bajo `deploy/`

## 🔧 Requisitos Previos

### Herramientas Necesarias
- **Java**: 11 (Temurin recomendado)
- **Node.js**: >=16
- **pnpm**: >=8 (se instalará automáticamente con el packageManager)
- **Docker Desktop**: Para levantar MySQL y servicios auxiliares
- **IDE**: IntelliJ IDEA (backend) y/o VS Code (frontend)

### Instalación Inicial
```bash
# 1. Clonar el repositorio
git clone <tu-repo>
cd fichaje

# 2. Instalar dependencias del monorepo
pnpm install

# 3. Verificar que Turborepo funciona
pnpm build
```

## 🚀 Cómo Empezar a Desarrollar

### Opción 1: Solo Base de Datos (Recomendado para IDE)

**Ideal si**: Quieres arrancar backend/frontend desde tu IDE con capacidad de debug

```bash
# 1. Levantar solo la base de datos y servicios auxiliares
pnpm dev:db

# 2. Arrancar backend desde IntelliJ (ver sección "Configuración del IDE")
# 3. Arrancar frontend desde VS Code o IntelliJ

# Al terminar
pnpm dev:db:down
```

**Servicios levantados**:
- MySQL en `localhost:3307`
- phpMyAdmin en `http://localhost:8081`
- MailHog en `http://localhost:8025`

**Cómo funciona**: Turborepo ejecuta la tarea `db:dev:up` del paquete `@fichaje/be`, que levanta solo los contenedores de infraestructura mediante Docker Compose.

### Opción 2: Stack Completo con Turborepo

**Ideal si**: Quieres levantar todo de una vez sin configurar nada en el IDE

```bash
# Levanta base de datos + backend + frontend (todo en paralelo)
pnpm dev:full

# Para detener: Ctrl+C y luego
pnpm dev:db:down
```

**Servicios levantados**:
- MySQL en `localhost:3307`
- Backend API en `http://localhost:8080`
- Frontend en `http://localhost:4200`
- phpMyAdmin en `http://localhost:8081`
- MailHog en `http://localhost:8025`

**Cómo funciona**: 
1. Turborepo levanta la BD con `db:dev:up` del paquete `@fichaje/be`
2. Luego ejecuta en paralelo las tareas `dev` de `@fichaje/be` y `@fichaje/fe`
3. El backend ejecuta `./mvnw spring-boot:run` con perfil dev
4. El frontend ejecuta `ng serve`

### Opción 3: Servicios Individuales

**Ideal si**: Necesitas control granular de qué levantas

```bash
# Solo base de datos
pnpm dev:db

# Solo backend (requiere que la BD esté levantada)
pnpm dev:be

# Solo frontend
pnpm dev:fe

# Detener base de datos
pnpm dev:db:down
```

**Cómo funciona**: Turborepo usa filtros (`--filter`) para ejecutar tareas solo en los paquetes especificados.

## 🛠️ Configuración del IDE

### IntelliJ IDEA (Backend)

#### 1. Prerrequisito: Levantar la Base de Datos
```bash
pnpm dev:db
```

#### 2. Importar el Proyecto
1. **File** → **Open** → Selecciona `apps/fichaje-be`
2. IntelliJ detectará que es un proyecto Maven
3. Espera a que se descarguen las dependencias

#### 3. Configurar Lombok
1. **File** → **Settings** → **Plugins**
2. Busca e instala **Lombok**
3. Reinicia IntelliJ si es necesario
4. **File** → **Settings** → **Build, Execution, Deployment** → **Compiler** → **Annotation Processors**
5. Activa **Enable annotation processing**

#### 4. Crear Configuración de Run
1. **Run** → **Edit Configurations**
2. Click en **+** → **Spring Boot**
3. Configura:
   - **Name**: `FichajesPi Dev`
   - **Main class**: Busca y selecciona `com.example.fichaje.FichajeBeApplication` (o la clase principal)
   - **Environment variables**: `SPRING_PROFILES_ACTIVE=dev`
   - **Use classpath of module**: `fichaje-be`
4. Click **OK**

#### 5. Verificar Configuración de BD
Abre `src/main/resources/application-dev.properties` y verifica:
```properties
spring.datasource.url=jdbc:mysql://localhost:3307/db_fichajespi_dev
spring.datasource.username=fichajes_dev
spring.datasource.password=fichajes_dev
```

#### 6. Ejecutar
Click en el botón ▶️ **Run** o **Debug** (🐛)

### VS Code o IntelliJ (Frontend)

#### 1. Abrir el Proyecto Frontend
```bash
cd apps/fichaje-fe
```

#### 2. Instalar Dependencias (si es necesario)
```bash
npm install
```

#### 3. Ejecutar en Modo Desarrollo
```bash
# Desde apps/fichaje-fe
npm run dev

# O desde la raíz del monorepo
pnpm dev:fe
```

#### 4. Abrir el Navegador
Navega a `http://localhost:4200`

## 🔍 Cómo Funciona Turborepo en Este Proyecto

Turborepo orquesta las tareas definidas en los `package.json` de cada workspace:

```
Raíz (pnpm dev:db)
  ↓
Turborepo ejecuta: turbo run db:dev:up --filter=@fichaje/be
  ↓
Se ejecuta el script db:dev:up del package.json de @fichaje/be
  ↓
Docker Compose levanta: MySQL + phpMyAdmin + MailHog
```

### Tareas Disponibles en turbo.json

- **`dev`**: Tareas persistentes (servidores de desarrollo)
- **`db:dev:up/down`**: Gestión de base de datos de desarrollo
- **`build`**: Compilación (cacheable)
- **`test`**: Tests (cacheable)
- **`lint`**: Linting (cacheable)

### Ventajas de Usar Turborepo

1. **Ejecución en paralelo**: `dev:full` ejecuta backend y frontend simultáneamente
2. **Filtros**: Ejecuta tareas solo en los paquetes necesarios
3. **Caché inteligente**: Reutiliza builds anteriores cuando no hay cambios
4. **Dependencias**: Respeta el orden de ejecución (ej: BD antes que backend)

## 📊 Puertos y Servicios

| Servicio | Puerto/URL | Credenciales |
|----------|------------|--------------|
| MySQL | `localhost:3307` | user: `fichajes_dev`, pass: `fichajes_dev`, db: `db_fichajespi_dev` |
| Backend API | `http://localhost:8080` | — |
| Frontend | `http://localhost:4200` | — |
| phpMyAdmin | `http://localhost:8081` | user: `fichajes_dev`, pass: `fichajes_dev` |
| MailHog UI | `http://localhost:8025` | — |
| MailHog SMTP | `localhost:1025` | (usado por Spring Boot para enviar emails) |

## 📋 Resumen de Comandos

| Comando | Qué hace | Cuándo usarlo |
|---------|----------|---------------|
| `pnpm dev:db` | Levanta solo MySQL, phpMyAdmin y MailHog | Desarrollo con IDE |
| `pnpm dev:db:down` | Detiene la base de datos | Al terminar de trabajar |
| `pnpm dev:be` | Levanta solo el backend | Desarrollo backend específico |
| `pnpm dev:fe` | Levanta solo el frontend | Desarrollo frontend específico |
| `pnpm dev:full` | Levanta todo el stack | Desarrollo full-stack |
| `pnpm build` | Compila todo el monorepo | Antes de hacer commit |
| `pnpm test` | Ejecuta todos los tests | Validar cambios |
| `pnpm docker:dev:logs` | Ver logs de Docker | Debugging de BD |

## 🐛 Solución de Problemas

### El puerto 3307 ya está en uso
```bash
# Ver qué proceso usa el puerto
lsof -i :3307

# Detener MySQL local si lo tienes
brew services stop mysql
# o cambiar el puerto en deploy/dev/compose.yaml
```

### No puedo conectar a la base de datos desde Spring Boot
1. Verifica que los contenedores estén corriendo:
   ```bash
   docker ps
   ```
2. Verifica los logs:
   ```bash
   pnpm docker:dev:logs
   ```
3. Espera ~10 segundos a que MySQL termine de inicializarse (healthcheck)
4. Verifica que el perfil sea `dev`: `SPRING_PROFILES_ACTIVE=dev`

### Error "Cannot find module" al ejecutar pnpm dev:fe
```bash
cd apps/fichaje-fe
npm install
```

### Spring Boot no encuentra la clase principal
1. Abre el proyecto `apps/fichaje-be` como proyecto raíz en IntelliJ
2. Asegúrate de tener el plugin de Lombok instalado
3. Haz **File** → **Invalidate Caches / Restart**

### Frontend no conecta con Backend (CORS)
1. Verifica que el backend esté corriendo en `http://localhost:8080`
2. Revisa `application-dev.properties` para configuración de CORS
3. Abre la consola del navegador (F12) para ver errores específicos

### Turborepo no encuentra los paquetes
```bash
# Reinstalar dependencias
pnpm install

# Verificar workspaces
cat pnpm-workspace.yaml
```

## 📁 Estructura del Proyecto

```
/
├── apps/
│   ├── fichaje-be/          # Backend Spring Boot (@fichaje/be)
│   │   ├── src/
│   │   ├── pom.xml
│   │   └── package.json     # Scripts: dev, build, test, db:dev:up/down
│   ├── fichaje-fe/          # Frontend Angular (@fichaje/fe)
│   │   ├── src/
│   │   └── package.json     # Scripts: dev, build, test
│   └── fichaje-desktop/     # Desktop (futuro)
├── packages/
│   ├── infra/               # Scripts de Docker Compose (@fichaje/infra)
│   │   └── package.json     # Scripts: dev:up, dev:down, prod:up, prod:down
│   └── brand/               # Assets compartidos
├── deploy/
│   ├── dev/
│   │   ├── compose.yaml     # Docker Compose para desarrollo
│   │   ├── .env.example     # Plantilla de variables
│   │   └── .env             # Variables locales (git-ignored)
│   └── prod/
│       └── compose.yaml     # Docker Compose para producción
├── package.json             # Scripts del monorepo (dev:db, dev:full, etc.)
├── turbo.json               # Configuración de Turborepo
├── pnpm-workspace.yaml      # Definición de workspaces
└── SETUP.md                 # Este archivo
```

## 🔍 Perfiles de Spring Boot

- **`dev`** (desarrollo local): 
  - BD en `localhost:3307`
  - Logs detallados
  - Hot reload habilitado
  
- **`test`** (tests automáticos):
  - BD temporal en `localhost:3308`
  - Se levanta y destruye automáticamente
  
- **`prod`** (producción):
  - Variables de entorno desde Docker
  - Logs optimizados

## 🚢 Build y Producción

### Build Local
```bash
# Build completo (todos los paquetes)
pnpm build

# Build solo backend (genera .jar)
pnpm turbo run build --filter=@fichaje/be

# Build solo frontend (genera dist/)
pnpm turbo run build --filter=@fichaje/fe
```

### Producción con Docker Compose
```bash
# 1. Configurar variables
cp deploy/prod/.env.example deploy/prod/.env
# Editar deploy/prod/.env con valores reales

# 2. Levantar stack de producción
pnpm docker:prod:up

# 3. Ver logs
pnpm docker:prod:logs

# 4. Detener
pnpm docker:prod:down
```

## 📚 Documentación Adicional

- **Docker**: Ver `DOCKER.md` para detalles de orquestación
- **README**: Ver `README.md` para descripción general del proyecto
- **Variables de entorno**: `deploy/dev/.env.example` y `deploy/prod/.env.example`

## 🤝 Contribuir

1. Clona el repositorio
2. Instala dependencias: `pnpm install`
3. Levanta el entorno: `pnpm dev:db`
4. Crea una rama: `git checkout -b feature/mi-feature`
5. Desarrolla y prueba localmente
6. Asegúrate de que todo compila: `pnpm build`
7. Asegúrate de que los tests pasan: `pnpm test`
8. Haz commit y push
9. Abre un Pull Request

---

¿Tienes dudas? Abre un issue en el repositorio.
