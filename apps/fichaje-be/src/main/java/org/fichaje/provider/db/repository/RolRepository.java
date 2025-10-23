package org.fichaje.provider.db.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.fichaje.provider.db.entity.Rol;
import org.fichaje.config.security.enums.RolNombre;

@Repository
public interface RolRepository
		extends JpaRepository<Rol, Long>, JpaSpecificationExecutor<Rol> {
	Optional<Rol> findByRolNombre(RolNombre rolNombre);
}
