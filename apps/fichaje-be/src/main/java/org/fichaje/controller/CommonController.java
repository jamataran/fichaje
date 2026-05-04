package org.fichaje.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.fichaje.provider.db.entity.TenantEntity;
import org.fichaje.service.CommonService;
import org.fichaje.util.SecurityUtils;
import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.Operation;

public class CommonController<E, S extends CommonService<E>> {

	@Autowired
	protected S service;

//
//	@Operation(summary = "Devuelve una lista con paginación de los objetos")
//	@GetMapping("/pages")
//	public ResponseEntity<Page<E>> pages(
//			@RequestParam(defaultValue = "0") int page,
//			@RequestParam(defaultValue = "20") int size,
//			@RequestParam(defaultValue = "id") String order,
//			@RequestParam(defaultValue = "true") boolean asc) {
//
//		Page<E> entities = service.pages(
//				PageRequest.of(page, size, Sort.by(order)));
//		if (!asc)
//			entities = service.pages(
//					PageRequest.of(page, size, Sort.by(order).descending()));
//
//		return new ResponseEntity<Page<E>>(entities, HttpStatus.OK);
//
//	}

	@Operation(summary = "Obtiene el objeto con el id indicado")
	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable Long id) {
		E result = service.findById(id).orElse(null);
		if (result == null)
			return ResponseEntity.notFound().build();

		if (result instanceof TenantEntity tenantEntity) {
			Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
			boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
			if (!isSuperAdmin && currentEmpresaId != null && tenantEntity.getEmpresa() != null &&
					!tenantEntity.getEmpresa().getId().equals(currentEmpresaId)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		}

		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Borra el objeto con el id indicado")
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		E result = service.findById(id).orElse(null);
		if (result != null && result instanceof TenantEntity tenantEntity) {
			Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
			boolean isSuperAdmin = SecurityUtils.isSuperAdmin();
			if (!isSuperAdmin && currentEmpresaId != null && tenantEntity.getEmpresa() != null &&
					!tenantEntity.getEmpresa().getId().equals(currentEmpresaId)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
		}

		service.delete(id);
		return ResponseEntity.ok().build();
	}
}
