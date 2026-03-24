package org.fichaje.dto.entity;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaParametroDTO {

	private Long id;

	private Long empresaId;

	@NotBlank(message = "La clave del parámetro es obligatoria")
	private String clave;

	@NotBlank(message = "El valor del parámetro es obligatorio")
	private String valor;
}