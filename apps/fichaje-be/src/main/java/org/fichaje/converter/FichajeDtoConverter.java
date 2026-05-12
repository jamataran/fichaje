package org.fichaje.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import org.fichaje.dto.entity.FichajeDto;
import org.fichaje.dto.entity.FichajeDtoReqRes;
import org.fichaje.provider.db.entity.Fichaje;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.service.UsuarioService;

@Component
@RequiredArgsConstructor
public class FichajeDtoConverter {

	private final UsuarioService usuarioService;

	public FichajeDto inverseTransform(Fichaje f) {
		if (f == null) return null;
		return FichajeDto.builder()
				.hora(f.getHora())
				.dia(f.getDia())
				.numeroUsuario(f.getUsuario() != null ? f.getUsuario().getNumero() : null)
				.nombreUsuario(f.getUsuario() != null ? f.getUsuario().getNombreEmpleado() : null)
				.tipo(f.getTipo())
				.origen(f.getOrigen())
				.build();
	}

	public Fichaje transform(FichajeDtoReqRes dto) {
		if (dto == null) return null;
		Fichaje f = new Fichaje();
		f.setHora(dto.getHora());
		f.setDia(dto.getDia());
		Usuario usuario = usuarioService
				.findByNumero(dto.getNumeroUsuario())
				.orElse(null);
		f.setUsuario(usuario);
		f.setTipo(dto.getTipo());
		f.setOrigen(dto.getOrigen());

		return f;
	}

}
