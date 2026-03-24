package org.fichaje.exception;

public class SedeParametroNotFoundException extends RuntimeException {

    public SedeParametroNotFoundException(Long id) {
        super("Parámetro de sede no encontrado con id: " + id);
    }
}