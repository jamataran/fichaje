export class Empleado {

  id?: number
  email: string
  numero: string
  nombreEmpleado: string
  dni: string
  diasVacaciones: number | null
  horasGeneradas: number | null
  working: boolean | null
  enVacaciones: boolean | null
  deBaja: boolean | null
  ultimoFichaje: string
  empresas: any[]
  sedes: any[]

  constructor(
    email: string,
    numero: string,
    nombreEmpleado: string,
    dni: string,
    diasVacaciones: number | null,
    horasGeneradas: number | null,
    working: boolean | null,
    enVacaciones: boolean | null,
    deBaja: boolean | null,
    ultimoFichaje: string,
    empresas: any[] = [],
    sedes: any[] = []
  ) {
    this.email = email
    this.numero = numero
    this.nombreEmpleado = nombreEmpleado
    this.dni = dni
    this.diasVacaciones = diasVacaciones
    this.horasGeneradas = horasGeneradas
    this.working = working
    this.enVacaciones = enVacaciones
    this.deBaja = deBaja
    this.ultimoFichaje = ultimoFichaje
    this.empresas = empresas
    this.sedes = sedes
  }

}