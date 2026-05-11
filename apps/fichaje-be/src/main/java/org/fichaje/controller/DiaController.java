package org.fichaje.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.fichaje.dto.entity.DiaDto;
import org.fichaje.provider.db.entity.DiaLaborable;
import org.fichaje.service.DiaLaborableService;

@RestController
@RequestMapping("/dia")
public class DiaController
		extends CommonController<DiaLaborable, DiaLaborableService> {

	@PostMapping("/create/{idCalendario}")
	public ResponseEntity<?> newDia(@RequestBody DiaDto diaDTO,
			@PathVariable Long idCalendario) {
		return service.createDia(diaDTO, idCalendario)
				.map(dia -> ResponseEntity.status(HttpStatus.CREATED).body(dia))
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@PostMapping("/create_dias")
	public ResponseEntity<?> newListaDias(@RequestBody List<DiaDto> listaDias) {
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(service.createDias(listaDias));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editDia(@RequestBody DiaDto editar,
			@PathVariable Long id) {
		return service.updateDia(id, editar)
				.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

}
