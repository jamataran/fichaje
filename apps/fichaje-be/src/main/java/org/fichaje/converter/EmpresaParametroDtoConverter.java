package org.fichaje.converter;

import org.fichaje.dto.entity.EmpresaParametroDTO;
import org.fichaje.provider.db.entity.EmpresaParametro;
import org.springframework.stereotype.Component;

@Component
public class EmpresaParametroDtoConverter {

	public EmpresaParametroDTO todtoConverter(EmpresaParametro parametro) {
		return EmpresaParametroDTO.builder()
				.id(parametro.getId())
				.empresaId(parametro.getEmpresa().getId())
				.clave(parametro.getClave())
				.valor(parametro.getValor())
				.build();
	}

	public EmpresaParametro transform(EmpresaParametroDTO dto) {
		return EmpresaParametro.builder()
				.clave(dto.getClave())
				.valor(dto.getValor())
				.build();
	}
}