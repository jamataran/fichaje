package org.fichaje.dto.entity;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarioDto {

	private long id;
	@Schema(description = "Nombre del calendario", example = "calendario_2021")
	private String nombre;
	private int year;
	// private boolean active;
	private int minutosMasEntrada;
	private int minutosMenosEntrada;
	private List<DiaDto> dias;

}
