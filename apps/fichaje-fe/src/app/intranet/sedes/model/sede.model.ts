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
  latitud?: number | null;
  longitud?: number | null;
  parametros?: SedeParametro[];
}

export interface SedeParametro {
  id: number;
  sedeId: number;
  clave: string;
  valor: string;
}

export interface SedeParametroCreate {
  clave: string;
  valor: string;
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
  latitud?: number | null;
  longitud?: number | null;
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
  latitud?: number | null;
  longitud?: number | null;
}
