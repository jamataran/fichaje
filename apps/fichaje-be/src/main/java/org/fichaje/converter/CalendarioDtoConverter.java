package org.fichaje.converter;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.fichaje.dto.entity.CalendarioDto;
import org.fichaje.provider.db.entity.Calendario;

@Component
public class CalendarioDtoConverter {

	@Autowired
	private DiaDtoConverter diaConverter;

	public Calendario transform(CalendarioDto dto) {
		Calendario c = new Calendario();
		c.setId(dto.getId());
		c.setNombre(dto.getNombre());
		c.setActive(dto.isActive());
		c.setMinutosMasEntrada(dto.getMinutosMasEntrada());
		c.setMinutosMenosEntrada(dto.getMinutosMenosEntrada());
		c.setYear(dto.getYear());
		if (dto.getDias() != null) {
			c.setDias(dto.getDias().stream()
					.map(diaConverter::transform)
					.collect(Collectors.toList()));
		}
		return c;
	}

	public CalendarioDto inverseTransform(Calendario c) {
		CalendarioDto dto = new CalendarioDto();
		dto.setId(c.getId());
		dto.setNombre(c.getNombre());
		dto.setActive(c.isActive());
		dto.setYear(c.getYear());
		dto.setMinutosMasEntrada(c.getMinutosMasEntrada());
		dto.setMinutosMenosEntrada(c.getMinutosMenosEntrada());
		if (c.getDias() != null) {
			dto.setDias(c.getDias().stream()
					.map(diaConverter::inverseTransform)
					.collect(Collectors.toList()));
		}
		return dto;
	}

}
