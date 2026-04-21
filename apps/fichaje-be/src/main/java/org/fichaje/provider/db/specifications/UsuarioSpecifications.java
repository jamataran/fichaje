package org.fichaje.provider.db.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.specifications.common.UserSpecificationImpl;
import jakarta.persistence.criteria.Join;

@Component
public final class UsuarioSpecifications extends UserSpecificationImpl<Usuario> {
	

	public Specification<Usuario> diasEquals(
			Integer d) {
		return (root, query, builder) -> builder
				.equal(root.get("diasVacaciones"),
						d);
	}

	public Specification<Usuario> horasEquals(
			Double h) {
		return (root, query, builder) -> builder
				.equal(root.get("horasGeneradas"),
						h);
	}

	public Specification<Usuario> isWorking(
			Boolean b) {
		return (root, query, builder) -> builder
				.equal(root.get("working"),
						b);
	}

	public Specification<Usuario> isEnVacaciones(
			Boolean b) {
		return (root, query, builder) -> builder
				.equal(root.get("enVacaciones"),
						b);
	}

	public Specification<Usuario> isDeBaja(
			Boolean b) {
		return (root, query, builder) -> builder
				.equal(root.get("deBaja"),
						b);
	}

	public Specification<Usuario> diasDesde(Integer d) {
		return (root, query, builder) -> builder
				.greaterThanOrEqualTo(root.get("diasVacaciones"), d);
	}

	public Specification<Usuario> diasHasta(Integer d) {
		return (root, query, builder) -> builder
				.lessThanOrEqualTo(root.get("diasVacaciones"), d);
	}

	public Specification<Usuario> horasDesde(Double h) {
		return (root, query, builder) -> builder
				.greaterThanOrEqualTo(root.get("horasGeneradas"), h);
	}

	public Specification<Usuario> horasHasta(Double h) {
		return (root, query, builder) -> builder
				.lessThanOrEqualTo(root.get("horasGeneradas"), h);
	}

	public Specification<Usuario> hasEmpresa(Long empresaId) {
		return (root, query, builder) -> {
			if (empresaId == null) return null;
			query.distinct(true);
			Join<Object, Object> empresasJoin = root.join("empresas");
			return builder.equal(empresasJoin.get("id"), empresaId);
		};
	}

	public Specification<Usuario> hasSede(Long sedeId) {
		return (root, query, builder) -> {
			if (sedeId == null) return null;
			query.distinct(true);
			Join<Object, Object> sedesJoin = root.join("sedes");
			return builder.equal(sedesJoin.get("id"), sedeId);
		};
	}


}
