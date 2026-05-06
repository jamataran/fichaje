package org.fichaje.provider.db.specifications;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import org.fichaje.dto.entity.PermisoDtoFilter;
import org.fichaje.provider.db.entity.Permiso;
import org.fichaje.provider.db.specifications.common.CommonSpecificationImpl;
import org.fichaje.provider.db.specifications.common.SpecificationHelper;

@Component
public final class PermisoSpecifications extends CommonSpecificationImpl<Permiso> {

	public Specification<Permiso> buildSpecification(PermisoDtoFilter dto, Long currentEmpresaId, boolean isSuperAdmin) {
		Specification<Permiso> spec = (root, query, cb) -> cb.conjunction();

		if (dto.getUsuarioNombre() != null)
			spec = spec.and(nombreUsuarioContains(dto.getUsuarioNombre()));
		if (dto.getUsuarioNumero() != null)
			spec = spec.and(numeroUsuarioContains(dto.getUsuarioNumero()));
		if (dto.getUsuarioDni() != null)
			spec = spec.and(dniUsuarioContains(dto.getUsuarioDni()));
		if (dto.getUsuarioEmail() != null)
			spec = spec.and(emailUsuarioContains(dto.getUsuarioEmail()));
		if (dto.getDiaDesde() != null)
			spec = spec.and(diaDesde(dto.getDiaDesde()));
		if (dto.getDiaHasta() != null)
			spec = spec.and(diaHasta(dto.getDiaHasta()));
		if (dto.getHoraInicioDesde() != null)
			spec = spec.and(horaInicioDesde(dto.getHoraInicioDesde()));
		if (dto.getHoraInicioHasta() != null)
			spec = spec.and(horaInicioHasta(dto.getHoraInicioHasta()));
		if (dto.getHoraFinDesde() != null)
			spec = spec.and(horaFinDesde(dto.getHoraFinDesde()));
		if (dto.getHoraFinHasta() != null)
			spec = spec.and(horaFinHasta(dto.getHoraFinHasta()));
		if (dto.getDescripcion() != null)
			spec = spec.and(descripcionContains(dto.getDescripcion()));
		if (dto.getEstado() != null)
			spec = spec.and(estadoContains(dto.getEstado()));

		// Aislamiento Multi-empresa
		if (currentEmpresaId != null) {
			spec = spec.and(hasEmpresa(currentEmpresaId));
		}

		return spec;
	}

	public Specification<Permiso> diaDesde(LocalDate dia) {
		return (root, query, builder) -> builder
				.greaterThanOrEqualTo(root.get("dia"), dia);
	}

	public Specification<Permiso> diaHasta(LocalDate dia) {
		return (root, query, builder) -> builder
				.lessThanOrEqualTo(root.get("dia"), dia);
	}

	public Specification<Permiso> horaInicioDesde(LocalTime time) {
		return (root, query, builder) -> builder
				.greaterThanOrEqualTo(root.get("horaInicio"), time);
	}

	public Specification<Permiso> horaInicioHasta(LocalTime time) {
		return (root, query, builder) -> builder
				.lessThanOrEqualTo(root.get("horaInicio"), time);
	}

	public Specification<Permiso> horaFinDesde(LocalTime time) {
		return (root, query, builder) -> builder
				.greaterThanOrEqualTo(root.get("horaFin"), time);
	}

	public Specification<Permiso> horaFinHasta(LocalTime time) {
		return (root, query, builder) -> builder
				.lessThanOrEqualTo(root.get("horaFin"), time);
	}

	public Specification<Permiso> descripcionContains(String expression) {
		return (root, query, builder) -> builder
				.like(root.get("descripcion"), SpecificationHelper.contains(expression));
	}

	public Specification<Permiso> estadoContains(String expression) {
		return (root, query, builder) -> builder
				.like(root.get("estado"), SpecificationHelper.contains(expression));
	}

//	public Specification<Permiso> isAprobada(Boolean b) {
//		return (root, query, builder) -> builder
//				.equal(root.get("aprobado"), b);
//	}

}
