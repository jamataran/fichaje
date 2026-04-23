package org.fichaje.controller;

import org.fichaje.dto.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.fichaje.converter.UsuarioDtoConverter;
import org.fichaje.dto.entity.UsuarioDTO;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.config.security.jwt.JwtProvider;
import org.fichaje.service.UsuarioService;
import org.fichaje.provider.db.specifications.UsuarioSpecifications;

import io.swagger.v3.oas.annotations.Operation;

import java.util.List;

@RestController
@RequestMapping("/usuario")
public class UsuarioController
		extends CommonController<Usuario, UsuarioService> {

	@Autowired
	UsuarioDtoConverter dtoConverter;
	@Autowired
	JwtProvider jwtProvider;
	@Autowired
	UsuarioSpecifications specifications;

	@Operation(summary = "Punto único de obtención de usuarios: permite listado, paginación y filtrado mediante query params")
	@GetMapping
	public ResponseEntity<Page<UsuarioDTO>> list(
			UsuarioDtoFilter filter,
			@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
		
		Specification<Usuario> spec = createSpec(filter);
		Page<UsuarioDTO> page = service.pagesAndSpec(spec, pageable)
				.map(usu -> dtoConverter.inverseTransform(usu));
		
		return ResponseEntity.ok(page);
	}

	private Specification<Usuario> createSpec(UsuarioDtoFilter dto) {
		Specification<Usuario> spec = Specification.where((root, query, criteriaBuilder) -> null);
		if (dto == null) return spec;

		if (dto.getNombreEmpleado() != null) {
			spec = spec.and(specifications.nombreUsuarioContains(dto.getNombreEmpleado()));
		}
		if (dto.getEmail() != null) {
			spec = spec.and(specifications.emailUsuarioContains(dto.getEmail()));
		}
		if (dto.getNumero() != null) {
			spec = spec.and(specifications.numeroUsuarioContains(dto.getNumero()));
		}
		if (dto.getDni() != null) {
			spec = spec.and(specifications.dniUsuarioContains(dto.getDni()));
		}
		if (dto.getWorking() != null) {
			spec = spec.and(specifications.isWorking(dto.getWorking()));
		}
		if (dto.getEnVacaciones() != null) {
			spec = spec.and(specifications.isEnVacaciones(dto.getEnVacaciones()));
		}
		if (dto.getDeBaja() != null) {
			spec = spec.and(specifications.isDeBaja(dto.getDeBaja()));
		}
		if (dto.getDiasVacacionesDesde() != null) {
			spec = spec.and(specifications.diasDesde(dto.getDiasVacacionesDesde()));
		}
		if (dto.getDiasVacacionesHasta() != null) {
			spec = spec.and(specifications.diasHasta(dto.getDiasVacacionesHasta()));
		}
		if (dto.getHorasGeneradasDesde() != null) {
			spec = spec.and(specifications.horasDesde(dto.getHorasGeneradasDesde()));
		}
		if (dto.getHorasGeneradasHasta() != null) {
			spec = spec.and(specifications.horasHasta(dto.getHorasGeneradasHasta()));
		}
		if (dto.getEmpresaId() != null) {
			spec = spec.and(specifications.hasEmpresa(dto.getEmpresaId()));
		}
		if (dto.getSedeId() != null) {
			spec = spec.and(specifications.hasSede(dto.getSedeId()));
		}
		return spec;
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editUser(@RequestBody UsuarioDtoEdit editar,
			@PathVariable Long id) {

		return service.findById(id).map(d -> {
			dtoConverter.transformEdit(d, editar);
			return ResponseEntity.ok(service.save(d));
		}).orElseGet(() -> {
			return ResponseEntity.notFound().build();
		});
	}

	@PutMapping("password/{id}")
	public ResponseEntity<?> editUserPassword(@RequestBody UsuarioDtoEditPassword editar,
			@RequestHeader("authorization") String token,
			@PathVariable Long id) {

		token = token.replace("Bearer ", "");
		Usuario usuario = service.findById(id).orElse(null);
		if (usuario != null) {
			if (jwtProvider.validateToken(token)
					&& jwtProvider.getSubjectFromToken(token).equals(usuario.getNumero())) {
				usuario = dtoConverter.transformEditPassword(usuario, editar);
				return ResponseEntity.ok(service.save(usuario));
			} else {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body(new Mensaje("No puedes cambiar la contraseña de otro usuario"));
			}
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/suma_vacaciones_plantilla/{dias}")
	public ResponseEntity<?> sumarVacacionesPlantilla(@PathVariable int dias) {
		service.list().stream().forEach(u -> {
			u.setDiasVacaciones(u.getDiasVacaciones() + dias);
			service.save(u);
		});
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "Usuario obtiene la información de su usario")
	@GetMapping("/miusuario")
	public ResponseEntity<?> getYourUser(@RequestHeader("authorization") String token) {
		token = token.replace("Bearer ", "");
		if (jwtProvider.validateToken(token)) {
			String numeroUsuario = jwtProvider.getSubjectFromToken(token);
			Usuario usuario = service.findByNumero(numeroUsuario).orElse(null);
			if (usuario != null) {
				return ResponseEntity.ok(usuario);
			} else {
				return ResponseEntity.notFound().build();
			}
		} else {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(new Mensaje("Solo puedes acceder a la información de tú usuario"));
		}
	}

	@Operation(summary = "Asigna una sede a un usuario")
	@PostMapping("/{id}/sedes/{sedeId}")
	public ResponseEntity<UsuarioDTO> addSede(@PathVariable Long id, @PathVariable Long sedeId) {
		return ResponseEntity.ok(service.addSede(id, sedeId));
	}

	@Operation(summary = "Quita una sede a un usuario")
	@DeleteMapping("/{id}/sedes/{sedeId}")
	public ResponseEntity<?> removeSede(@PathVariable Long id, @PathVariable Long sedeId) {
		service.removeSede(id, sedeId);
		return ResponseEntity.noContent().build();
	}

	@Operation(summary = "Lista las sedes de un usuario")
	@GetMapping("/{id}/sedes")
	public ResponseEntity<List<SedeDTO>> listSedes(@PathVariable Long id) {
		return ResponseEntity.ok(service.listSedes(id));
	}

	@Operation(summary = "Lista todas las empresas de un usuario")
	@GetMapping("/{id}/empresas")
	public ResponseEntity<List<EmpresaDTOWithoutSedes>> listEmpresas(@PathVariable Long id) {
		return ResponseEntity.ok(service.findEmpresasByUsuarioIdWithoutSedes(id));
	}
}
