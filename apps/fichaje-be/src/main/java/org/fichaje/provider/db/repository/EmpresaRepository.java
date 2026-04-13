package org.fichaje.provider.db.repository;

import org.fichaje.provider.db.entity.Empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmpresaRepository
		extends JpaRepository<Empresa, Long>, JpaSpecificationExecutor<Empresa> {

	@EntityGraph(attributePaths = {"sedes", "parametros"})
	Optional<Empresa> findById(Long id);

	@EntityGraph(attributePaths = {"sedes", "parametros"})
	Page<Empresa> findAll(Pageable pageable);
}
