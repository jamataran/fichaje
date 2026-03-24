package org.fichaje.exception;

public class SedeNotFoundException extends RuntimeException {

    public SedeNotFoundException(Long id) {
        super("Sede no encontrada con id: " + id);
    }
}