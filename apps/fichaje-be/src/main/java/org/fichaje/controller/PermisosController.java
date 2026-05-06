package org.fichaje.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.fichaje.converter.PermisoDtoConverter;
import org.fichaje.dto.entity.ChartDataDto;
import org.fichaje.dto.entity.Mensaje;
import org.fichaje.dto.entity.PermisoDto;
import org.fichaje.dto.entity.PermisoDtoFilter;
import org.fichaje.dto.interfaces.IUsuarioDtoEstadistica;
import org.fichaje.provider.db.entity.Permiso;
import org.fichaje.provider.db.entity.enums.EstadosPeticion;
import org.fichaje.provider.mail.EmailService;
import org.fichaje.provider.db.entity.RrhhDto;
import org.fichaje.service.SecurityService;
import org.fichaje.service.PermisoService;
import org.fichaje.service.NotificationService;
import org.fichaje.provider.db.specifications.PermisoSpecifications;

import io.swagger.v3.oas.annotations.Operation;

import org.fichaje.util.SecurityUtils;

@RestController
@RequestMapping("/permiso")
//@CrossOrigin(origins = "http://localhost:4200")
public class PermisosController
		extends CommonController<Permiso, PermisoService> {

	@Autowired
	PermisoDtoConverter dtoConverter;
	@Autowired
	EmailService emailService;
	@Autowired
	NotificationService notificationService;
	@Autowired
	PermisoSpecifications specifications;

	@PostMapping("/create")
	public ResponseEntity<?> newPermiso(@RequestBody PermisoDto dto) {

		notificationService.sendNotification(dto.getNumeroUsuario(),
				"Petición de permiso",
				emailService.generateBodyForPermiso(dto.getNombreUsuario(),
						dto.getNumeroUsuario(),
						dto.getDescripcion(),
						dto.getDia().toString(),
						dto.getHoraInicio().toString(),
						dto.getHoraFin().toString(),
						EstadosPeticion.PENDIENTE.toString()));

		Permiso permiso = dtoConverter.transform(dto);

		if (permiso != null) {
			return ResponseEntity
					.status(HttpStatus.CREATED)
					.body(service.save(permiso));

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
			Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
			boolean isSuperAdmin = SecurityUtils.isSuperAdmin();

			if (!isSuperAdmin && currentEmpresaId != null && d.getEmpresa() != null &&
					!d.getEmpresa().getId().equals(currentEmpresaId)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}

			d.setAprobado(true);
			d.setEstado(EstadosPeticion.APROBADO.toString());

			notificationService.sendNotification(d.getUsuario().getNumero(),
					"Petición de permiso",
					emailService.generateBodyForPermiso(
							d.getUsuario().getNombreEmpleado(),
							d.getUsuario().getNumero(),
							d.getDescripcion(),
							d.getDia().toString(),
							d.getHoraInicio().toString(),
							d.getHoraFin().toString(),
							EstadosPeticion.APROBADO.toString()));

			return ResponseEntity.ok(service.save(d));
		}).orElseGet(() -> {
			return ResponseEntity.notFound().build();
		});
	}

	@PutMapping("/denegar/{id}")
	public ResponseEntity<?> denegar(@PathVariable Long id) {
		return service.findById(id).map(d -> {
			Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
			boolean isSuperAdmin = SecurityUtils.isSuperAdmin();

			if (!isSuperAdmin && currentEmpresaId != null && d.getEmpresa() != null &&
					!d.getEmpresa().getId().equals(currentEmpresaId)) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}

			d.setAprobado(false);
			d.setEstado(EstadosPeticion.DENEGADO.toString());

			notificationService.sendNotification(d.getUsuario().getNumero(),
					"Petición de permiso",
					emailService.generateBodyForPermiso(
							d.getUsuario().getNombreEmpleado(),
							d.getUsuario().getNumero(),
							d.getDescripcion(),
							d.getDia().toString(),
							d.getHoraInicio().toString(),
							d.getHoraFin().toString(),
							EstadosPeticion.DENEGADO.toString()));

			return ResponseEntity.ok(service.save(d));
		}).orElseGet(() -> {
			return ResponseEntity.notFound().build();
		});
	}

	@Operation(summary = "Obtiene una lista paginada y filtrada de objetos, el filtro se realiza a través de un DTO de ejemplo")
	@PostMapping("/pagesFiltered")
	public ResponseEntity<Page<Permiso>> pageDtoSpec(
			@RequestBody PermisoDtoFilter dto,
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

		Specification<Permiso> spec = (root, query, cb) -> cb.conjunction();
		if (dto.getUsuarioNombre() != null)
			spec = spec.and(specifications.nombreUsuarioContains(dto.getUsuarioNombre()));
		if (dto.getUsuarioNumero() != null)
			spec = spec.and(specifications.numeroUsuarioContains(dto.getUsuarioNumero()));
		if (dto.getUsuarioDni() != null)
			spec = spec.and(specifications.dniUsuarioContains(dto.getUsuarioDni()));
		if (dto.getUsuarioEmail() != null)
			spec = spec.and(specifications.emailUsuarioContains(dto.getUsuarioEmail()));
		if (dto.getDiaDesde() != null)
			spec = spec.and(specifications.diaDesde(dto.getDiaDesde()));
		if (dto.getDiaHasta() != null)
			spec = spec.and(specifications.diaHasta(dto.getDiaHasta()));
		if (dto.getHoraInicioDesde() != null)
			spec = spec.and(specifications.horaInicioDesde(dto.getHoraInicioDesde()));
		if (dto.getHoraInicioHasta() != null)
			spec = spec.and(specifications.horaInicioHasta(dto.getHoraInicioHasta()));
		if (dto.getHoraFinDesde() != null)
			spec = spec.and(specifications.horaFinDesde(dto.getHoraFinDesde()));
		if (dto.getHoraFinHasta() != null)
			spec = spec.and(specifications.horaFinHasta(dto.getHoraFinHasta()));
		if (dto.getDescripcion() != null)
			spec = spec.and(specifications.descripcionContains(dto.getDescripcion()));
		if (dto.getEstado() != null)
			spec = spec.and(specifications.estadoContains(dto.getEstado()));

		// Aislamiento Multi-empresa
		if (currentEmpresaId != null) {
			spec = spec.and(specifications.hasEmpresa(currentEmpresaId));
		} else if (!isSuperAdmin) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		Page<Permiso> entities = service.pagesAndSpec(
				spec,
				PageRequest.of(page, size, Sort.by(order)));

		if (!asc)
			entities = service.pagesAndSpec(
					spec,
					PageRequest.of(page, size, Sort.by(order).descending()));

//		Page<VacacionesDto> entitiesDto = entities
//				.map(usu -> dtoConverter.inverseTransform(usu));

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(entities);

	}

	@Operation(summary = "Obtiene una lista filtrada de objetos, el filtro se realiza a través de un DTO de ejemplo")
	@PostMapping("/listFiltered")
	public ResponseEntity<List<Permiso>> filteredList(
			@RequestBody PermisoDtoFilter dto) {

		Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
		String currentUserNumber = SecurityUtils.getCurrentUserNumber();
		boolean isRrhh = SecurityUtils.isRRHH();
		boolean isSuperAdmin = SecurityUtils.isSuperAdmin();

		// Si NO es RRHH ni SuperAdmin, filtrar por número de usuario
		if (!isRrhh && !isSuperAdmin) {
			dto.setUsuarioNumero(currentUserNumber);
		}

		Specification<Permiso> spec = Specification
				.where(dto.getUsuarioNombre() == null ? (Specification<Permiso>) null
						: specifications.nombreUsuarioContains(
								dto.getUsuarioNombre()))
				.and(dto.getUsuarioNumero() == null ? null
						: specifications.numeroUsuarioContains(
								dto.getUsuarioNumero()))
				.and(dto.getUsuarioDni() == null ? null
						: specifications.dniUsuarioContains(
								dto.getUsuarioDni()))
				.and(dto.getUsuarioEmail() == null ? null
						: specifications.emailUsuarioContains(
								dto.getUsuarioEmail()))
				.and(dto.getDiaDesde() == null ? null
						: specifications.diaDesde(
								dto.getDiaDesde()))
				.and(dto.getDiaHasta() == null ? null
						: specifications.diaHasta(
								dto.getDiaHasta()))
				.and(dto.getHoraInicioDesde() == null ? null
						: specifications.horaInicioDesde(
								dto.getHoraInicioDesde()))
				.and(dto.getHoraInicioHasta() == null ? null
						: specifications.horaInicioHasta(
								dto.getHoraInicioHasta()))
				.and(dto.getHoraFinDesde() == null ? null
						: specifications.horaFinDesde(
								dto.getHoraFinDesde()))
				.and(dto.getHoraFinHasta() == null ? null
						: specifications.horaFinHasta(
								dto.getHoraFinHasta()))
				.and(dto.getDescripcion() == null ? null
						: specifications.descripcionContains(
								dto.getDescripcion()))
				.and(dto.getEstado() == null ? null
						: specifications.estadoContains(
								dto.getEstado()));

		// Aislamiento Multi-empresa
		if (currentEmpresaId != null) {
			spec = spec.and(specifications.hasEmpresa(currentEmpresaId));
		} else if (!isSuperAdmin) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}

		List<Permiso> entities = service.filterAndList(spec);

		return ResponseEntity
				.status(HttpStatus.OK)
				.body(entities);

	}

	@Operation(summary = "Obtiene el número de permisos de los últimos 12 meses")
	@GetMapping("/count")
	public ResponseEntity<?> countLast12Months() {
		ChartDataDto result = service.numberOfPermisosLast12Months();
		return ResponseEntity.ok(result);
	}

	@Operation(summary = "Obtiene el número de permisos por usuario de los últimos 12 meses")
	@GetMapping("/count/users")
	public ResponseEntity<?> countUsersLast12Months() {
		List<IUsuarioDtoEstadistica> result = service.numberOfPermisosPerUserLast12Months();
		return ResponseEntity.ok(result);
	}

}
