package org.fichaje.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.entity.Vacaciones;
import org.fichaje.provider.db.repository.VacacionesRepository;

@Service
@Transactional
public class VacacionesService
		extends CommonServiceImpl<Vacaciones, VacacionesRepository>{

	public List<Vacaciones> findByUsuarioSinAgotar(Usuario usuario) {
		return repository.findByUsuarioAndConsumidasFalseAndAprobadoTrue(usuario);
	}

	public List<Vacaciones> findByUser(Long id) {
		return repository.findByUsuarioId(id);
	}
}
