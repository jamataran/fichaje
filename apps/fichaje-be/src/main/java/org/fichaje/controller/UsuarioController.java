package org.fichaje.controller;

import org.fichaje.dto.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import org.fichaje.converter.UsuarioDtoConverter;
import org.fichaje.dto.entity.UsuarioDTO;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.entity.UsuarioPrincipal;
import org.fichaje.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

import org.fichaje.util.SecurityUtils;

@RestController
@RequestMapping("/usuario")
public class UsuarioController
		extends CommonController<Usuario, UsuarioService> {

	public UsuarioController(UsuarioService service) {
		super(service);
	}

	@Operation(summary = "Punto único de obtención de usuarios: permite listado, paginación y filtrado mediante query params")
	@GetMapping
	public ResponseEntity<Page<UsuarioDTO>> list(
			UsuarioDtoFilter filter,
			@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
		
		return ResponseEntity.ok(service.getUsuariosPaged(filter, pageable));
	}

	@Override
	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable Long id) {
		return ResponseEntity.ok(service.getUsuarioById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editUser(@RequestBody UsuarioDtoEdit editar,
			@PathVariable Long id) {
		return ResponseEntity.ok(service.updateUsuario(id, editar));
	}

	@PutMapping("password/{id}")
	public ResponseEntity<?> editUserPassword(@RequestBody UsuarioDtoEditPassword editar,
			@PathVariable Long id) {
		return ResponseEntity.ok(service.updatePassword(id, editar));
	}

	@PutMapping("/suma_vacaciones_plantilla/{dias}")
	public ResponseEntity<?> sumarVacacionesPlantilla(@PathVariable int dias) {
		service.sumVacacionesPlantilla(dias);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Usuario obtiene la información de su usario")
	@GetMapping("/miusuario")
	public ResponseEntity<UsuarioDTO> getYourUser() {
		return ResponseEntity.ok(service.miUsuario());
	}

	@Operation(summary = "Asigna una sede a un usuario")
	@PostMapping("/{id}/sedes/{sedeId}")
	public ResponseEntity<UsuarioDTO> addSede(@PathVariable Long id, @PathVariable Long sedeId) {
		// En un entorno multi-tenancy real, deberíamos validar que sedeId pertenece a la empresaId del token
		return ResponseEntity.ok(service.addSede(id, sedeId));
	}

	@Operation(summary = "Quita una sede a un usuario")
	@DeleteMapping("/{id}/sedes/{sedeId}")
	public ResponseEntity<?> removeSede(@PathVariable Long id, @PathVariable Long sedeId) {
		service.removeSede(id, sedeId);
		return ResponseEntity.noContent().build();
	}
}
