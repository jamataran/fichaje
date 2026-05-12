package org.fichaje.dto.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SedeParametroDTO {

	private Long id;

	private Long sedeId;

	@NotBlank(message = "La clave del parámetro es obligatoria")
	private String clave;

	@NotBlank(message = "El valor del parámetro es obligatorio")
	private String valor;
}