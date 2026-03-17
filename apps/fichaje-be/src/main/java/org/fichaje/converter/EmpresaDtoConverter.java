package org.fichaje.converter;

import org.fichaje.dto.entity.EmpresaDTO;
import org.fichaje.provider.db.entity.Empresa;
import org.springframework.stereotype.Component;

@Component
public class EmpresaDtoConverter {

	public Empresa transform(EmpresaDTO dto) {
		Empresa e = new Empresa();
		e.setNombre(dto.getNombre());
		e.setCif(dto.getCif());
		e.setActiva(dto.isActiva());
		return e;
	}

	public EmpresaDTO todtoConverter(Empresa e) {
		EmpresaDTO dto = new EmpresaDTO();
		dto.setId(e.getId());
		dto.setNombre(e.getNombre());
		dto.setCif(e.getCif());
		dto.setActiva(e.isActiva());
		return dto;
	}

}
