package org.fichaje.dto.entity;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SedeDTO {

	private Long id;

	private Long empresaId;

	@NotBlank(message = "El nombre de la sede es obligatorio")
	@Pattern(regexp = "^[\\p{L}\\s.,'&()-]+$",
			message = "El nombre solo puede contener letras, espacios y caracteres especiales básicos")
	private String nombre;

	@NotBlank(message = "El email es obligatorio")
	@Email(message = "El formato del email no es válido")
	private String email;

	@Pattern(regexp = "^[+]?[0-9\\s()]{6,20}$",
			message = "El formato del teléfono no es válido")
	private String telefono;

	@NotBlank(message = "La dirección es obligatoria")
	private String direccion;

	@NotBlank(message = "El código postal es obligatorio")
	@Size(max = 10, message = "El código postal no puede superar los 10 caracteres")
	@Pattern(regexp = "^[0-9]{5}$",
			message = "El código postal debe tener 5 dígitos")
	private String codigoPostal;

	@NotBlank(message = "La localidad es obligatoria")
	@Pattern(regexp = "^[\\p{L}\\s.,'()-]+$",
			message = "La localidad solo puede contener letras, espacios y caracteres especiales básicos")
	private String localidad;

	@NotBlank(message = "La provincia es obligatoria")
	@Pattern(regexp = "^[\\p{L}\\s.,'()-]+$",
			message = "La provincia solo puede contener letras, espacios y caracteres especiales básicos")
	private String provincia;

	@NotBlank(message = "El país es obligatorio")
	@Pattern(regexp = "^[\\p{L}\\s.,'()-]+$",
			message = "El país solo puede contener letras, espacios y caracteres especiales básicos")
	private String pais;

	@DecimalMin(value = "-90.0", message = "La latitud mínima es -90")
	@DecimalMax(value = "90.0", message = "La latitud máxima es 90")
	private Double latitud;

	@DecimalMin(value = "-180.0", message = "La longitud mínima es -180")
	@DecimalMax(value = "180.0", message = "La longitud máxima es 180")
	private Double longitud;

	private boolean activa;

	private Set<SedeParametroDTO> parametros;
}