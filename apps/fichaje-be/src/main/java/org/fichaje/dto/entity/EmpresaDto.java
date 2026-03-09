package org.fichaje.dto.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaDto {

	private long id;
	private String nombre;
	private String cif;
	private boolean activa;

}
