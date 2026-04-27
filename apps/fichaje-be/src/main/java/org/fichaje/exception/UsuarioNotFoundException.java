package org.fichaje.exception;

public class UsuarioNotFoundException extends RuntimeException {
    public UsuarioNotFoundException(Long id) {
        super("Usuario no encontrado con id: " + id);
    }
    public UsuarioNotFoundException(String numero) { super("Usuario no encontrado con número: " + numero); }
}