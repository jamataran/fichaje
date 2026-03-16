package org.fichaje.dto.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaDTO {

	private Long id;

	@NotNull
	private String nombre;

	@NotNull
	@Pattern(regexp = "^[ABCDEFGHJKLMNPQRSUVW]{1}[0-9]{7}[0-9A-J]{1}$",
			message = "El CIF no tiene un formato válido")
	private String cif;

	private boolean activa;

}
