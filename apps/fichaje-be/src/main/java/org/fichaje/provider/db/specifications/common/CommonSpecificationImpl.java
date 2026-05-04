package org.fichaje.provider.db.specifications.common;

import org.springframework.data.jpa.domain.Specification;

public class CommonSpecificationImpl<E> implements SpecificationTemplate<E> {
	
	SpecificationHelper helper;

	@Override
	public Specification<E> nombreUsuarioContains(
			String expression) {
		return (root, query, builder) -> builder
				.like(root.join("usuario").get("nombreEmpleado"),
						SpecificationHelper.contains(expression));
	}

	@Override
	public Specification<E> numeroUsuarioContains(
			String expression) {
		return (root, query, builder) -> builder
				.like(root.join("usuario").get("numero"),
						SpecificationHelper.contains(expression));
	}

	@Override
	public Specification<E> dniUsuarioContains(
			String expression) {
		return (root, query, builder) -> builder
				.like(root.join("usuario").get("dni"),
						SpecificationHelper.contains(expression));
	}

	@Override
	public Specification<E> emailUsuarioContains(
			String expression) {
		return (root, query, builder) -> builder
				.like(root.join("usuario").get("email"),
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
