package org.fichaje.provider.db.specifications.common;

import org.springframework.data.jpa.domain.Specification;

public interface SpecificationTemplate<E> {

	public Specification<E> nombreUsuarioContains(String expression);

	public Specification<E> numeroUsuarioContains(String expression);

	public Specification<E> dniUsuarioContains(String expression);

	public Specification<E> emailUsuarioContains(String expression);

	public Specification<E> hasEmpresa(Long empresaId);

	public Specification<E> hasSede(Long sedeId);

}
