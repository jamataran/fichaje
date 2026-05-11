package org.fichaje.provider.db.specifications;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import org.fichaje.provider.db.entity.Incidencia;
import org.fichaje.provider.db.specifications.common.CommonSpecificationImpl;
import org.fichaje.provider.db.specifications.common.SpecificationHelper;

@Component
public final class IncidenciaSpecifications extends CommonSpecificationImpl<Incidencia> {

	public Specification<Incidencia> diaDesde(LocalDate dia) {
		return (root, query, builder) -> builder
				.greaterThanOrEqualTo(root.get("dia"), dia);
	}

	public Specification<Incidencia> diaHasta(LocalDate dia) {
		return (root, query, builder) -> builder
				.lessThanOrEqualTo(root.get("dia"), dia);
	}

	public Specification<Incidencia> isResuelta(Boolean b) {
		return (root, query, builder) -> builder
				.equal(root.get("resuelta"), b);
	}

	public Specification<Incidencia> explicacionContains(
			String expression) {
		return (root, query, builder) -> builder
				.like(root.get("explicacion"), SpecificationHelper.contains(expression));
	}

	public Specification<Incidencia> resumenContains(
			String expression) {
		return (root, query, builder) -> builder
				.like(root.get("resumen"), SpecificationHelper.contains(expression));
	}

	public Specification<Incidencia> getFilter(org.fichaje.dto.entity.IncidenciaDtoFilter dto) {
		return (root, query, cb) -> {
			Specification<Incidencia> spec = Specification.where((Specification<Incidencia>) null);

			if (dto.getUsuarioNombre() != null) {
				spec = spec.and(nombreUsuarioContains(dto.getUsuarioNombre()));
			}
			if (dto.getUsuarioEmail() != null) {
				spec = spec.and(emailUsuarioContains(dto.getUsuarioEmail()));
			}
			if (dto.getUsuarioNumero() != null) {
				spec = spec.and(numeroUsuarioContains(dto.getUsuarioNumero()));
			}
			if (dto.getUsuarioDni() != null) {
				spec = spec.and(dniUsuarioContains(dto.getUsuarioDni()));
			}
			if (dto.getExplicacion() != null) {
				spec = spec.and(explicacionContains(dto.getExplicacion()));
			}
			if (dto.getResumen() != null) {
				spec = spec.and(resumenContains(dto.getResumen()));
			}
			if (dto.getResuelta() != null) {
				spec = spec.and(isResuelta(dto.getResuelta()));
			}
			if (dto.getDiaDesde() != null) {
				spec = spec.and(diaDesde(dto.getDiaDesde()));
			}
			if (dto.getDiaHasta() != null) {
				spec = spec.and(diaHasta(dto.getDiaHasta()));
			}

			return spec.toPredicate(root, query, cb);
		};
	}

}
