export class NuevoUsuario {
    numero: string
    nombreEmpleado: string
    email: string
    dni: string
    roles: string[]
    sedeId: number | null

    constructor(numero: string, nombreEmpleado: string, email: string, dni: string, roles: string[], sedeId: number | null = null) {
        this.numero = numero
        this.nombreEmpleado = nombreEmpleado
        this.email = email
        this.roles = roles
        this.dni = dni
        this.sedeId = sedeId
    }

}
