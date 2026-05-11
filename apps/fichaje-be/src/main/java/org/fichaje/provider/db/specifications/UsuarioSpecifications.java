package org.fichaje.provider.db.specifications;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import org.fichaje.dto.entity.UsuarioDtoFilter;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.specifications.common.UserSpecificationImpl;

@Component
public final class UsuarioSpecifications extends UserSpecificationImpl<Usuario> {

	public Specification<Usuario> buildSpecification(UsuarioDtoFilter dto, Long currentEmpresaId, boolean isSuperAdmin) {
		if (dto == null) return (root, query, cb) -> cb.conjunction();

		Specification<Usuario> spec = (root, query, cb) -> cb.conjunction();

		if (dto.getNombreEmpleado() != null) {
			spec = spec.and(nombreUsuarioContains(dto.getNombreEmpleado()));
		}
		if (dto.getEmail() != null) {
			spec = spec.and(emailUsuarioContains(dto.getEmail()));
		}
		if (dto.getNumero() != null) {
			spec = spec.and(numeroUsuarioContains(dto.getNumero()));
		}
		if (dto.getDni() != null) {
			spec = spec.and(dniUsuarioContains(dto.getDni()));
		}
		if (dto.getWorking() != null) {
			spec = spec.and(isWorking(dto.getWorking()));
		}
		if (dto.getEnVacaciones() != null) {
			spec = spec.and(isEnVacaciones(dto.getEnVacaciones()));
		}
		if (dto.getDeBaja() != null) {
			spec = spec.and(isDeBaja(dto.getDeBaja()));
		}
		if (dto.getDiasVacacionesDesde() != null) {
			spec = spec.and(diasDesde(dto.getDiasVacacionesDesde()));
		}
		if (dto.getDiasVacacionesHasta() != null) {
			spec = spec.and(diasHasta(dto.getDiasVacacionesHasta()));
		}
		if (dto.getHorasGeneradasDesde() != null) {
			spec = spec.and(horasDesde(dto.getHorasGeneradasDesde()));
		}
		if (dto.getHorasGeneradasHasta() != null) {
			spec = spec.and(horasHasta(dto.getHorasGeneradasHasta()));
		}
		if (dto.getEmpresaId() != null) {
			spec = spec.and(hasEmpresa(dto.getEmpresaId()));
		}
		if (dto.getSedeId() != null) {
			spec = spec.and(hasSede(dto.getSedeId()));
		}

		// Aislamiento Multi-empresa (si no es superadmin o se especifica empresa)
		if (currentEmpresaId != null) {
			spec = spec.and(hasEmpresa(currentEmpresaId));
		}

		return spec;
	}

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

	}
