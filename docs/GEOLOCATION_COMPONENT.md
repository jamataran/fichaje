# Componente de Geolocalización

## Descripción General
Componente Angular para mostrar la ubicación actual del usuario en un mapa interactivo usando Leaflet y OpenStreetMap (OSS).

## Ubicación
```
/apps/fichaje-fe/src/app/shared/components/geolocation/
├── geolocation.component.ts
├── geolocation.component.html
└── geolocation.component.css
```

## Características

### ✅ Implementado
- **Geolocalización del navegador**: Obtiene la ubicación actual del usuario de forma segura
- **Mapa interactivo**: Utiliza Leaflet + OpenStreetMap (totalmente OSS, sin API keys)
- **Indicador de precisión**: Muestra un círculo que representa el margen de error de la ubicación
- **Información en tiempo real**: Coordenadas (lat/lon) y precisión (metros)
- **Responsivo**: Se adapta perfectamente a móvil, tablet y desktop
- **Estados de carga**: Spinner mientras se obtiene la ubicación
- **Manejo de errores**: Mensajes claros si no hay permisos o falla la geolocalización
- **Limpieza de recursos**: Destruye correctamente el mapa al desmontar el componente

### 🔄 Evoluciones Futuras
- Emisión de eventos cuando se obtiene la ubicación
- Recepción de parámetros de entrada (zoom inicial, centro del mapa, etc.)
- Historial de ubicaciones
- Integración con servicios de backend
- Actualización en tiempo real (polling)

## Instalación de Dependencias

Ya están instaladas en el proyecto:
```bash
npm install leaflet @types/leaflet@1.9.8 --legacy-peer-deps
```

## Integración en el Proyecto

### 1. **Shared Module** (ya configurado)
El componente está declarado en `SharedModule`:
```typescript
// apps/fichaje-fe/src/app/shared/shared.module.ts
declarations: [..., GeolocationComponent],
exports: [..., GeolocationComponent],
```

### 2. **Home Component** (ya integrado)
El componente se usa en el home:
```html
<!-- apps/fichaje-fe/src/app/intranet/home/containers/home/home.component.html -->
<app-geolocation></app-geolocation>
```

### 3. **Estilos Globales** (ya configurado)
CSS de Leaflet importado en styles.css:
```css
@import "~leaflet/dist/leaflet.css";
```

### 4. **Assets** (ya copiados)
Los iconos de Leaflet se encuentran en:
```
/apps/fichaje-fe/src/assets/
├── marker-icon.png
├── marker-icon-2x.png
├── marker-shadow.png
└── ...
```

## Uso Actual

El componente se muestra en el dashboard del home como una tarjeta con:
- Mapa de 300-400px de altura (responsivo)
- Información de ubicación en badges en la esquina inferior
- Manejo automático de permisos

```html
<div class="card card-geolocation">
  <div class="card-header">
    <h5 class="card-title">
      <i class="bi bi-geo-alt me-2"></i>Tu Ubicación
    </h5>
  </div>
  <div class="card-body geolocation-body">
    <app-geolocation></app-geolocation>
  </div>
</div>
```

## Responsividad

- **Móvil (< 480px)**: Mapa compacto, badges apilados verticalmente
- **Tablet (480px - 768px)**: Mapa mediano, badges en disposición adaptada
- **Desktop (> 768px)**: Mapa completo, badges en fila

## Consideraciones de Privacidad

⚠️ **Importante**: El navegador solicitará permiso al usuario la primera vez que acceda a la página.

- Solo se obtiene la ubicación una vez (sin actualización en tiempo real)
- Se usa HTTPS en producción (requerido por el navegador)
- Los datos no se envían a ningún servidor externo (solo OSS local)

## Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|-----------|---------|----------|
| Leaflet | ^1.9.x | Librería de mapas OSS |
| OpenStreetMap | - | Proveedor de mapas gratuito |
| TypeScript | ~4.4.4 | Tipado seguro |
| Angular | ~13.0.3 | Framework |

## Configuración por Defecto

```typescript
{
  enableHighAccuracy: true,    // Máxima precisión
  timeout: 10000,              // 10 segundos de timeout
  maximumAge: 0                // No usar caché
}
```

## Estructura del Componente

```
GeolocationComponent
├── Properties
│   ├── isLoading: boolean
│   ├── error: string | null
│   ├── coordinates: { latitude, longitude }
│   └── accuracy: number | null
├── Private Methods
│   ├── configureLeafletIcons()
│   ├── getGeolocation()
│   ├── handleGeolocationError()
│   └── initializeMap()
└── Lifecycle Hooks
    ├── ngOnInit()
    └── ngOnDestroy()
```

## Ejemplo de Salida

El componente mostrará:
- Un mapa interactivo centrado en la ubicación del usuario
- Un marcador azul en el centro
- Un círculo de precisión (margen de error)
- Badges con información: Lat/Lon y Precisión

## Próximos Pasos (Cuando sea Necesario)

1. **Envío al backend**: Crear servicio para guardar geolocalización
2. **Eventos**: Emitir `@Output()` con la ubicación
3. **Parámetros**: Aceptar `@Input()` para configuración del mapa
4. **Historial**: Guardar ubicaciones previas en el navegador (localStorage)
5. **Geofencing**: Validar si el usuario está dentro de una zona permitida

---

**Estado**: ✅ Completado y funcional
**Última actualización**: 2025-11-20

