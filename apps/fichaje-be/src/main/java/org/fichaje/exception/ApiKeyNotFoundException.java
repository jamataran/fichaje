package org.fichaje.exception;

public class ApiKeyNotFoundException extends BusinessException {
    public ApiKeyNotFoundException(Long id) {
        super("No se encontró la API Key con ID: " + id);
    }
}
