package org.fichaje.provider.db.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.fichaje.dto.interfaces.ITopIncidencias;
import org.fichaje.dto.interfaces.IUsuarioDtoEstadistica;
import org.fichaje.provider.db.entity.Incidencia;

@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Long>,
		JpaSpecificationExecutor<Incidencia> {

	List<Incidencia> findByUsuarioId(Long id);

	@Query(value = "SELECT COUNT(i.dia) numero FROM incidencias i WHERE MONTH(i.dia)=?1 AND YEAR(i.dia)=?2 AND i.empresa_id=?3", nativeQuery = true)
	Integer countNumberOfIncidenciasOfMonth(int mes, int year, Long empresaId);

	@Query(value = "SELECT COUNT(i.id) cantidad, u.nombre_empleado nombreEmpleado, u.numero id FROM incidencias i JOIN usuarios u ON i.usuario_id=u.id WHERE i.empresa_id=?1 AND i.dia >= DATE_SUB(NOW(), INTERVAL 12 MONTH) GROUP BY i.usuario_id ORDER BY cantidad DESC", nativeQuery = true)
	List<IUsuarioDtoEstadistica> numberOfIncidenciasPerUserLast12Months(Long empresaId);

	@Query(value = "SELECT COUNT(id) cantidad, resumen FROM incidencias WHERE empresa_id=?1 AND dia >= DATE_SUB(NOW(), INTERVAL 12 MONTH) GROUP BY resumen ORDER BY cantidad DESC", nativeQuery = true)
	List<ITopIncidencias> topIncidenciasLast12Months(Long empresaId);

}
