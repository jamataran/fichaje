package org.fichaje.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.fichaje.converter.EmpresaDtoConverter;
import org.fichaje.dto.entity.EmpresaDTO;
import org.fichaje.provider.db.entity.Empresa;
import org.fichaje.service.EmpresaService;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/empresas")
public class EmpresaController
		extends CommonController<Empresa, EmpresaService> {

	@Autowired
	EmpresaDtoConverter dtoConverter;

	@Operation(summary = "Crea una nueva empresa")
	@PostMapping
	public ResponseEntity<EmpresaDTO> newEmpresa(
			@RequestBody EmpresaDTO empresaDto) {
		Empresa empresaGuardada = service.save(dtoConverter.transform(empresaDto));
		EmpresaDTO responseDTO = dtoConverter.inverseTransform(empresaGuardada);

		URI    location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(empresaGuardada.getId())
				.toUri();

		return ResponseEntity.created(location).body(responseDTO);
	}

	@Operation(summary = "Devuelve una lista de DTO de empresas")
	@GetMapping
	public ResponseEntity<Page<EmpresaDTO>> list(Pageable pageable) {
		return ResponseEntity.ok(service.findAll(pageable).map(dtoConverter::inverseTransform));
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
			return ResponseEntity.noContent().build();
		}).orElseGet(() -> {
			return ResponseEntity.notFound().build();
		});
	}

}