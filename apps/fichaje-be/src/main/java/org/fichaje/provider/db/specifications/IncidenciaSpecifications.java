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

}
