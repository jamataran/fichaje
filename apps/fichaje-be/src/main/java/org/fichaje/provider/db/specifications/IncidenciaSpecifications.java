package org.fichaje.provider.db.specifications;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import org.fichaje.dto.entity.IncidenciaDtoFilter;
import org.fichaje.provider.db.entity.Incidencia;
import org.fichaje.provider.db.specifications.common.CommonSpecificationImpl;
import org.fichaje.provider.db.specifications.common.SpecificationHelper;

@Component
public final class IncidenciaSpecifications extends CommonSpecificationImpl<Incidencia> {

	public Specification<Incidencia> buildSpecification(IncidenciaDtoFilter dto, Long currentEmpresaId, boolean isSuperAdmin) {
		Specification<Incidencia> spec = (root, query, cb) -> cb.conjunction();

		if (dto != null) {
			if (dto.getUsuarioNombre() != null && !dto.getUsuarioNombre().isEmpty()) {
				spec = spec.and(nombreUsuarioContains(dto.getUsuarioNombre()));
			}
			if (dto.getUsuarioEmail() != null && !dto.getUsuarioEmail().isEmpty()) {
				spec = spec.and(emailUsuarioContains(dto.getUsuarioEmail()));
			}
			if (dto.getUsuarioNumero() != null && !dto.getUsuarioNumero().isEmpty()) {
				spec = spec.and(numeroUsuarioContains(dto.getUsuarioNumero()));
			}
			if (dto.getUsuarioDni() != null && !dto.getUsuarioDni().isEmpty()) {
				spec = spec.and(dniUsuarioContains(dto.getUsuarioDni()));
			}
			if (dto.getExplicacion() != null && !dto.getExplicacion().isEmpty()) {
				spec = spec.and(explicacionContains(dto.getExplicacion()));
			}
			if (dto.getResumen() != null && !dto.getResumen().isEmpty()) {
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
		}

		// Aislamiento Multi-empresa
		if (currentEmpresaId != null) {
			spec = spec.and(hasEmpresa(currentEmpresaId));
		} else if (!isSuperAdmin) {
			return (root, query, cb) -> cb.disjunction();
		}

		return spec;
	}

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

}
