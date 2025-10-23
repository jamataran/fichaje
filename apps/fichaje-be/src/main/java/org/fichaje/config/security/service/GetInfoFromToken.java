package org.fichaje.config.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fichaje.provider.db.entity.Rol;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.entity.RrhhDto;
import org.fichaje.config.security.enums.RolNombre;
import org.fichaje.config.security.jwt.JwtProvider;
import org.fichaje.service.UsuarioService;

@Service
public class GetInfoFromToken {

	@Autowired
	UsuarioService usuarioService;
	@Autowired
	JwtProvider jwtProvider;

	public RrhhDto rrhhInfo(String token) {

		token = token.replace("Bearer ", "");

		boolean response = true;
		String numeroUsuario = "";

		if (jwtProvider.validateToken(token)) {
			numeroUsuario = jwtProvider.getSubjectFromToken(token);
			Usuario usuario = usuarioService.findByNumero(numeroUsuario).orElse(null);
			if (usuario != null) {
				for (Rol rol : usuario.getRoles()) {
					if (rol.getRolNombre() == RolNombre.ROLE_RRHH)
						response = false;
				}
			}
		}

		return new RrhhDto(response, numeroUsuario);
	}
}
