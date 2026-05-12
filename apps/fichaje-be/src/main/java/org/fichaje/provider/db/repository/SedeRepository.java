package org.fichaje.provider.db.repository;

import org.fichaje.provider.db.entity.Sede;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SedeRepository extends JpaRepository<Sede, Long>, JpaSpecificationExecutor<Sede> {

	List<Sede> findByEmpresaId(Long empresaId);
}