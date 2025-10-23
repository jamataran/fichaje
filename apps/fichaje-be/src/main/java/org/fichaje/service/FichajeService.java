package org.fichaje.service;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import org.fichaje.provider.db.entity.Fichaje;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.repository.FichajeRepository;
import org.fichaje.service.common.CommonServiceImpl;

@Service
@Transactional
public class FichajeService
		extends CommonServiceImpl<Fichaje, FichajeRepository> {

	public List<Fichaje> findByUsuarioAndDia(Usuario usuario, LocalDate dia) {
		return repository.findByUsuarioAndDiaOrderByHora(usuario, dia);
	}

}
