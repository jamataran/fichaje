package org.fichaje.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de API Keys
 * No expone el hash de la key por seguridad
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyDto {
    
    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    
    private String description;
    
    @NotNull(message = "El ID de usuario es obligatorio")
    private Long usuarioId;
    
    private String usuarioNombre;
    
    private Boolean active;
    
    private LocalDateTime expiresAt;
    
    private LocalDateTime lastUsedAt;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private String createdBy;
    
    // Este campo solo se incluye en la respuesta de creación
    private String plainApiKey;
}
