package org.fichaje.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.fichaje.dto.entity.IncidenciaDtoEdit;
import org.fichaje.dto.entity.IncidenciaDtoFilter;
import org.fichaje.provider.db.entity.Incidencia;
import org.fichaje.service.IncidenciaService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/incidencia")
public class IncidenciaController
		extends CommonController<Incidencia, IncidenciaService> {

	public IncidenciaController(IncidenciaService service) {
		super(service);
	}

	@Operation(summary = "Obtiene una lista paginada y filtrada de objetos")
	@PostMapping("/pagesFiltered")
	public ResponseEntity<Page<Incidencia>> pageDtoSpec(
			@RequestBody IncidenciaDtoFilter dto,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "id") String order,
			@RequestParam(defaultValue = "true") boolean asc) {

		return ResponseEntity.ok(service.getFilteredPages(dto, page, size, order, asc));
	}

	@Operation(summary = "Obtiene una lista filtrada de objetos")
	@PostMapping("/listFiltered")
	public ResponseEntity<List<Incidencia>> filteredList(@RequestBody IncidenciaDtoFilter dto) {

		return ResponseEntity.ok(service.getFilteredList(dto));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editIncidencia(@RequestBody IncidenciaDtoEdit editar,
			@PathVariable Long id) {

		return service.updateIncidencia(id, editar)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@Operation(summary = "Obtiene el número de incidencias de los últimos 12 meses")
	@GetMapping("/count")
	public ResponseEntity<?> countLast12Months() {
		return ResponseEntity.ok(service.getStatsLast12Months());
	}

	@Operation(summary = "Obtiene el número de incidencias por usuario de los últimos 12 meses")
	@GetMapping("/count/users")
	public ResponseEntity<?> countUsersLast12Months() {
		return ResponseEntity.ok(service.getStatsPerUserLast12Months());
	}

	@Operation(summary = "Obtiene el número de incidencias agrupadas por resumen de los últimos 12 meses")
	@GetMapping("/count/top")
	public ResponseEntity<?> topIncidenciasLast12Months() {
		return ResponseEntity.ok(service.getTopStatsLast12Months());
	}
}
