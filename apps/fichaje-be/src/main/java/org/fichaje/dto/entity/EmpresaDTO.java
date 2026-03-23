package org.fichaje.dto.entity;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaDTO {

	private Long id;

	@NotBlank(message = "El nombre de la empresa es obligatorio")
	private String nombre;

	private String razonSocial;

	@NotBlank(message = "El CIF es obligatorio")
	@Pattern(regexp = "^[ABCDEFGHJKLMNPQRSUVW][0-9]{7}[0-9A-J]$",
			message = "El formato inicial del CIF es incorrecto")
	private String cif;

	private boolean activa;

	@Email(message = "El formato del email no es válido")
	private String email;

	@Pattern(regexp = "^[+]?[0-9\\s()]{6,20}$",
			message = "El formato del teléfono no es válido")
	private String telefono;

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

	private List<EmpresaParametroDTO> parametros = new ArrayList<>();
}