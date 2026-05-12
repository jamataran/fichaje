package org.fichaje.dto;

import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de API Keys
 * No expone el hash de la key por seguridad
 */
public record ApiKeyDto(
    Long id,
    String name,
    String description,
    Long usuarioId,
    String usuarioNombre,
    Boolean active,
    LocalDateTime expiresAt,
    LocalDateTime lastUsedAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdBy,
    String plainApiKey // Solo se incluye en la respuesta de creación
) {
    /**
     * Constructor para convertir desde la entidad convenientemente (sin plainApiKey)
     */
    public static ApiKeyDto fromEntity(org.fichaje.provider.db.entity.ApiKey apiKey) {
        return new ApiKeyDto(
            apiKey.getId(),
            apiKey.getName(),
            apiKey.getDescription(),
            apiKey.getUsuario().getId(),
            apiKey.getUsuario().getNombreEmpleado(),
            apiKey.getActive(),
            apiKey.getExpiresAt(),
            apiKey.getLastUsedAt(),
            apiKey.getCreatedAt(),
            apiKey.getUpdatedAt(),
            apiKey.getCreatedBy(),
            null
        );
    }

    /**
     * Crea un DTO con la API Key en texto plano
     */
    public ApiKeyDto withPlainApiKey(String plainApiKey) {
        return new ApiKeyDto(
            id, name, description, usuarioId, usuarioNombre, active, 
            expiresAt, lastUsedAt, createdAt, updatedAt, createdBy, plainApiKey
        );
    }
}
