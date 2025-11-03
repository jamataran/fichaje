package org.fichaje.service;

import org.fichaje.provider.db.entity.ApiKey;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.repository.ApiKeyRepository;
import org.fichaje.provider.db.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ApiKeyService {

    private static final Logger logger = LoggerFactory.getLogger(ApiKeyService.class);
    private static final int API_KEY_LENGTH = 32; // 32 bytes = 256 bits

    @Autowired
    private ApiKeyRepository apiKeyRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Genera una nueva API Key
     * @return La API Key en texto plano (solo se muestra una vez)
     */
    public String generateApiKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[API_KEY_LENGTH];
        secureRandom.nextBytes(key);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(key);
    }

    /**
     * Crea una nueva API Key para un usuario
     * @param name Nombre descriptivo de la API Key
     * @param description Descripción
     * @param usuarioId ID del usuario
     * @param expiresInDays Días hasta expiración (null = sin expiración)
     * @param createdBy Usuario que crea la API Key
     * @return Un objeto con la API Key en plano y la entidad guardada
     */
    public ApiKeyCreationResult createApiKey(String name, String description, Long usuarioId, 
                                             Integer expiresInDays, String createdBy) {
        // Validaciones de seguridad
        if (name == null || name.trim().isEmpty() || name.length() > 100) {
            throw new IllegalArgumentException("Nombre inválido");
        }
        
        if (description != null && description.length() > 255) {
            throw new IllegalArgumentException("Descripción demasiado larga");
        }
        
        if (expiresInDays != null && expiresInDays < 1) {
            throw new IllegalArgumentException("Los días de expiración deben ser positivos");
        }
        
        // Límite de API Keys por usuario (prevenir abuso)
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        long activeKeysCount = apiKeyRepository.findByUsuarioAndActiveTrue(usuario).size();
        if (activeKeysCount >= 10) {
            throw new IllegalStateException("Límite de API Keys activas alcanzado (máximo 10 por usuario)");
        }

        String plainApiKey = generateApiKey();
        String hashedKey = passwordEncoder.encode(plainApiKey);

        ApiKey apiKey = new ApiKey();
        apiKey.setKeyHash(hashedKey);
        apiKey.setName(name.trim());
        apiKey.setDescription(description != null ? description.trim() : null);
        apiKey.setUsuario(usuario);
        apiKey.setActive(true);
        apiKey.setCreatedBy(createdBy);
        
        if (expiresInDays != null && expiresInDays > 0) {
            apiKey.setExpiresAt(LocalDateTime.now().plusDays(expiresInDays));
        }

        ApiKey saved = apiKeyRepository.save(apiKey);
        // Log seguro sin información sensible
        logger.info("API Key creada: {} para usuario ID: {} por: {}", name, usuarioId, createdBy);

        return new ApiKeyCreationResult(plainApiKey, saved);
    }

    /**
     * Valida una API Key y retorna el usuario asociado si es válida
     * NOTA: Este método es vulnerable a timing attacks. Para producción, considera:
     * 1. Implementar rate limiting
     * 2. Usar un índice en key_hash para búsqueda más eficiente
     * 3. Implementar constant-time comparison si es crítico
     */
    public Optional<Usuario> validateApiKey(String plainApiKey) {
        // Validación básica para prevenir ataques
        if (plainApiKey == null || plainApiKey.length() < 32 || plainApiKey.length() > 100) {
            logger.warn("Intento de validación con API Key de formato inválido");
            return Optional.empty();
        }
        
        List<ApiKey> allKeys = apiKeyRepository.findAll();
        
        for (ApiKey apiKey : allKeys) {
            try {
                if (passwordEncoder.matches(plainApiKey, apiKey.getKeyHash())) {
                    if (apiKey.isValid()) {
                        // Actualizar último uso de forma asíncrona para no bloquear
                        updateLastUsed(apiKey);
                        return Optional.of(apiKey.getUsuario());
                    } else {
                        // No revelar información específica sobre la key
                        logger.warn("Intento de uso de API Key inválida o expirada");
                        return Optional.empty();
                    }
                }
            } catch (Exception e) {
                // Continuar con la siguiente key en caso de error
                logger.error("Error al validar API Key", e);
            }
        }
        
        // Mensaje genérico que no revela información
        logger.warn("Intento de autenticación con API Key inválida");
        return Optional.empty();
    }

    /**
     * Actualiza el timestamp de último uso
     */
    private void updateLastUsed(ApiKey apiKey) {
        apiKey.updateLastUsed();
        apiKeyRepository.save(apiKey);
    }

    /**
     * Desactiva una API Key
     */
    public void deactivateApiKey(Long id) {
        ApiKey apiKey = apiKeyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("API Key no encontrada"));
        apiKey.setActive(false);
        apiKeyRepository.save(apiKey);
        logger.info("API Key desactivada: {}", apiKey.getName());
    }

    /**
     * Activa una API Key
     */
    public void activateApiKey(Long id) {
        ApiKey apiKey = apiKeyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("API Key no encontrada"));
        apiKey.setActive(true);
        apiKeyRepository.save(apiKey);
        logger.info("API Key activada: {}", apiKey.getName());
    }

    /**
     * Elimina una API Key
     */
    public void deleteApiKey(Long id) {
        ApiKey apiKey = apiKeyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("API Key no encontrada"));
        apiKeyRepository.delete(apiKey);
        logger.info("API Key eliminada: {}", apiKey.getName());
    }

    /**
     * Lista todas las API Keys de un usuario
     */
    public List<ApiKey> getApiKeysByUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return apiKeyRepository.findByUsuario(usuario);
    }

    /**
     * Lista todas las API Keys
     */
    public List<ApiKey> getAllApiKeys() {
        return apiKeyRepository.findAll();
    }

    /**
     * Obtiene una API Key por ID
     */
    public Optional<ApiKey> getApiKeyById(Long id) {
        return apiKeyRepository.findById(id);
    }

    /**
     * Clase para retornar el resultado de creación de API Key
     */
    public static class ApiKeyCreationResult {
        private final String plainApiKey; // Solo se muestra una vez
        private final ApiKey apiKey;

        public ApiKeyCreationResult(String plainApiKey, ApiKey apiKey) {
            this.plainApiKey = plainApiKey;
            this.apiKey = apiKey;
        }

        public String getPlainApiKey() {
            return plainApiKey;
        }

        public ApiKey getApiKey() {
            return apiKey;
        }
    }
}
