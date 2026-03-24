package org.fichaje.converter;

import org.fichaje.dto.entity.EmpresaDTO;
import org.fichaje.dto.entity.EmpresaParametroDTO;
import org.fichaje.dto.entity.SedeDTO;
import org.fichaje.provider.db.entity.Empresa;
import org.fichaje.provider.db.entity.EmpresaParametro;
import org.fichaje.provider.db.entity.Sede;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EmpresaDtoConverter {

	private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

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
				.ubicacion(toPoint(dto.getLatitud(), dto.getLongitud()))
				.parametros(toParametroEntityList(dto.getParametros()))
				.sedes(toSedeEntityList(dto.getSedes()))
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
				.latitud(e.getUbicacion() != null ? e.getUbicacion().getY() : null)
				.longitud(e.getUbicacion() != null ? e.getUbicacion().getX() : null)
				.parametros(toParametroDtoList(e.getParametros()))
				.sedes(toSedeDtoList(e.getSedes()))
				.build();
	}

	private Point toPoint(Double latitud, Double longitud) {
		if (latitud == null || longitud == null) return null;
		return GEOMETRY_FACTORY.createPoint(new Coordinate(longitud, latitud));
	}

	private List<EmpresaParametroDTO> toParametroDtoList(List<EmpresaParametro> parametros) {
		if (parametros == null) return new ArrayList<>();
		return parametros.stream()
				.map(p -> EmpresaParametroDTO.builder()
						.id(p.getId())
						.empresaId(p.getEmpresa().getId())
						.clave(p.getClave())
						.valor(p.getValor())
						.build())
				.toList();
	}

	private List<EmpresaParametro> toParametroEntityList(List<EmpresaParametroDTO> parametros) {
		if (parametros == null) return new ArrayList<>();
		return parametros.stream()
				.map(p -> EmpresaParametro.builder()
						.clave(p.getClave())
						.valor(p.getValor())
						.build())
				.toList();
	}

	private List<SedeDTO> toSedeDtoList(List<Sede> sedes) {
		if (sedes == null) return new ArrayList<>();
		return sedes.stream()
				.map(s -> SedeDTO.builder()
						.id(s.getId())
						.empresaId(s.getEmpresa().getId())
						.nombre(s.getNombre())
						.direccion(s.getDireccion())
						.codigoPostal(s.getCodigoPostal())
						.localidad(s.getLocalidad())
						.provincia(s.getProvincia())
						.pais(s.getPais())
						.latitud(s.getUbicacion() != null ? s.getUbicacion().getY() : null)
						.longitud(s.getUbicacion() != null ? s.getUbicacion().getX() : null)
						.activa(s.isActiva())
						.build())
				.toList();
	}

	private List<Sede> toSedeEntityList(List<SedeDTO> sedes) {
		if (sedes == null) return new ArrayList<>();
		return sedes.stream()
				.map(s -> Sede.builder()
						.nombre(s.getNombre())
						.direccion(s.getDireccion())
						.codigoPostal(s.getCodigoPostal())
						.localidad(s.getLocalidad())
						.provincia(s.getProvincia())
						.pais(s.getPais())
						.ubicacion(toPoint(s.getLatitud(), s.getLongitud()))
						.activa(s.isActiva())
						.build())
				.toList();
	}

}
