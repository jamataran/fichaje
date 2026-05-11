package org.fichaje.dto.entity;

import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para la gestión de calendarios laborales.
 */
@Schema(description = "Datos de un calendario laboral")
public record CalendarioDto(
    Long id,
    
    @Schema(description = "Nombre descriptivo del calendario", example = "Calendario General 2024")
    String nombre,
    
    @Schema(description = "Año al que pertenece el calendario", example = "2024")
    int year,
    
    @Schema(description = "Indica si el calendario es el actual activo")
    boolean active,
    
    @Schema(description = "Minutos de margen permitidos después de la hora de entrada", example = "15")
    int minutosMasEntrada,
    
    @Schema(description = "Minutos de margen permitidos antes de la hora de entrada", example = "15")
    int minutosMenosEntrada,
    
    @Schema(description = "Lista de días laborables configurados")
    List<DiaDto> dias
) {}
