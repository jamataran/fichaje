export interface Empresa {
  id: number;
  nombre: string;
  razonSocial?: string;
  cif: string;
  activa: boolean;
  email?: string;
  telefono?: string;
  direccion?: string;
  codigoPostal?: string;
  localidad?: string;
  provincia?: string;
  pais?: string;
  latitud?: number;
  longitud?: number;
  sedes?: SedeEmpresa[];
}

export interface SedeEmpresa {
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

export interface EmpresaCreate {
  nombre: string;
  razonSocial?: string;
  cif: string;
  activa: boolean;
  email: string;
  telefono?: string;
  direccion: string;
  codigoPostal: string;
  localidad: string;
  provincia: string;
  pais: string;
  latitud?: number | null;
  longitud?: number | null;
}

export interface EmpresaUpdate {
  nombre: string;
  razonSocial?: string;
  cif: string;
  activa: boolean;
}
