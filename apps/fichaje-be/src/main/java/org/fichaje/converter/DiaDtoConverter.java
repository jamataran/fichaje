package org.fichaje.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.fichaje.dto.entity.DiaDto;
import org.fichaje.provider.db.entity.Calendario;
import org.fichaje.provider.db.entity.DiaLaborable;
import org.fichaje.service.CalendarioService;

@Component
public class DiaDtoConverter {

	@Autowired
	private CalendarioService service;

	public DiaLaborable transform(DiaDto dto) {
		DiaLaborable dia = new DiaLaborable();
		dia.setDia(dto.getDia());
		dia.setHoraInicio(dto.getHoraInicio());
		dia.setHoraFin(dto.getHoraFin());
		Calendario calendario = service
				.findByNombre(dto.getCalendarioNombre())
				.orElse(null);
		dia.setCalendario(calendario);
		return dia;
	}

	public DiaLaborable transformById(DiaDto dto, Long idCalendario) {
		DiaLaborable dia = new DiaLaborable();
		dia.setDia(dto.getDia());
		dia.setHoraInicio(dto.getHoraInicio());
		dia.setHoraFin(dto.getHoraFin());
		Calendario calendario = service
				.findById(idCalendario)
				.orElse(null);
		dia.setCalendario(calendario);
		return dia;
	}

}
