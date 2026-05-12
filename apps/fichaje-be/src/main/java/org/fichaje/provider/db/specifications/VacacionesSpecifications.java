package org.fichaje.provider.db.specifications;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import org.fichaje.dto.entity.VacacionesDtoFilter;
import org.fichaje.provider.db.entity.Vacaciones;
import org.fichaje.provider.db.specifications.common.CommonSpecificationImpl;
import org.fichaje.provider.db.specifications.common.SpecificationHelper;

@Component
public final class VacacionesSpecifications extends CommonSpecificationImpl<Vacaciones> {

	public Specification<Vacaciones> buildSpecification(VacacionesDtoFilter dto, Long currentEmpresaId, boolean isSuperAdmin) {
		Specification<Vacaciones> spec = (root, query, cb) -> cb.conjunction();

		if (dto.getUsuarioNombre() != null)
			spec = spec.and(nombreUsuarioContains(dto.getUsuarioNombre()));
		if (dto.getUsuarioNumero() != null)
			spec = spec.and(numeroUsuarioContains(dto.getUsuarioNumero()));
		if (dto.getUsuarioDni() != null)
			spec = spec.and(dniUsuarioContains(dto.getUsuarioDni()));
		if (dto.getUsuarioEmail() != null)
			spec = spec.and(emailUsuarioContains(dto.getUsuarioEmail()));
		if (dto.getConsumidas() != null)
			spec = spec.and(areConsumidas(dto.getConsumidas()));
		if (dto.getAprobado() != null)
			spec = spec.and(areAprobadas(dto.getAprobado()));
		if (dto.getEstado() != null)
			spec = spec.and(estadoContains(dto.getEstado()));
		if (dto.getInicioDesde() != null)
			spec = spec.and(inicioDesde(dto.getInicioDesde()));
		if (dto.getInicioHasta() != null)
			spec = spec.and(inicioHasta(dto.getInicioHasta()));
		if (dto.getFinDesde() != null)
			spec = spec.and(finDesde(dto.getFinDesde()));
		if (dto.getFinHasta() != null)
			spec = spec.and(finHasta(dto.getFinHasta()));

		// Aislamiento Multi-empresa
		if (currentEmpresaId != null) {
			spec = spec.and(hasEmpresa(currentEmpresaId));
		}

		return spec;
	}

	public Specification<Vacaciones> inicioDesde(LocalDate dia) {
		return (root, query, builder) -> builder
				.greaterThanOrEqualTo(root.get("inicio"), dia);
	}

	public Specification<Vacaciones> inicioHasta(LocalDate dia) {
		return (root, query, builder) -> builder
				.lessThanOrEqualTo(root.get("inicio"), dia);
	}

	public Specification<Vacaciones> finDesde(LocalDate dia) {
		return (root, query, builder) -> builder
				.greaterThanOrEqualTo(root.get("fin"), dia);
	}

	public Specification<Vacaciones> finHasta(LocalDate dia) {
		return (root, query, builder) -> builder
				.lessThanOrEqualTo(root.get("fin"), dia);
	}

	public Specification<Vacaciones> areConsumidas(Boolean b) {
		return (root, query, builder) -> builder
				.equal(root.get("consumidas"), b);
	}

	public Specification<Vacaciones> areAprobadas(Boolean b) {
		return (root, query, builder) -> builder
				.equal(root.get("aprobado"), b);
	}

	public Specification<Vacaciones> estadoContains(String expression) {
		return (root, query, builder) -> builder
				.like(root.get("estado"), SpecificationHelper.contains(expression));
	}
}
