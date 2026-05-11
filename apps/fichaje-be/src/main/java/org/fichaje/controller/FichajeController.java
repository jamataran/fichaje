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

import org.fichaje.dto.entity.FichajeDto;
import org.fichaje.dto.entity.FichajeDtoReqRes;
import org.fichaje.provider.db.entity.Fichaje;
import org.fichaje.service.FichajeService;
import org.fichaje.dto.entity.Mensaje;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/fichaje")
public class FichajeController
		extends CommonController<Fichaje, FichajeService> {

	public FichajeController(FichajeService service) {
		super(service);
	}

	@PostMapping("/now")
	public ResponseEntity<?> nuevoFichajeNow(
			@RequestBody FichajeDtoReqRes fichajeDto) {
		FichajeDtoReqRes result = service.fichar(fichajeDto);
		if (result == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new Mensaje("No se ha podido realizar el fichaje: Usuario no encontrado o sin empresa asignada"));
		}
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(result);
	}

	@Operation(summary = "Obtiene una lista paginada y filtrada de fichajes")
	@PostMapping("/pagesFiltered")
	public ResponseEntity<Page<Fichaje>> pageDtoSpec(
			@RequestBody FichajeDto dto,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "id") String order,
			@RequestParam(defaultValue = "true") boolean asc) {

		Page<Fichaje> entities = service.getFilteredPages(dto, page, size, order, asc);
		return ResponseEntity.ok(entities);
	}

	@Operation(summary = "Obtiene una lista filtrada de fichajes")
	@PostMapping("/listFiltered")
	public ResponseEntity<List<Fichaje>> filteredList(
			@RequestBody FichajeDto dto) {

		List<Fichaje> entities = service.getFilteredList(dto);
		return ResponseEntity.ok(entities);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editFichaje(@RequestBody FichajeDto editar,
			@PathVariable Long id) {
		return service.findById(id).map(x -> {
			x.setDia(editar.getDia());
			x.setHora(editar.getHora());
			x.setTipo(editar.getTipo());
			return ResponseEntity.ok(service.save(x));
		}).orElseGet(() -> ResponseEntity.notFound().build());
	}

}
