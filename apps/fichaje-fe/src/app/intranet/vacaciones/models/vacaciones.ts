import { Empleado } from "../../empleados/model/empleado";

export interface Vacaciones {
  id?: number;
  consumidas: boolean | null;
  aprobado: boolean | null;
  estado: string;
  inicio: string;
  fin: string;
  usuario: Empleado | null;
}
