package org.fichaje.converter;

import org.fichaje.dto.entity.UsuarioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import org.fichaje.dto.entity.UsuarioDtoEdit;
import org.fichaje.dto.entity.UsuarioDtoEditPassword;
import org.fichaje.provider.db.entity.Usuario;

@Component
public class UsuarioDtoConverter {

	@Autowired
	PasswordEncoder passwordEncoder;

	/**
	 * Convierte un Usuario a UsuarioDTO (lectura/respuesta)
	 */
	public UsuarioDTO inverseTransform(Usuario u) {
		return UsuarioDTO
				.builder()
				.id(u.getId())
				.email(u.getEmail())
				.numero(u.getNumero())
				.nombreEmpleado(u.getNombreEmpleado())
				.dni(u.getDni())
				.diasVacaciones(u.getDiasVacaciones())
				.horasGeneradas(u.getHorasGeneradas())
				.working(u.getWorking())
				.enVacaciones(u.getEnVacaciones())
				.deBaja(u.getDeBaja())
				.build();
	}

	/**
	 * Aplica cambios de edición a un Usuario existente
	 */
	public Usuario transformEdit(Usuario u, UsuarioDtoEdit editar) {
		u.setEmail(editar.getEmail());
		u.setNumero(editar.getNumero());
		u.setNombreEmpleado(editar.getNombreEmpleado());
		u.setDni(editar.getDni());
		u.setDiasVacaciones(editar.getDiasVacaciones());
		u.setHorasGeneradas(editar.getHorasGeneradas());
		u.setEnVacaciones(editar.getEnVacaciones());
		u.setDeBaja(editar.getDeBaja());
		u.setWorking(editar.getWorking());
		return u;
	}

	/**
	 * Aplica cambio de contraseña a un Usuario
	 */
	public Usuario transformEditPassword(Usuario u, UsuarioDtoEditPassword editar) {
		u.setPassword(passwordEncoder.encode(editar.getPassword()));
		return u;
	}

}
