package org.fichaje.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO que contiene el token JWT de respuesta.
 */
@Schema(description = "Respuesta que contiene el token JWT")
public record JwtResponse(
    @Schema(description = "Token JWT generado", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String token
) {}
