package org.fichaje.dto.entity;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}