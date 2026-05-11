package org.fichaje.dto.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para representar un día laborable en un calendario.
 */
@Schema(description = "Datos de un día laborable")
public record DiaDto(
    Long id,
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Fecha del día", example = "2024-01-01")
    LocalDate dia,
    
    @JsonFormat(pattern = "HH:mm")
    @Schema(description = "Hora de inicio de la jornada", example = "08:00")
    LocalTime horaInicio,
    
    @JsonFormat(pattern = "HH:mm")
    @Schema(description = "Hora de fin de la jornada", example = "17:00")
    LocalTime horaFin,
    
    @Schema(description = "Nombre del calendario asociado")
    String calendarioNombre
) {}
