package org.fichaje.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.fichaje.converter.EmpresaDtoConverter;
import org.fichaje.dto.entity.EmpresaDto;
import org.fichaje.dto.entity.Mensaje;
import org.fichaje.provider.db.entity.Empresa;
import org.fichaje.service.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/empresa")
//@CrossOrigin(origins = "http://localhost:4200")
public class EmpresaController
		extends CommonController<Empresa, EmpresaService> {

	@Autowired
	EmpresaDtoConverter dtoConverter;

	@Operation(summary = "Crea una nueva empresa")
	@PostMapping("/create")
	public ResponseEntity<?> newEmpresa(
			@RequestBody EmpresaDto empresaDto) {
		service.save(dtoConverter.transform(empresaDto));

//		return ResponseEntity.created(null).build();
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new Mensaje("Empresa creada"));

//		return ResponseEntity.status(HttpStatus.CREATED)
//				.body(service.save(dtoConverter.transform(calendarioDto)));
	}

	@Operation(summary = "Devuelve una lista de DTO de empresas")
	@GetMapping("/list/dto")
	public ResponseEntity<List<EmpresaDto>> listDto() {
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(service.list().stream()
						.map(c -> dtoConverter.inverseTransform(c))
						.collect(Collectors.toList()));

	}

	@Operation(summary = "Edita una empresa")
	@PutMapping("/{id}")
	public ResponseEntity<?> editEmpresa(@RequestBody Empresa editar,
			@PathVariable Long id) {

		return service.findById(id).map(c -> {
			c.setId(editar.getId());
			c.setNombre(editar.getNombre());
			c.setCif(editar.getCif());
			c.setActiva(editar.isActiva());

			return ResponseEntity.ok(service.save(c));
		}).orElseGet(() -> {
			return ResponseEntity.notFound().build();
		});
	}

	@Override
	@Operation(summary = "Elimina una empresa")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		return service.findById(id).map(c -> {
			service.delete(id);
			// return ResponseEntity.ok(new Mensaje("Empresa eliminada"));
			return ResponseEntity.status(HttpStatus.CREATED)
					.body(new Mensaje("Empresa eliminada"));
		}).orElseGet(() -> {
			return ResponseEntity.notFound().build();
		});
	}

}
