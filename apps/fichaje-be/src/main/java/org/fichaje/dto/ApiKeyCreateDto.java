package org.fichaje.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * DTO para crear una nueva API Key
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyCreateDto {
    
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    
    private String description;
    
    @NotNull(message = "El ID de usuario es obligatorio")
    private Long usuarioId;
    
    // Días hasta expiración (null = sin expiración)
    private Integer expiresInDays;
}
