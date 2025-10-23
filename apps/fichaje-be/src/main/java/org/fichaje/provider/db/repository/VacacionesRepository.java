package org.fichaje.provider.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.entity.Vacaciones;

@Repository
public interface VacacionesRepository extends JpaRepository<Vacaciones, Long>,
		JpaSpecificationExecutor<Vacaciones> {

	List<Vacaciones> findByUsuarioAndConsumidasFalseAndAprobadoTrue(
			Usuario usuario);

	List<Vacaciones> findByUsuarioId(Long id);
}
