package org.fichaje.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.fichaje.dto.entity.CalendarioDto;
import org.fichaje.dto.entity.Mensaje;
import org.fichaje.provider.db.entity.Calendario;
import org.fichaje.service.CalendarioService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/calendario")
public class CalendarioController
		extends CommonController<Calendario, CalendarioService> {

	public CalendarioController(CalendarioService service) {
		super(service);
	}

	@Operation(summary = "Crea un nuevo calendario")
	@PostMapping("/create")
	public ResponseEntity<?> newCalendario(
			@RequestBody CalendarioDto calendarioDto) {

		service.createCalendario(calendarioDto);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new Mensaje("Calendario creado"));
	}

	@Operation(summary = "Devuelve una lista de DTO de calendarios")
	@GetMapping("/list/dto")
	public ResponseEntity<List<CalendarioDto>> listDto() {
		return ResponseEntity.ok(service.getCalendariosDto());
	}

	@Operation(summary = "Edita un calendario")
	@PutMapping("/{id}")
	public ResponseEntity<?> editCalendario(@RequestBody CalendarioDto editar,
			@PathVariable Long id) {

		return service.updateCalendario(id, editar)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN).build());
	}

}
