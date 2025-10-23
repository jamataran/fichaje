package org.fichaje.converter;

import org.springframework.stereotype.Component;

import org.fichaje.dto.entity.IncidenciaDtoEdit;
import org.fichaje.provider.db.entity.Incidencia;

@Component
public class IncidenciaDtoConverter {


	public Incidencia transformEdit(Incidencia i, IncidenciaDtoEdit editar) {
		i.setResuelta(editar.getResuelta());
		i.setExplicacion(editar.getExplicacion());
		return i;
	}

}
