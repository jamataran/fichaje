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
public class EmpresaDTO {

	private Long id;

	@NotBlank(message = "El nombre de la empresa es obligatorio")
	@Pattern(regexp = "^[\\p{L}\\s.,'&()-]+$",
			message = "El nombre solo puede contener letras, espacios y caracteres especiales básicos")
	private String nombre;

	@Pattern(regexp = "^[\\p{L}\\s.,'&()-]+$",
			message = "La razón social solo puede contener letras, espacios y caracteres especiales básicos")
	private String razonSocial;

	@NotBlank(message = "El CIF es obligatorio")
	@Pattern(regexp = "^[ABCDEFGHJKLMNPQRSUVW][0-9]{7}[0-9A-J]$",
			message = "El formato inicial del CIF es incorrecto")
	private String cif;

	private boolean activa;

	private Set<EmpresaParametroDTO> parametros;

	private Set<SedeDTO> sedes;
}