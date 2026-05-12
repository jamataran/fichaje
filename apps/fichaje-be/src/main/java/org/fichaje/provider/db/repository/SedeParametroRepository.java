package org.fichaje.provider.db.repository;

import org.fichaje.provider.db.entity.SedeParametro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SedeParametroRepository extends JpaRepository<SedeParametro, Long>, JpaSpecificationExecutor<SedeParametro> {

	List<SedeParametro> findBySedeId(Long sedeId);

	boolean existsBySedeIdAndClave(Long sedeId, String clave);
}