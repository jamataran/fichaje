package org.fichaje.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import org.fichaje.provider.db.entity.Rol;
import org.fichaje.config.security.enums.RolNombre;
import org.fichaje.provider.db.repository.RolRepository;

@Service
@Transactional
public class RolService extends CommonServiceImpl<Rol, RolRepository> {

	public Optional<Rol> findByRolNombre(RolNombre rolNombre) {
		return repository.findByRolNombre(rolNombre);
	}

}
