import { Empleado } from "../../empleados/model/empleado";

export interface Permiso {
  id?: number;
  aprobado: boolean | null;
  dia: string;
  descripcion: string;
  horaFin: string;
  horaInicio: string;
  usuario: Empleado | null;
  estado: string;
}
