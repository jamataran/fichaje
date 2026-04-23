package org.fichaje.dto.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

	private Long id;

	private String email;
	private String password;
	private String numero;
	private String nombreEmpleado;
	private String dni;

	private List<String> roles;

	private Integer diasVacaciones;

	private Double horasGeneradas;

	private Boolean enVacaciones;

	private Boolean deBaja;

	private Boolean working;

	private List<SedeDTO> sedes;
	private List<EmpresaDTOWithoutSedes> empresas;

	private Long sedeId;

}
