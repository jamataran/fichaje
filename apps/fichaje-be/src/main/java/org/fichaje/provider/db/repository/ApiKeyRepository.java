package org.fichaje.provider.db.repository;

import org.fichaje.provider.db.entity.ApiKey;
import org.fichaje.provider.db.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    /**
     * Buscar API Keys activas
     */
    List<ApiKey> findByActiveTrue();

    /**
     * Buscar API Keys activas que no han expirado
     */
    @org.springframework.data.jpa.repository.Query("SELECT a FROM ApiKey a WHERE a.active = true AND (a.expiresAt IS NULL OR a.expiresAt > :now)")
    List<ApiKey> findActiveAndNotExpired(java.time.LocalDateTime now);

    /**
     * Buscar API Key por su hash
     */
    Optional<ApiKey> findByKeyHash(String keyHash);

    /**
     * Buscar todas las API Keys de un usuario
     */
    List<ApiKey> findByUsuario(Usuario usuario);

    /**
     * Buscar API Keys activas de un usuario
     */
    List<ApiKey> findByUsuarioAndActiveTrue(Usuario usuario);

    /**
     * Verificar si existe una API Key con ese hash
     */
    boolean existsByKeyHash(String keyHash);

    /**
     * Buscar por nombre
     */
    Optional<ApiKey> findByName(String name);
}
