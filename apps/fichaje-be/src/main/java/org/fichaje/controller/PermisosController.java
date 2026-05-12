package org.fichaje.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.fichaje.dto.entity.ChartDataDto;
import org.fichaje.dto.entity.PermisoDto;
import org.fichaje.dto.entity.PermisoDtoFilter;
import org.fichaje.dto.interfaces.IUsuarioDtoEstadistica;
import org.fichaje.provider.db.entity.Permiso;
import org.fichaje.service.PermisoService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permiso")
public class PermisosController extends CommonController<Permiso, PermisoService> {

	public PermisosController(PermisoService service) {
		super(service);
	}

	@PostMapping("/create")
	public ResponseEntity<Permiso> newPermiso(@RequestBody PermisoDto dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(service.createPermiso(dto));
	}

	@PutMapping("/aprobar/{id}")
	public ResponseEntity<Permiso> aprobar(@PathVariable Long id) {
		return ResponseEntity.ok(service.aprobar(id));
	}

	@PutMapping("/denegar/{id}")
	public ResponseEntity<Permiso> denegar(@PathVariable Long id) {
		return ResponseEntity.ok(service.denegar(id));
	}

	@Operation(summary = "Obtiene una lista paginada y filtrada de objetos")
	@PostMapping("/pagesFiltered")
	public ResponseEntity<Page<Permiso>> pageDtoSpec(
			@RequestBody PermisoDtoFilter dto,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "id") String order,
			@RequestParam(defaultValue = "true") boolean asc) {
		return ResponseEntity.ok(service.getPageFiltered(dto, page, size, order, asc));
	}

	@Operation(summary = "Obtiene una lista filtrada de objetos")
	@PostMapping("/listFiltered")
	public ResponseEntity<List<Permiso>> filteredList(@RequestBody PermisoDtoFilter dto) {
		return ResponseEntity.ok(service.getListFiltered(dto));
	}

	@Operation(summary = "Obtiene el número de permisos de los últimos 12 meses")
	@GetMapping("/count")
	public ResponseEntity<ChartDataDto> countLast12Months() {
		return ResponseEntity.ok(service.numberOfPermisosLast12Months());
	}

	@Operation(summary = "Obtiene el número de permisos por usuario de los últimos 12 meses")
	@GetMapping("/count/users")
	public ResponseEntity<List<IUsuarioDtoEstadistica>> countUsersLast12Months() {
		return ResponseEntity.ok(service.numberOfPermisosPerUserLast12Months());
	}

}
