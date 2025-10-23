package org.fichaje.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.fichaje.converter.DiaDtoConverter;
import org.fichaje.dto.entity.DiaDto;
import org.fichaje.provider.db.entity.DiaLaborable;
import org.fichaje.service.DiaLaborableService;

@RestController
@RequestMapping("/dia")
//@CrossOrigin(origins = "http://localhost:4200")
public class DiaController
		extends CommonController<DiaLaborable, DiaLaborableService> {

	@Autowired
	DiaDtoConverter dtoConverter;

	@PostMapping("/create/{idCalendario}")
	public ResponseEntity<?> newDia(@RequestBody DiaDto diaDTO,
			@PathVariable Long idCalendario) {
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(service.save(dtoConverter.transformById(diaDTO, idCalendario)));
	}

	@PostMapping("/create_dias")
	public ResponseEntity<?> newListaDias(@RequestBody List<DiaDto> listaDias) {

		// forma vieja
//		listaDias.stream()
//				.forEach(dia -> service.save(dtoConverter.transform(dia)));
//		return ResponseEntity.status(HttpStatus.OK).build();
//		listaDias.stream().map(d->dtoConverter.transform(d)).collect(Collectors.toList());

		// forma nueva
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(service.saveAll(
						listaDias.stream().map(d -> dtoConverter.transform(d))
								.collect(Collectors.toList())));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editDia(@RequestBody DiaDto editar,
			@PathVariable Long id) {

		return service.findById(id).map(d -> {
			d.setDia(editar.getDia());
			d.setHoraInicio(editar.getHoraInicio());
			d.setHoraFin(editar.getHoraFin());
			return ResponseEntity.ok(service.save(d));
		}).orElseGet(() -> {
			return ResponseEntity.notFound().build();
		});
	}

}
