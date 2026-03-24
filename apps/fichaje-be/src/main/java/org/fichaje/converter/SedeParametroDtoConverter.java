package org.fichaje.converter;

import org.fichaje.dto.entity.SedeParametroDTO;
import org.fichaje.provider.db.entity.SedeParametro;
import org.springframework.stereotype.Component;

@Component
public class SedeParametroDtoConverter {

	public SedeParametroDTO todtoConverter(SedeParametro parametro) {
		return SedeParametroDTO.builder()
				.id(parametro.getId())
				.sedeId(parametro.getSede().getId())
				.clave(parametro.getClave())
				.valor(parametro.getValor())
				.build();
	}

	public SedeParametro transform(SedeParametroDTO dto) {
		return SedeParametro.builder()
				.clave(dto.getClave())
				.valor(dto.getValor())
				.build();
	}
}