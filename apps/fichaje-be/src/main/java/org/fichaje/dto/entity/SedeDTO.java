package org.fichaje.dto.entity;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SedeDTO {

	private Long id;

	private Long empresaId;

	@NotBlank(message = "El nombre de la sede es obligatorio")
	private String nombre;

	private String direccion;

	@Size(max = 10, message = "El código postal no puede superar los 10 caracteres")
	private String codigoPostal;

	private String localidad;

	private String provincia;

	private String pais;

	@DecimalMin(value = "-90.0", message = "La latitud mínima es -90")
	@DecimalMax(value = "90.0", message = "La latitud máxima es 90")
	private Double latitud;

	@DecimalMin(value = "-180.0", message = "La longitud mínima es -180")
	@DecimalMax(value = "180.0", message = "La longitud máxima es 180")
	private Double longitud;

	private boolean activa;

	private List<SedeParametroDTO> parametros;
}