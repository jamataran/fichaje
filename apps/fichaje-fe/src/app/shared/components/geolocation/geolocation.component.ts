import { Component, OnInit, OnDestroy, ViewChild, ElementRef, AfterViewInit, ChangeDetectorRef } from '@angular/core';
import * as L from 'leaflet';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-geolocation',
  templateUrl: './geolocation.component.html',
  styleUrls: ['./geolocation.component.css']
})
export class GeolocationComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('mapContainer', { static: false }) mapContainer!: ElementRef;

  private map!: L.Map;
  private marker!: L.Marker;
  private circle!: L.Circle;
  private mapInitialized = false;
  private destroy$ = new Subject<void>();

  isLoading = true;
  error: string | null = null;
  coordinates: { latitude: number; longitude: number } | null = null;
  accuracy: number | null = null;

  constructor(private cdr: ChangeDetectorRef) {
    // Configurar los iconos de Leaflet
    this.configureLeafletIcons();
  }

  ngOnInit(): void {
    this.getGeolocation();
  }

  ngAfterViewInit(): void {
    // Este hook se ejecuta después de que la vista se haya renderizado
    // Si ya tenemos coordenadas, inicializamos el mapa aquí
    if (this.coordinates && !this.mapInitialized) {
      this.initializeMap();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
    if (this.map) {
      this.map.remove();
    }
  }

  private configureLeafletIcons(): void {
    // Configurar el icono predeterminado de Leaflet
    const iconUrl = 'assets/marker-icon.png';
    const shadowUrl = 'assets/marker-shadow.png';

    const defaultIcon = L.icon({
      iconUrl: iconUrl,
      shadowUrl: shadowUrl,
      iconSize: [25, 41],
      iconAnchor: [12, 41],
      popupAnchor: [1, -34],
      shadowSize: [41, 41]
    });

    L.Marker.prototype.options.icon = defaultIcon;
  }

  private getGeolocation(): void {
    if (!navigator.geolocation) {
      this.error = 'La geolocalización no está disponible en tu navegador';
      this.isLoading = false;
      this.cdr.detectChanges();
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        console.log('Geolocalización obtenida:', position.coords);
        this.coordinates = {
          latitude: position.coords.latitude,
          longitude: position.coords.longitude
        };
        this.accuracy = Math.round(position.coords.accuracy);
        this.isLoading = false;
        this.cdr.detectChanges();

        // Inicializar el mapa después de que Angular detecte los cambios
        setTimeout(() => {
          if (this.mapContainer && !this.mapInitialized) {
            this.initializeMap();
          }
        }, 100);
      },
      (error) => {
        console.error('Error de geolocalización:', error);
        this.handleGeolocationError(error);
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      {
        enableHighAccuracy: true,
        timeout: 15000,
        maximumAge: 0
      }
    );
  }

  private handleGeolocationError(error: GeolocationPositionError): void {
    switch (error.code) {
      case error.PERMISSION_DENIED:
        this.error = 'Permiso denegado. Por favor, habilita la geolocalización en tu navegador.';
        break;
      case error.POSITION_UNAVAILABLE:
        this.error = 'La información de ubicación no está disponible.';
        break;
      case error.TIMEOUT:
        this.error = 'La solicitud de geolocalización tardó demasiado tiempo.';
        break;
      default:
        this.error = 'Ocurrió un error al obtener la geolocalización.';
    }
  }

  private initializeMap(): void {
    if (!this.coordinates || !this.mapContainer || this.mapInitialized) {
      console.warn('No se puede inicializar el mapa', {
        hasCoordinates: !!this.coordinates,
        hasContainer: !!this.mapContainer,
        mapInitialized: this.mapInitialized
      });
      return;
    }

    try {
      console.log('Inicializando mapa en:', this.coordinates);

      // Inicializar el mapa
      this.map = L.map(this.mapContainer.nativeElement).setView(
        [this.coordinates.latitude, this.coordinates.longitude],
        17
      );

      // Agregar la capa de OpenStreetMap
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© OpenStreetMap contributors',
        maxZoom: 19
      }).addTo(this.map);

      // Agregar marcador en la ubicación actual
      this.marker = L.marker([this.coordinates.latitude, this.coordinates.longitude], {
        title: 'Tu ubicación actual'
      }).addTo(this.map);

      this.marker.bindPopup(
        `<div class="popup-content">
          <strong>Tu ubicación</strong><br>
          Latitud: ${this.coordinates.latitude.toFixed(6)}<br>
          Longitud: ${this.coordinates.longitude.toFixed(6)}<br>
          Precisión: ±${this.accuracy}m
        </div>`
      );

      // Agregar círculo de precisión
      if (this.accuracy) {
        this.circle = L.circle(
          [this.coordinates.latitude, this.coordinates.longitude],
          {
            radius: this.accuracy,
            color: '#0ea5e9',
            weight: 2,
            opacity: 0.3,
            fill: true,
            fillColor: '#0ea5e9',
            fillOpacity: 0.1
          }
        ).addTo(this.map);

        // Ajustar el zoom para que se vea el círculo
        this.map.fitBounds(this.circle.getBounds(), { padding: [50, 50] });
      }

      // Forzar que el mapa se redimensione correctamente
      setTimeout(() => {
        this.map.invalidateSize();
      }, 300);

      this.mapInitialized = true;
      console.log('Mapa inicializado correctamente');
    } catch (err) {
      console.error('Error al inicializar el mapa:', err);
      this.error = 'Error al cargar el mapa. Por favor, recarga la página.';
    }
  }
}
