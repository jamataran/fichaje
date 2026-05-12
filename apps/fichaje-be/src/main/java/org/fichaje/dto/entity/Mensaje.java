package org.fichaje.dto.entity;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO genérico para enviar mensajes de respuesta.
 */
@Schema(description = "Respuesta genérica con un mensaje")
public record Mensaje(
    @Schema(description = "Contenido del mensaje", example = "Operación realizada con éxito")
    String mensaje
) {}
