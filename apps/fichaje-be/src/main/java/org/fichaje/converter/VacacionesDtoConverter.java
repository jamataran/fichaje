package org.fichaje.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.fichaje.dto.entity.VacacionesDto;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.entity.Vacaciones;
import org.fichaje.service.UsuarioService;

@Component
public class VacacionesDtoConverter {

	@Autowired
	private UsuarioService service;

	public Vacaciones transform(VacacionesDto dto) {
		Vacaciones vacaciones = new Vacaciones();

		// comprobamos que la fecha fin es posterior o igual a la de inicio
		int comparacion = dto.getInicio().compareTo(dto.getFin());
		if (comparacion > 0) {
			return null;
		}

		vacaciones.setInicio(dto.getInicio());
		vacaciones.setFin(dto.getFin());
		Usuario usuario = service.findByNumero(dto.getNumeroUsuario())
				.orElse(null);
		vacaciones.setUsuario(usuario);
		vacaciones.setConsumidas(false);
		return vacaciones;
	}

}
