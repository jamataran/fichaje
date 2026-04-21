export interface Sede {
  id: number;
  nombre: string;
  email: string;
  telefono?: string;
  direccion: string;
  codigoPostal: string;
  localidad: string;
  provincia: string;
  pais: string;
  activa: boolean;
  latitud?: number;
  longitud?: number;
}

export interface SedeCreate {
  nombre: string;
  email: string;
  telefono?: string;
  direccion: string;
  codigoPostal: string;
  localidad: string;
  provincia: string;
  pais: string;
  activa: boolean;
  latitud?: number;
  longitud?: number;
}

export interface SedeUpdate {
  nombre: string;
  email: string;
  telefono?: string;
  direccion: string;
  codigoPostal: string;
  localidad: string;
  provincia: string;
  pais: string;
  activa: boolean;
}
