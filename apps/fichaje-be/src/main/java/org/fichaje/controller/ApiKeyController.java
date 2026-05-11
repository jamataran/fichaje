package org.fichaje.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fichaje.dto.ApiKeyCreateDto;
import org.fichaje.dto.ApiKeyDto;
import org.fichaje.dto.entity.Mensaje;
import org.fichaje.service.ApiKeyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para gestión de API Keys.
 * Solo accesible para usuarios con rol RRHH.
 */
@Slf4j
@RestController
@RequestMapping("/apikey")
@CrossOrigin(origins = "${client.url}")
@RequiredArgsConstructor
@Tag(name = "API Key", description = "Endpoints para la gestión de llaves de API")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('RRHH')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una nueva API Key", description = "La API Key en texto plano solo se muestra una vez en esta respuesta.")
    public ApiKeyDto createApiKey(@Valid @RequestBody ApiKeyCreateDto dto, Authentication authentication) {
        log.info("Creando API Key '{}' para usuario ID {} por {}", dto.name(), dto.usuarioId(), authentication.getName());
        return apiKeyService.createApiKey(
                dto.name(),
                dto.description(),
                dto.usuarioId(),
                dto.expiresInDays(),
                authentication.getName()
        );
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('RRHH')")
    @Operation(summary = "Listar todas las API Keys")
    public List<ApiKeyDto> listAllApiKeys() {
        return apiKeyService.getAllApiKeys();
    }

    @GetMapping("/usuario/{usuarioId}")
    @PreAuthorize("hasRole('RRHH')")
    @Operation(summary = "Listar API Keys de un usuario específico")
    public List<ApiKeyDto> listApiKeysByUsuario(@PathVariable Long usuarioId) {
        return apiKeyService.getApiKeysByUsuario(usuarioId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('RRHH')")
    @Operation(summary = "Obtener detalles de una API Key")
    public ResponseEntity<ApiKeyDto> getApiKey(@PathVariable Long id) {
        return apiKeyService.getApiKeyDtoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('RRHH')")
    @Operation(summary = "Desactivar una API Key")
    public Mensaje deactivateApiKey(@PathVariable Long id) {
        apiKeyService.deactivateApiKey(id);
        return new Mensaje("API Key desactivada correctamente");
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasRole('RRHH')")
    @Operation(summary = "Activar una API Key")
    public Mensaje activateApiKey(@PathVariable Long id) {
        apiKeyService.activateApiKey(id);
        return new Mensaje("API Key activada correctamente");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RRHH')")
    @Operation(summary = "Eliminar una API Key")
    public Mensaje deleteApiKey(@PathVariable Long id) {
        apiKeyService.deleteApiKey(id);
        return new Mensaje("API Key eliminada correctamente");
    }
}
