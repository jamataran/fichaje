package org.fichaje.exception;

public class EmpresaNotFoundException extends RuntimeException {

    public EmpresaNotFoundException(Long id) {
        super("Empresa no encontrada con id: " + id);
    }
}