package org.fichaje.controller;

import org.fichaje.dto.ApiKeyCreateDto;
import org.fichaje.dto.ApiKeyDto;
import org.fichaje.dto.entity.Mensaje;
import org.fichaje.provider.db.entity.ApiKey;
import org.fichaje.service.ApiKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para gestión de API Keys
 * Solo accesible para usuarios con rol RRHH
 */
@RestController
@RequestMapping("/apikey")
// TODO: Configurar CORS de forma segura en producción, limitando a dominios específicos
// @CrossOrigin(origins = {"https://tudominio.com", "https://app.tudominio.com"})
@CrossOrigin(origins = "${client.url}") // Usar configuración desde properties
public class ApiKeyController {

    private static final Logger logger = LoggerFactory.getLogger(ApiKeyController.class);

    @Autowired
    private ApiKeyService apiKeyService;

    /**
     * Crear una nueva API Key
     * IMPORTANTE: La API Key en texto plano solo se muestra en esta respuesta
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('RRHH')")
    public ResponseEntity<?> createApiKey(@Valid @RequestBody ApiKeyCreateDto dto, 
                                          Authentication authentication) {
        try {
            String createdBy = authentication.getName();
            
            ApiKeyService.ApiKeyCreationResult result = apiKeyService.createApiKey(
                    dto.getName(),
                    dto.getDescription(),
                    dto.getUsuarioId(),
                    dto.getExpiresInDays(),
                    createdBy
            );

            ApiKeyDto response = convertToDto(result.getApiKey());
            response.setPlainApiKey(result.getPlainApiKey());

            logger.info("API Key creada por {}: {}", createdBy, dto.getName());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Errores de validación - es seguro mostrar el mensaje
            logger.warn("Validación fallida al crear API Key: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new Mensaje(e.getMessage()));
        } catch (Exception e) {
            // Errores inesperados - NO exponer detalles internos
            logger.error("Error inesperado al crear API Key", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Mensaje("Error al crear API Key"));
        }
    }

    /**
     * Listar todas las API Keys
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('RRHH')")
    public ResponseEntity<List<ApiKeyDto>> listAllApiKeys() {
        try {
            List<ApiKey> apiKeys = apiKeyService.getAllApiKeys();
            List<ApiKeyDto> dtos = apiKeys.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Error al listar API Keys: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Listar API Keys de un usuario específico
     */
    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('RRHH')")
    public ResponseEntity<List<ApiKeyDto>> listApiKeysByUsuario(@PathVariable Long usuarioId) {
        try {
            List<ApiKey> apiKeys = apiKeyService.getApiKeysByUsuario(usuarioId);
            List<ApiKeyDto> dtos = apiKeys.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            logger.error("Error al listar API Keys del usuario: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    /**
     * Obtener detalles de una API Key
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('RRHH')")
    public ResponseEntity<?> getApiKey(@PathVariable Long id) {
        try {
            return apiKeyService.getApiKeyById(id)
                    .map(apiKey -> ResponseEntity.ok(convertToDto(apiKey)))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Error al obtener API Key: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new Mensaje("Error al obtener API Key"));
        }
    }

    /**
     * Desactivar una API Key
     */
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('RRHH')")
    public ResponseEntity<?> deactivateApiKey(@PathVariable Long id) {
        try {
            apiKeyService.deactivateApiKey(id);
            return ResponseEntity.ok(new Mensaje("API Key desactivada correctamente"));
        } catch (Exception e) {
            logger.error("Error al desactivar API Key: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new Mensaje("Error al desactivar API Key"));
        }
    }

    /**
     * Activar una API Key
     */
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('RRHH')")
    public ResponseEntity<?> activateApiKey(@PathVariable Long id) {
        try {
            apiKeyService.activateApiKey(id);
            return ResponseEntity.ok(new Mensaje("API Key activada correctamente"));
        } catch (Exception e) {
            logger.error("Error al activar API Key: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new Mensaje("Error al activar API Key"));
        }
    }

    /**
     * Eliminar una API Key
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RRHH')")
    public ResponseEntity<?> deleteApiKey(@PathVariable Long id) {
        try {
            apiKeyService.deleteApiKey(id);
            return ResponseEntity.ok(new Mensaje("API Key eliminada correctamente"));
        } catch (Exception e) {
            logger.error("Error al eliminar API Key: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new Mensaje("Error al eliminar API Key"));
        }
    }

    /**
     * Convierte una entidad ApiKey a DTO (sin exponer el hash)
     */
    private ApiKeyDto convertToDto(ApiKey apiKey) {
        ApiKeyDto dto = new ApiKeyDto();
        dto.setId(apiKey.getId());
        dto.setName(apiKey.getName());
        dto.setDescription(apiKey.getDescription());
        dto.setUsuarioId(apiKey.getUsuario().getId());
        dto.setUsuarioNombre(apiKey.getUsuario().getNombreEmpleado());
        dto.setActive(apiKey.getActive());
        dto.setExpiresAt(apiKey.getExpiresAt());
        dto.setLastUsedAt(apiKey.getLastUsedAt());
        dto.setCreatedAt(apiKey.getCreatedAt());
        dto.setUpdatedAt(apiKey.getUpdatedAt());
        dto.setCreatedBy(apiKey.getCreatedBy());
        return dto;
    }
}
