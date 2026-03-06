package org.fichaje.service;

import org.fichaje.provider.db.entity.Empresa;
import org.fichaje.provider.db.repository.EmpresaRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class EmpresaService
		extends CommonServiceImpl<Empresa, EmpresaRepository> {

	public Optional<Empresa> findByCif(String cif) {
		return repository.findByCif(cif);
	}

	public Optional<Empresa> findByActiva(Boolean activa) {
		return repository.findByActiva(activa);
	}

}
