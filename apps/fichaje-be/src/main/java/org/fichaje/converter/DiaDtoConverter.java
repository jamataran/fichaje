package org.fichaje.converter;

import org.springframework.stereotype.Component;

import org.fichaje.dto.entity.DiaDto;
import org.fichaje.provider.db.entity.Calendario;
import org.fichaje.provider.db.entity.DiaLaborable;

@Component
public class DiaDtoConverter {

	public DiaLaborable transform(DiaDto dto) {
		DiaLaborable dia = new DiaLaborable();
		dia.setId(dto.getId());
		dia.setDia(dto.getDia());
		dia.setHoraInicio(dto.getHoraInicio());
		dia.setHoraFin(dto.getHoraFin());
		return dia;
	}

	public DiaLaborable transformWithCalendario(DiaDto dto, Calendario calendario) {
		DiaLaborable dia = transform(dto);
		dia.setCalendario(calendario);
		return dia;
	}

	public DiaDto inverseTransform(DiaLaborable dia) {
		DiaDto dto = new DiaDto();
		dto.setId(dia.getId());
		dto.setDia(dia.getDia());
		dto.setHoraInicio(dia.getHoraInicio());
		dto.setHoraFin(dia.getHoraFin());
		if (dia.getCalendario() != null) {
			dto.setCalendarioNombre(dia.getCalendario().getNombre());
		}
		return dto;
	}

}
