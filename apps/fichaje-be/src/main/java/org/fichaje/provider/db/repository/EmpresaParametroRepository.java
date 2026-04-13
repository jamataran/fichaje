package org.fichaje.provider.db.repository;

import org.fichaje.provider.db.entity.EmpresaParametro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface EmpresaParametroRepository
		extends JpaRepository<EmpresaParametro, Long>, JpaSpecificationExecutor<EmpresaParametro> {

	List<EmpresaParametro> findByEmpresaId(Long empresaId);

}
