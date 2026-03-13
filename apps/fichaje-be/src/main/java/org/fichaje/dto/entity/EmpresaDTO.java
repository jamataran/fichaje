package org.fichaje.dto.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaDTO {

	private Long id;
	private String nombre;
	private String cif;
	private boolean activa;

}
