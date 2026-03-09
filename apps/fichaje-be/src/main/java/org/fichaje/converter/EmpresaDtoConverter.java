package org.fichaje.converter;

import org.fichaje.dto.entity.EmpresaDto;
import org.fichaje.provider.db.entity.Empresa;
import org.springframework.stereotype.Component;

@Component
public class EmpresaDtoConverter {

//	@Autowired
//	private DiaDtoToDia service;

	public Empresa transform(EmpresaDto dto) {
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

	public EmpresaDto inverseTransform(Empresa e) {
		EmpresaDto dto = new EmpresaDto();
		dto.setId(e.getId());
		dto.setNombre(e.getNombre());
		dto.setCif(e.getCif());
		dto.setActiva(e.isActiva());
		return dto;
	}

}
