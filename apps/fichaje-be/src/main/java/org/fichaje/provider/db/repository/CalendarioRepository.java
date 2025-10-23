package org.fichaje.provider.db.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import org.fichaje.provider.db.entity.Calendario;

@Repository
public interface CalendarioRepository
		extends JpaRepository<Calendario, Long>, JpaSpecificationExecutor<Calendario> {
	Optional<Calendario> findByNombre(String nombre);

	Optional<Calendario> findById(Long id);

//	@Query("SELECT c FROM Calendario c WHERE c.active = TRUE")
//	Optional<Calendario> getActive();

	Optional<Calendario> findByActiveTrue();
}
