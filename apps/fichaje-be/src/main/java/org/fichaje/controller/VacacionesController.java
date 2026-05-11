package org.fichaje.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.fichaje.dto.entity.VacacionesDto;
import org.fichaje.dto.entity.VacacionesDtoFilter;
import org.fichaje.provider.db.entity.Vacaciones;
import org.fichaje.service.VacacionesService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/vacaciones")
public class VacacionesController
		extends CommonController<Vacaciones, VacacionesService> {

	public VacacionesController(VacacionesService service) {
		super(service);
	}

	@PostMapping("/create")
	public ResponseEntity<?> newVacaciones(@RequestBody VacacionesDto dto) {
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(service.createVacaciones(dto));
	}

	@PutMapping("/aprobar/{id}")
	public ResponseEntity<?> aprobar(@PathVariable Long id) {
		return ResponseEntity.ok(service.aprobar(id));
	}

	@PutMapping("/denegar/{id}")
	public ResponseEntity<?> denegar(@PathVariable Long id) {
		return ResponseEntity.ok(service.denegar(id));
	}

	@Operation(summary = "Obtiene una lista paginada y filtrada de objetos, el filtro se realiza a través de un DTO de ejemplo")
	@PostMapping("/pagesFiltered")
	public ResponseEntity<Page<Vacaciones>> pageDtoSpec(
			@RequestBody VacacionesDtoFilter dto,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "id") String order,
			@RequestParam(defaultValue = "true") boolean asc) {

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(service.getPageFiltered(dto, page, size, order, asc));
	}

	@Operation(summary = "Obtiene una lista filtrada de objetos, el filtro se realiza a través de un DTO de ejemplo")
	@PostMapping("/listFiltered")
	public ResponseEntity<List<Vacaciones>> filteredList(
			@RequestBody VacacionesDtoFilter dto) {

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(service.getListFiltered(dto));
	}
}
