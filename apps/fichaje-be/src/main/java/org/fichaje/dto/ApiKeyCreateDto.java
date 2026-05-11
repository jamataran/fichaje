package org.fichaje.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO para crear una nueva API Key
 */
public record ApiKeyCreateDto(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    String name,
    
    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    String description,
    
    @NotNull(message = "El ID de usuario es obligatorio")
    Long usuarioId,
    
    @Positive(message = "Los días de expiración deben ser positivos")
    Integer expiresInDays
) {}
