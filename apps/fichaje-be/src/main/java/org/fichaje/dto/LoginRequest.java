package org.fichaje.dto;

import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para el inicio de sesión de un usuario.
 */
@Schema(description = "Datos para el inicio de sesión")
public record LoginRequest(
    @NotBlank(message = "El número de empleado es obligatorio")
    @Schema(description = "Número de empleado o identificación", example = "E12345")
    String numero,

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "Contraseña del usuario", example = "password123")
    String password
) {}
