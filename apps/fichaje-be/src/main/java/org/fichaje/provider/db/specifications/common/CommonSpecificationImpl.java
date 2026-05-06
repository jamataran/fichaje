package org.fichaje.provider.db.specifications.common;

import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class CommonSpecificationImpl<E> implements SpecificationTemplate<E> {

	SpecificationHelper helper;

	@Override
	public Specification<E> nombreUsuarioContains(String expression) {
		return (root, query, builder) -> builder
				.like(root.join("usuario", JoinType.LEFT).get("nombreEmpleado"),
						SpecificationHelper.contains(expression));
	}

	@Override
	public Specification<E> numeroUsuarioContains(String expression) {
		return (root, query, builder) -> builder
				.like(root.join("usuario", JoinType.LEFT).get("numero"),
						SpecificationHelper.contains(expression));
	}

	@Override
	public Specification<E> dniUsuarioContains(String expression) {
		return (root, query, builder) -> builder
				.like(root.join("usuario", JoinType.LEFT).get("dni"),
						SpecificationHelper.contains(expression));
	}

	@Override
	public Specification<E> emailUsuarioContains(String expression) {
		return (root, query, builder) -> builder
				.like(root.join("usuario", JoinType.LEFT).get("email"),
						SpecificationHelper.contains(expression));
	}

	@Override
	public Specification<E> hasEmpresa(Long empresaId) {
		return (root, query, builder) -> {
			if (empresaId == null) return null;
			return builder.equal(root.get("empresa").get("id"), empresaId);
		};
	}

	@Override
	public Specification<E> hasSede(Long sedeId) {
		return (root, query, builder) -> {
			if (sedeId == null) return null;
			return builder.equal(root.join("usuario").join("sedes").get("id"), sedeId);
		};
	}


}
