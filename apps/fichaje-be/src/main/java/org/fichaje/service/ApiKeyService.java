package org.fichaje.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fichaje.dto.ApiKeyDto;
import org.fichaje.exception.ApiKeyNotFoundException;
import org.fichaje.provider.db.entity.ApiKey;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.repository.ApiKeyRepository;
import org.fichaje.provider.db.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ApiKeyService {

    private static final int API_KEY_LENGTH = 32;
    private static final int MAX_KEYS_PER_USER = 10;

    private final ApiKeyRepository apiKeyRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public String generateApiKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[API_KEY_LENGTH];
        secureRandom.nextBytes(key);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(key);
    }

    public ApiKeyDto createApiKey(String name, String description, Long usuarioId, 
                                 Integer expiresInDays, String createdBy) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId));
        
        long activeKeysCount = apiKeyRepository.findByUsuarioAndActiveTrue(usuario).size();
        if (activeKeysCount >= MAX_KEYS_PER_USER) {
            throw new IllegalStateException("Límite de API Keys activas alcanzado (máximo " + MAX_KEYS_PER_USER + " por usuario)");
        }

        String plainApiKey = generateApiKey();
        String hashedKey = passwordEncoder.encode(plainApiKey);

        ApiKey apiKey = ApiKey.builder()
                .keyHash(hashedKey)
                .name(name.trim())
                .description(description != null ? description.trim() : null)
                .usuario(usuario)
                .empresa(usuario.getEmpresas().stream().findFirst()
                        .orElseThrow(() -> new IllegalStateException("El usuario no pertenece a ninguna empresa")))
                .active(true)
                .createdBy(createdBy)
                .expiresAt(expiresInDays != null && expiresInDays > 0 ? LocalDateTime.now().plusDays(expiresInDays) : null)
                .build();

        ApiKey saved = apiKeyRepository.save(apiKey);
        log.info("API Key '{}' creada para usuario ID: {} por: {}", name, usuarioId, createdBy);

        return ApiKeyDto.fromEntity(saved).withPlainApiKey(plainApiKey);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> validateApiKey(String plainApiKey) {
        if (plainApiKey == null || plainApiKey.length() < 32 || plainApiKey.length() > 100) {
            return Optional.empty();
        }
        
        // Optimización: Solo buscamos keys activas y no expiradas directamente en la DB.
        // Aun así, tenemos que iterar y comparar hashes porque BCrypt no es buscable en DB.
        List<ApiKey> candidates = apiKeyRepository.findActiveAndNotExpired(LocalDateTime.now());
        
        return candidates.stream()
                .filter(apiKey -> passwordEncoder.matches(plainApiKey, apiKey.getKeyHash()))
                .findFirst()
                .map(apiKey -> {
                    // Actualizar último uso (requiere transacción de escritura, pero validateApiKey es readOnly habitualmente)
                    // Podríamos hacerlo asíncrono si fuera crítico el rendimiento.
                    updateLastUsed(apiKey.getId());
                    return apiKey.getUsuario();
                });
    }

    // Método separado para actualizar el último uso sin bloquear la validación
    public void updateLastUsed(Long apiKeyId) {
        apiKeyRepository.findById(apiKeyId).ifPresent(apiKey -> {
            apiKey.updateLastUsed();
            apiKeyRepository.save(apiKey);
        });
    }

    public void deactivateApiKey(Long id) {
        ApiKey apiKey = findByIdOrThrow(id);
        apiKey.setActive(false);
        apiKeyRepository.save(apiKey);
        log.info("API Key '{}' desactivada", apiKey.getName());
    }

    public void activateApiKey(Long id) {
        ApiKey apiKey = findByIdOrThrow(id);
        apiKey.setActive(true);
        apiKeyRepository.save(apiKey);
        log.info("API Key '{}' activada", apiKey.getName());
    }

    public void deleteApiKey(Long id) {
        ApiKey apiKey = findByIdOrThrow(id);
        apiKeyRepository.delete(apiKey);
        log.info("API Key '{}' eliminada", apiKey.getName());
    }

    @Transactional(readOnly = true)
    public List<ApiKeyDto> getApiKeysByUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return apiKeyRepository.findByUsuario(usuario).stream()
                .map(ApiKeyDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ApiKeyDto> getAllApiKeys() {
        return apiKeyRepository.findAll().stream()
                .map(ApiKeyDto::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public Optional<ApiKeyDto> getApiKeyDtoById(Long id) {
        return apiKeyRepository.findById(id).map(ApiKeyDto::fromEntity);
    }

    private ApiKey findByIdOrThrow(Long id) {
        return apiKeyRepository.findById(id)
                .orElseThrow(() -> new ApiKeyNotFoundException(id));
    }
}
