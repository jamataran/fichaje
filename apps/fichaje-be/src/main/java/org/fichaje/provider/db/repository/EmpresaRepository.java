package org.fichaje.provider.db.repository;

import org.fichaje.provider.db.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository
		extends JpaRepository<Empresa, Long>, JpaSpecificationExecutor<Empresa> {

	Optional<Empresa> findByCif(String cif);

	Optional<Empresa> findByActiva(Boolean activa);
}
