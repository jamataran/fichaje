package org.fichaje.converter;

import org.fichaje.dto.entity.SedeDTO;
import org.fichaje.dto.entity.SedeParametroDTO;
import org.fichaje.provider.db.entity.Sede;
import org.fichaje.provider.db.entity.SedeParametro;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SedeDtoConverter {

	private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

	public Sede transform(SedeDTO dto) {
		return Sede.builder()
				.nombre(dto.getNombre())
				.direccion(dto.getDireccion())
				.codigoPostal(dto.getCodigoPostal())
				.localidad(dto.getLocalidad())
				.provincia(dto.getProvincia())
				.pais(dto.getPais())
				.ubicacion(toPoint(dto.getLatitud(), dto.getLongitud()))
				.activa(dto.isActiva())
				.parametros(toParametroEntityList(dto.getParametros()))
				.build();
	}

	public SedeDTO todtoConverter(Sede sede) {
		return SedeDTO.builder()
				.id(sede.getId())
				.empresaId(sede.getEmpresa().getId())
				.nombre(sede.getNombre())
				.direccion(sede.getDireccion())
				.codigoPostal(sede.getCodigoPostal())
				.localidad(sede.getLocalidad())
				.provincia(sede.getProvincia())
				.pais(sede.getPais())
				.latitud(sede.getUbicacion() != null ? sede.getUbicacion().getY() : null)
				.longitud(sede.getUbicacion() != null ? sede.getUbicacion().getX() : null)
				.activa(sede.isActiva())
				.parametros(toParametroDtoList(sede.getParametros()))
				.build();
	}

	private Point toPoint(Double latitud, Double longitud) {
		if (latitud == null || longitud == null) return null;
		return GEOMETRY_FACTORY.createPoint(new Coordinate(longitud, latitud));
	}

	private List<SedeParametroDTO> toParametroDtoList(List<SedeParametro> parametros) {
		if (parametros == null) return new ArrayList<>();
		return parametros.stream()
				.map(p -> SedeParametroDTO.builder()
						.id(p.getId())
						.sedeId(p.getSede().getId())
						.clave(p.getClave())
						.valor(p.getValor())
						.build())
				.toList();
	}

	private List<SedeParametro> toParametroEntityList(List<SedeParametroDTO> parametros) {
		if (parametros == null) return new ArrayList<>();
		return parametros.stream()
				.map(p -> SedeParametro.builder()
						.clave(p.getClave())
						.valor(p.getValor())
						.build())
				.toList();
	}
}