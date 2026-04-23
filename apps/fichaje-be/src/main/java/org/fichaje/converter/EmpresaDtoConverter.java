package org.fichaje.converter;

import org.fichaje.dto.entity.EmpresaCreateDTO;
import org.fichaje.dto.entity.EmpresaDTO;
import org.fichaje.dto.entity.EmpresaParametroDTO;
import org.fichaje.dto.entity.SedeDTO;
import org.fichaje.provider.db.entity.Empresa;
import org.fichaje.provider.db.entity.EmpresaParametro;
import org.fichaje.provider.db.entity.Sede;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.fichaje.dto.entity.EmpresaDTOWithoutSedes;

@Component
public class EmpresaDtoConverter {

	public EmpresaDTOWithoutSedes toDtoWithoutSedes(Empresa e) {
		return EmpresaDTOWithoutSedes.builder()
				.id(e.getId())
				.nombre(e.getNombre())
				.razonSocial(e.getRazonSocial())
				.cif(e.getCif())
				.activa(e.isActiva())
				.build();
	}

	public Empresa transform(EmpresaCreateDTO dto) {
		return Empresa.builder()
				.nombre(dto.getNombre())
				.razonSocial(dto.getRazonSocial())
				.cif(dto.getCif())
				.activa(dto.isActiva())
				.build();
	}

	public EmpresaDTO todtoConverter(Empresa e) {
		return EmpresaDTO.builder()
				.id(e.getId())
				.nombre(e.getNombre())
				.razonSocial(e.getRazonSocial())
				.cif(e.getCif())
				.activa(e.isActiva())
				.sedes(toSedeDtoList(e.getSedes()))
				.build();
	}

	private Set<EmpresaParametroDTO> toParametroDtoList(Set<EmpresaParametro> parametros) {
		if (parametros == null) return new HashSet<>();
		return parametros.stream()
				.map(p -> EmpresaParametroDTO.builder()
						.id(p.getId())
						.empresaId(p.getEmpresa().getId())
						.clave(p.getClave())
						.valor(p.getValor())
						.build())
				.collect(Collectors.toSet());
	}

	private Set<EmpresaParametro> toParametroEntityList(Set<EmpresaParametroDTO> parametros) {
		if (parametros == null) return new HashSet<>();
		return parametros.stream()
				.map(p -> EmpresaParametro.builder()
						.clave(p.getClave())
						.valor(p.getValor())
						.build())
				.collect(Collectors.toSet());
	}

	private Set<SedeDTO> toSedeDtoList(Set<Sede> sedes) {
		if (sedes == null) return new HashSet<>();
		return sedes.stream()
				.map(s -> SedeDTO.builder()
						.id(s.getId())
						.empresaId(s.getEmpresa().getId())
						.nombre(s.getNombre())
						.email(s.getEmail())
						.telefono(s.getTelefono())
						.direccion(s.getDireccion())
						.codigoPostal(s.getCodigoPostal())
						.localidad(s.getLocalidad())
						.provincia(s.getProvincia())
						.pais(s.getPais())
						.latitud(s.getUbicacion() != null ? s.getUbicacion().getY() : null)
						.longitud(s.getUbicacion() != null ? s.getUbicacion().getX() : null)
						.activa(s.isActiva())
						.build())
				.collect(Collectors.toSet());
	}
}