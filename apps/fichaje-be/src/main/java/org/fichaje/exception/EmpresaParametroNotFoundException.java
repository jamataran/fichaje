package org.fichaje.exception;

public class EmpresaParametroNotFoundException extends RuntimeException {

    public EmpresaParametroNotFoundException(Long id) {
        super("Parámetro no encontrado con id: " + id);
    }
}