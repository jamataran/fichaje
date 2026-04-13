export class NuevoUsuario {
    numero: string
    nombreEmpleado: string
    email: string
    dni: string
    roles: string[]
    sedeId: number

    constructor(numero: string, nombreEmpleado: string, email: string, dni: string, roles: string[], sedeId: number) {
        this.numero = numero
        this.nombreEmpleado = nombreEmpleado
        this.email = email
        this.roles = roles
        this.dni = dni
        this.sedeId = sedeId
    }

}
