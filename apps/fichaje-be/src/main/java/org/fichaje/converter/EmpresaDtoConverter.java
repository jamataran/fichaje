package org.fichaje.converter;

import org.fichaje.dto.entity.EmpresaDTO;
import org.fichaje.provider.db.entity.Empresa;
import org.springframework.stereotype.Component;

@Component
public class EmpresaDtoConverter {

	public Empresa transform(EmpresaDTO dto) {
		return Empresa.builder()
				.nombre(dto.getNombre())
				.razonSocial(dto.getRazonSocial())
				.cif(dto.getCif())
				.activa(dto.isActiva())
				.email(dto.getEmail())
				.telefono(dto.getTelefono())
				.direccion(dto.getDireccion())
				.codigoPostal(dto.getCodigoPostal())
				.localidad(dto.getLocalidad())
				.provincia(dto.getProvincia())
				.pais(dto.getPais())
				.latitud(dto.getLatitud())
				.longitud(dto.getLongitud())
				.build();
	}

	public EmpresaDTO todtoConverter(Empresa e) {
		return EmpresaDTO.builder()
				.id(e.getId())
				.nombre(e.getNombre())
				.razonSocial(e.getRazonSocial())
				.cif(e.getCif())
				.activa(e.isActiva())
				.email(e.getEmail())
				.telefono(e.getTelefono())
				.direccion(e.getDireccion())
				.codigoPostal(e.getCodigoPostal())
				.localidad(e.getLocalidad())
				.provincia(e.getProvincia())
				.pais(e.getPais())
				.latitud(e.getLatitud())
				.longitud(e.getLongitud())
				.build();
	}

}
