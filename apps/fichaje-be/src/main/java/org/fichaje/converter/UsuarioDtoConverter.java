package org.fichaje.converter;

import org.fichaje.dto.entity.UsuarioDTO;
import org.fichaje.provider.db.entity.Sede;
import org.fichaje.provider.db.entity.Empresa;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import org.fichaje.dto.entity.UsuarioDtoEdit;
import org.fichaje.dto.entity.UsuarioDtoEditPassword;
import org.fichaje.provider.db.entity.Usuario;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class UsuarioDtoConverter {

	private final PasswordEncoder passwordEncoder;
	private final SedeDtoConverter sedeDtoConverter;
	private final EmpresaDtoConverter empresaDtoConverter;

	public UsuarioDtoConverter(SedeDtoConverter sedeDtoConverter, PasswordEncoder passwordEncoder, EmpresaDtoConverter empresaDtoConverter){
		this.sedeDtoConverter = sedeDtoConverter;
		this.passwordEncoder = passwordEncoder;
		this.empresaDtoConverter = empresaDtoConverter;
	}

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
				.sedes(u.getSedes() != null ? u.getSedes().stream()
						.map(sedeDtoConverter::todtoConverter)
						.toList() : new ArrayList<>())
				.empresas(u.getEmpresas() != null ? u.getEmpresas().stream()
						.map(empresaDtoConverter::toDtoWithoutSedes)
						.toList() : new ArrayList<>())
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

	public UsuarioDTO inverseTransformForSession(Usuario u, Long empresaId) {
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
				.empresas(u.getEmpresas() != null
						? u.getEmpresas().stream()
						.filter(e -> empresaId != null && e.getId().equals(empresaId))
						.map(empresaDtoConverter::toDtoWithoutSedes)
						.toList()
						: new ArrayList<>())
				.sedes(u.getSedes() != null
						? u.getSedes().stream()
						.filter(s -> empresaId != null && s.getEmpresa().getId().equals(empresaId))
						.map(sedeDtoConverter::todtoConverter)
						.toList()
						: new ArrayList<>())
				.build();
	}

}
