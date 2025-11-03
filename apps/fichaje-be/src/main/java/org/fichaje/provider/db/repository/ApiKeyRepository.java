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
