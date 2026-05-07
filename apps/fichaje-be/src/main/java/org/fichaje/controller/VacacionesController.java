package org.fichaje.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.fichaje.converter.VacacionesDtoConverter;
import org.fichaje.dto.entity.Mensaje;
import org.fichaje.dto.entity.VacacionesDto;
import org.fichaje.dto.entity.VacacionesDtoFilter;
import org.fichaje.provider.db.entity.Vacaciones;
import org.fichaje.provider.db.entity.enums.EstadosPeticion;
import org.fichaje.provider.mail.EmailService;
import org.fichaje.provider.db.entity.RrhhDto;
import org.fichaje.service.SecurityService;
import org.fichaje.service.VacacionesService;
import org.fichaje.service.NotificationService;
import org.fichaje.provider.db.specifications.VacacionesSpecifications;

import io.swagger.v3.oas.annotations.Operation;

import org.fichaje.util.SecurityUtils;

@RestController
@RequestMapping("/vacaciones")
//@CrossOrigin(origins = "http://localhost:4200")
public class VacacionesController
		extends CommonController<Vacaciones, VacacionesService> {

	@Autowired
	VacacionesDtoConverter dtoConverter;
	@Autowired
	EmailService emailService;
	@Autowired
	NotificationService notificationService;
	@Autowired
	VacacionesSpecifications specifications;

	@PostMapping("/create")
	public ResponseEntity<?> newVacaciones(@RequestBody VacacionesDto dto) {

		notificationService.sendNotification(dto.getNumeroUsuario(),
				"Petición de vacaciones",
				emailService.generateBodyForVacaciones(dto.getNombreUsuario(),
						dto.getNumeroUsuario(),
						dto.getInicio().toString(),
						dto.getFin().toString(),
						EstadosPeticion.PENDIENTE.toString()));

		Vacaciones vacaciones = dtoConverter.transform(dto);

		if (vacaciones != null) {
			return ResponseEntity
					.status(HttpStatus.CREATED)
					.body(service.save(vacaciones));

		} else {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(new Mensaje(
							"La fecha de fin debe ser posterior a la de inicio."));

		}

	}

	@PutMapping("/aprobar/{id}")
	public ResponseEntity<?> aprobar(@PathVariable Long id) {
		return service.findById(id).map(d -> {
			d.setAprobado(true);
			d.setEstado(EstadosPeticion.APROBADO.toString());

			notificationService.sendNotification(d.getUsuario().getNumero(),
					"Petición de vacaciones",
					emailService.generateBodyForVacaciones(
							d.getUsuario().getNombreEmpleado(),
							d.getUsuario().getNumero(),
							d.getInicio().toString(),
							d.getFin().toString(),
							EstadosPeticion.APROBADO.toString()));

			return ResponseEntity.ok(service.save(d));
		}).orElseGet(() -> {
			return ResponseEntity.notFound().build();
		});
	}

	@PutMapping("/denegar/{id}")
	public ResponseEntity<?> denegar(@PathVariable Long id) {
		return service.findById(id).map(d -> {
			d.setAprobado(false);
			d.setEstado(EstadosPeticion.DENEGADO.toString());

			notificationService.sendNotification(d.getUsuario().getNumero(),
					"Petición de vacaciones",
					emailService.generateBodyForVacaciones(
							d.getUsuario().getNombreEmpleado(),
							d.getUsuario().getNumero(),
							d.getInicio().toString(),
							d.getFin().toString(),
							EstadosPeticion.DENEGADO.toString()));

			return ResponseEntity.ok(service.save(d));
		}).orElseGet(() -> {
			return ResponseEntity.notFound().build();
		});
	}

	@Operation(summary = "Obtiene una lista paginada y filtrada de objetos, el filtro se realiza a través de un DTO de ejemplo")
	@PostMapping("/pagesFiltered")
	public ResponseEntity<Page<Vacaciones>> pageDtoSpec(
			@RequestBody VacacionesDtoFilter dto,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "id") String order,
			@RequestParam(defaultValue = "true") boolean asc) {

		Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
		String currentUserNumber = SecurityUtils.getCurrentUserNumber();
		boolean isRrhh = SecurityUtils.isRRHH();
		boolean isSuperAdmin = SecurityUtils.isSuperAdmin();

		// Si NO es RRHH ni SuperAdmin, filtrar por número de usuario
		if (!isRrhh && !isSuperAdmin) {
			dto.setUsuarioNumero(currentUserNumber);
		}

		Specification<Vacaciones> spec = (root, query, cb) -> cb.conjunction();

		if (dto.getUsuarioNombre() != null) {
			spec = spec.and(specifications.nombreUsuarioContains(dto.getUsuarioNombre()));
		}
		if (dto.getUsuarioNumero() != null) {
			spec = spec.and(specifications.numeroUsuarioContains(dto.getUsuarioNumero()));
		}
		if (dto.getUsuarioDni() != null) {
			spec = spec.and(specifications.dniUsuarioContains(dto.getUsuarioDni()));
		}
		if (dto.getUsuarioEmail() != null) {
			spec = spec.and(specifications.emailUsuarioContains(dto.getUsuarioEmail()));
		}
		if (dto.getConsumidas() != null) {
			spec = spec.and(specifications.areConsumidas(dto.getConsumidas()));
		}
		if (dto.getEstado() != null) {
			spec = spec.and(specifications.estadoContains(dto.getEstado()));
		}
		if (dto.getInicioDesde() != null) {
			spec = spec.and(specifications.inicioDesde(dto.getInicioDesde()));
		}
		if (dto.getInicioHasta() != null) {
			spec = spec.and(specifications.inicioHasta(dto.getInicioHasta()));
		}
		if (dto.getFinDesde() != null) {
			spec = spec.and(specifications.finDesde(dto.getFinDesde()));
		}
		if (dto.getFinHasta() != null) {
			spec = spec.and(specifications.finHasta(dto.getFinHasta()));
		}

		// Aislamiento Multi-empresa
		if (currentEmpresaId != null) {
			spec = spec.and(specifications.hasEmpresa(currentEmpresaId));
		} else if (!isSuperAdmin) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		Page<Vacaciones> entities = service.pagesAndSpec(
				spec,
				PageRequest.of(page, size, asc ? Sort.by(order).ascending() : Sort.by(order).descending()));

//		Page<VacacionesDto> entitiesDto = entities
//				.map(usu -> dtoConverter.inverseTransform(usu));

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(entities);

	}

	@Operation(summary = "Obtiene una lista filtrada de objetos, el filtro se realiza a través de un DTO de ejemplo")
	@PostMapping("/listFiltered")
	public ResponseEntity<List<Vacaciones>> filteredList(
			@RequestBody VacacionesDtoFilter dto) {

		Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
		String currentUserNumber = SecurityUtils.getCurrentUserNumber();
		boolean isRrhh = SecurityUtils.isRRHH();
		boolean isSuperAdmin = SecurityUtils.isSuperAdmin();

		// Si NO es RRHH ni SuperAdmin, filtrar por número de usuario
		if (!isRrhh && !isSuperAdmin) {
			dto.setUsuarioNumero(currentUserNumber);
		}

		Specification<Vacaciones> spec = (root, query, cb) -> cb.conjunction();

		if (dto.getUsuarioNombre() != null) {
			spec = spec.and(specifications.nombreUsuarioContains(dto.getUsuarioNombre()));
		}
		if (dto.getUsuarioNumero() != null) {
			spec = spec.and(specifications.numeroUsuarioContains(dto.getUsuarioNumero()));
		}
		if (dto.getUsuarioDni() != null) {
			spec = spec.and(specifications.dniUsuarioContains(dto.getUsuarioDni()));
		}
		if (dto.getUsuarioEmail() != null) {
			spec = spec.and(specifications.emailUsuarioContains(dto.getUsuarioEmail()));
		}
		if (dto.getConsumidas() != null) {
			spec = spec.and(specifications.areConsumidas(dto.getConsumidas()));
		}
		if (dto.getAprobado() != null) {
			spec = spec.and(specifications.areAprobadas(dto.getAprobado()));
		}
		if (dto.getEstado() != null) {
			spec = spec.and(specifications.estadoContains(dto.getEstado()));
		}
		if (dto.getInicioDesde() != null) {
			spec = spec.and(specifications.inicioDesde(dto.getInicioDesde()));
		}
		if (dto.getInicioHasta() != null) {
			spec = spec.and(specifications.inicioHasta(dto.getInicioHasta()));
		}
		if (dto.getFinDesde() != null) {
			spec = spec.and(specifications.finDesde(dto.getFinDesde()));
		}
		if (dto.getFinHasta() != null) {
			spec = spec.and(specifications.finHasta(dto.getFinHasta()));
		}

		// Aislamiento Multi-empresa
		if (currentEmpresaId != null) {
			spec = spec.and(specifications.hasEmpresa(currentEmpresaId));
		} else if (!isSuperAdmin) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		List<Vacaciones> entities = service.filterAndList(spec);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(entities);

	}
}
