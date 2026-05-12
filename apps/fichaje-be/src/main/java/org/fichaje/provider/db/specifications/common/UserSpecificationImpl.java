package org.fichaje.provider.db.specifications.common;

import org.springframework.data.jpa.domain.Specification;

public class UserSpecificationImpl<E> implements SpecificationTemplate<E> {

	@Override
	public Specification<E> nombreUsuarioContains(
			String expression) {
		return (root, query, builder) -> builder
				.like(root.get("nombreEmpleado"),
						SpecificationHelper.contains(expression));
	}

	@Override
	public Specification<E> numeroUsuarioContains(
			String expression) {
		return (root, query, builder) -> builder
				.like(root.get("numero"),
						SpecificationHelper.contains(expression));
	}

	@Override
	public Specification<E> dniUsuarioContains(
			String expression) {
		return (root, query, builder) -> builder
				.like(root.get("dni"),
						SpecificationHelper.contains(expression));
	}

	@Override
	public Specification<E> emailUsuarioContains(
			String expression) {
		return (root, query, builder) -> builder
				.like(root.get("email"),
						SpecificationHelper.contains(expression));
	}

	@Override
	public Specification<E> hasEmpresa(Long empresaId) {
		return (root, query, builder) -> {
			if (empresaId == null) return null;
			query.distinct(true);
			return builder.equal(root.join("empresas").get("id"), empresaId);
		};
	}

	@Override
	public Specification<E> hasSede(Long sedeId) {
		return (root, query, builder) -> {
			if (sedeId == null) return null;
			query.distinct(true);
			return builder.equal(root.join("sedes").get("id"), sedeId);
		};
	}

}
