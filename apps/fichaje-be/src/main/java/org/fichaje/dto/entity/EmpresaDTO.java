package org.fichaje.dto.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaDTO {

	private Long id;

	@NotBlank(message = "El nombre de la empresa es obligatorio")
	private String nombre;

	@NotBlank(message = "El CIF es obligatorio")
	@Pattern(regexp = "^[ABCDEFGHJKLMNPQRSUVW][0-9]{7}[0-9A-J]$",
			message = "El formato inicial del CIF es incorrecto")
	private String cif;

	private boolean activa;
}