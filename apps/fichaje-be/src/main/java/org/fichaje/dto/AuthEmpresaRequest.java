package org.fichaje.dto;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para solicitar autenticación en una empresa específica.
 */
@Schema(description = "Solicitud de token para una empresa específica")
public record AuthEmpresaRequest(
    @NotNull(message = "El ID de la empresa es obligatorio")
    @Schema(description = "ID de la empresa a la que se desea acceder", example = "1")
    Long empresaId
) {}
