package org.fichaje.converter;

import org.fichaje.dto.entity.EmpresaDTO;
import org.fichaje.provider.db.entity.Empresa;
import org.springframework.stereotype.Component;

@Component
public class EmpresaDtoConverter {

//	@Autowired
//	private DiaDtoToDia service;

	public Empresa transform(EmpresaDTO dto) {
		Empresa e = new Empresa();
		e.setNombre(dto.getNombre());
		e.setCif(dto.getCif());
		e.setActiva(dto.isActiva());
//		c.setDias(
//				dto.getDias().stream()
//						.map(diaDto -> service.transform(diaDto))
//						.collect(Collectors.toList()));
		return e;
	}

	public EmpresaDTO inverseTransform(Empresa e) {
		EmpresaDTO dto = new EmpresaDTO();
		dto.setId(e.getId());
		dto.setNombre(e.getNombre());
		dto.setCif(e.getCif());
		dto.setActiva(e.isActiva());
		return dto;
	}

}
