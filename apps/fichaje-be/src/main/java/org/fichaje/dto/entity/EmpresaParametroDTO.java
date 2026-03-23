package org.fichaje.dto.entity;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fichaje.provider.db.entity.enums.EmpresaParametroClave;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaParametroDTO {

	private Long id;

	private Long empresaId;

	@NotNull(message = "La clave del parámetro es obligatoria")
	private EmpresaParametroClave clave;

	@NotBlank(message = "El valor del parámetro es obligatorio")
	private String valor;
}