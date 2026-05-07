package org.fichaje.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.fichaje.converter.FichajeDtoConverter;
import org.fichaje.dto.entity.FichajeDto;
import org.fichaje.dto.entity.FichajeDtoReqRes;
import org.fichaje.provider.db.entity.Fichaje;
import org.fichaje.provider.db.entity.RrhhDto;
import org.fichaje.service.SecurityService;
import org.fichaje.service.FichajeService;
import org.fichaje.provider.db.specifications.FichajeSpecifications;

import io.swagger.v3.oas.annotations.Operation;

import org.fichaje.util.SecurityUtils;

import org.fichaje.dto.entity.Mensaje;

@RestController
@RequestMapping("/fichaje")
//@CrossOrigin(origins = "http://localhost:4200")
public class FichajeController
		extends CommonController<Fichaje, FichajeService> {

	@Autowired
	FichajeDtoConverter dtoConverter;
	@Autowired
	FichajeSpecifications specifications;
	@Autowired
    SecurityService securityService;

	@PostMapping("/now")
	public ResponseEntity<?> nuevoFichajeNow(
			@RequestBody FichajeDtoReqRes fichajeDto) {
		System.out.println("Fichaje");
		FichajeDtoReqRes result = dtoConverter.fichar(fichajeDto);
		if (result == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(new Mensaje("No se ha podido realizar el fichaje: Usuario no encontrado o sin empresa asignada"));
		}
		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(result);

	}

	@Operation(summary = "Obtiene una lista paginada y filtrada de objetos, el filtro se realiza a través de un DTO de ejemplo")
	@PostMapping("/pagesFiltered")
	public ResponseEntity<Page<Fichaje>> pageDtoSpec(
			@RequestBody FichajeDto dto,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "id") String order,
			@RequestParam(defaultValue = "true") boolean asc) {

		Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
		String currentUserNumber = SecurityUtils.getCurrentUserNumber();
		boolean isRrhh = SecurityUtils.isRRHH();
		boolean isSuperAdmin = SecurityUtils.isSuperAdmin();

		// Si NO es RRHH ni SuperAdmin, solo puede ver sus propios fichajes
		if (!isRrhh && !isSuperAdmin) {
			dto.setNumeroUsuario(currentUserNumber);
		}

		Specification<Fichaje> spec = (root, query, cb) -> cb.conjunction();

		if (dto.getNombreUsuario() != null) {
			spec = spec.and(specifications.nombreUsuarioContains(dto.getNombreUsuario()));
		}
		if (dto.getNumeroUsuario() != null) {
			spec = spec.and(specifications.numeroUsuarioContains(dto.getNumeroUsuario()));
		}
		if (dto.getTipo() != null) {
			spec = spec.and(specifications.tipoContains(dto.getTipo()));
		}
		if (dto.getHoraDesde() != null) {
			spec = spec.and(specifications.horaMayorQue(dto.getHoraDesde()));
		}
		if (dto.getHoraHasta() != null) {
			spec = spec.and(specifications.horaMenorQue(dto.getHoraHasta()));
		}
		if (dto.getDiaDesde() != null) {
			spec = spec.and(specifications.diaMayorQue(dto.getDiaDesde()));
		}
		if (dto.getDiaHasta() != null) {
			spec = spec.and(specifications.diaMenorQue(dto.getDiaHasta()));
		}

		// Aislamiento Multi-empresa: Siempre filtrar por la empresa del token (salvo superadmin sin empresa seleccionada)
		if (currentEmpresaId != null) {
			spec = spec.and(specifications.hasEmpresa(currentEmpresaId));
		} else if (!isSuperAdmin) {
			// Si no es superadmin y no hay empresaId en el token, no debería ver nada o solo lo suyo si aplica
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		Page<Fichaje> entities = service.pagesAndSpec(
				spec,
				PageRequest.of(page, size, asc ? Sort.by(order).ascending() : Sort.by(order).descending()));

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(entities);
	}

	@Operation(summary = "Obtiene una lista filtrada de objetos, el filtro se realiza a través de un DTO de ejemplo")
	@PostMapping("/listFiltered")
	public ResponseEntity<List<Fichaje>> filteredList(
			@RequestBody FichajeDto dto) {

		Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
		String currentUserNumber = SecurityUtils.getCurrentUserNumber();
		boolean isRrhh = SecurityUtils.isRRHH();
		boolean isSuperAdmin = SecurityUtils.isSuperAdmin();

		// Si NO es RRHH ni SuperAdmin, solo puede ver sus propios fichajes
		if (!isRrhh && !isSuperAdmin) {
			dto.setNumeroUsuario(currentUserNumber);
		}

		Specification<Fichaje> spec = (root, query, cb) -> cb.conjunction();

		if (dto.getNombreUsuario() != null) {
			spec = spec.and(specifications.nombreUsuarioContains(dto.getNombreUsuario()));
		}
		if (dto.getNumeroUsuario() != null) {
			spec = spec.and(specifications.numeroUsuarioContains(dto.getNumeroUsuario()));
		}
		if (dto.getTipo() != null) {
			spec = spec.and(specifications.tipoContains(dto.getTipo()));
		}
		if (dto.getHoraDesde() != null) {
			spec = spec.and(specifications.horaMayorQue(dto.getHoraDesde()));
		}
		if (dto.getHoraHasta() != null) {
			spec = spec.and(specifications.horaMenorQue(dto.getHoraHasta()));
		}
		if (dto.getDiaDesde() != null) {
			spec = spec.and(specifications.diaMayorQue(dto.getDiaDesde()));
		}
		if (dto.getDiaHasta() != null) {
			spec = spec.and(specifications.diaMenorQue(dto.getDiaHasta()));
		}

		// Aislamiento Multi-empresa
		if (currentEmpresaId != null) {
			spec = spec.and(specifications.hasEmpresa(currentEmpresaId));
		} else if (!isSuperAdmin) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		List<Fichaje> entities = service.filterAndList(spec);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(entities);
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> editFichaje(@RequestBody FichajeDto editar,
			@PathVariable Long id) {
		return service.findById(id).map(x -> {
			x.setDia(editar.getDia());
			x.setHora(editar.getHora());
			x.setTipo(editar.getTipo());
			return ResponseEntity.ok(service.save(x));
		}).orElseGet(() -> {
			return ResponseEntity.notFound().build();
		});
	}

}
