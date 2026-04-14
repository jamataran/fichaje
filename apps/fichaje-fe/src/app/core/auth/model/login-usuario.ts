export class LoginUsuario {
    numero: string
    password: string
    empresaId: number | null

    constructor(numero: string, password: string, empresaId: number | null) {
        this.numero = numero
        this.password = password
        this.empresaId = empresaId
    }
}
