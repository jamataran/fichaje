package org.fichaje.controller;

import org.fichaje.service.CommonService;
import org.fichaje.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;

public abstract class CommonController<E, S extends CommonService<E>> {

	protected final S service;

	protected CommonController(S service) {
		this.service = service;
	}

	@Operation(summary = "Devuelve una lista con paginación de los objetos")
	@GetMapping("/pages")
	public ResponseEntity<Page<E>> pages(Pageable pageable) {
		return ResponseEntity.ok(service.pages(pageable));
	}

	@Operation(summary = "Obtiene el objeto con el id indicado")
	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable Long id) {
		return service.findById(id)
				.map(entity -> {
					SecurityUtils.checkTenantAccess(entity);
					return ResponseEntity.ok(entity);
				})
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
	}

	@Operation(summary = "Borra el objeto con el id indicado")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.findById(id).ifPresent(entity -> {
			SecurityUtils.checkTenantAccess(entity);
			service.delete(id);
		});
		return ResponseEntity.noContent().build();
	}
}
