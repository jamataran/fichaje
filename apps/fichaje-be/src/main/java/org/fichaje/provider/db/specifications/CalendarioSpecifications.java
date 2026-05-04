package org.fichaje.provider.db.specifications;

import org.fichaje.provider.db.entity.Calendario;
import org.fichaje.provider.db.specifications.common.CommonSpecificationImpl;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class CalendarioSpecifications extends CommonSpecificationImpl<Calendario> {

    @Override
    public Specification<Calendario> hasEmpresa(Long empresaId) {
        return (root, query, builder) -> {
            if (empresaId == null) return null;
            return builder.equal(root.get("sede").get("empresa").get("id"), empresaId);
        };
    }

    public Specification<Calendario> hasSede(Long sedeId) {
        return (root, query, builder) -> {
            if (sedeId == null) return null;
            return builder.equal(root.get("sede").get("id"), sedeId);
        };
    }
}
