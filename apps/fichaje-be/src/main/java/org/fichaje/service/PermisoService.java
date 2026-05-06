package org.fichaje.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import org.fichaje.converter.PermisoDtoConverter;
import org.fichaje.dto.entity.ChartDataDto;
import org.fichaje.dto.entity.PermisoDto;
import org.fichaje.dto.entity.PermisoDtoFilter;
import org.fichaje.dto.interfaces.IUsuarioDtoEstadistica;
import org.fichaje.exception.BusinessException;
import org.fichaje.provider.db.entity.Permiso;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.entity.enums.EstadosPeticion;
import org.fichaje.provider.db.repository.PermisoRepository;
import org.fichaje.provider.db.specifications.PermisoSpecifications;
import org.fichaje.provider.mail.EmailService;
import org.fichaje.util.SecurityUtils;

@Service
@Transactional
public class PermisoService extends CommonServiceImpl<Permiso, PermisoRepository> {

	private final PermisoDtoConverter dtoConverter;
	private final EmailService emailService;
	private final NotificationService notificationService;
	private final PermisoSpecifications specifications;

	public PermisoService(PermisoDtoConverter dtoConverter,
						  EmailService emailService,
						  NotificationService notificationService,
						  PermisoSpecifications specifications) {
		this.dtoConverter = dtoConverter;
		this.emailService = emailService;
		this.notificationService = notificationService;
		this.specifications = specifications;
	}

	public Permiso createPermiso(PermisoDto dto) {
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
		if (permiso == null) {
			throw new BusinessException("La fecha de fin debe ser posterior a la de inicio.");
		}

		// Aislamiento Multi-empresa: Asignar la empresa del usuario al permiso
		if (permiso.getUsuario() != null && !permiso.getUsuario().getEmpresas().isEmpty()) {
			Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
			if (currentEmpresaId != null) {
				permiso.setEmpresa(permiso.getUsuario().getEmpresas().stream()
						.filter(e -> e.getId().equals(currentEmpresaId))
						.findFirst()
						.orElse(permiso.getUsuario().getEmpresas().iterator().next()));
			} else {
				permiso.setEmpresa(permiso.getUsuario().getEmpresas().iterator().next());
			}
		}

		return save(permiso);
	}

	public Permiso aprobar(Long id) {
		return repository.findById(id).map(d -> {
			validateEmpresaAccess(d);

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

			return save(d);
		}).orElseThrow(() -> new BusinessException("Permiso no encontrado con ID: " + id));
	}

	public Permiso denegar(Long id) {
		return repository.findById(id).map(d -> {
			validateEmpresaAccess(d);

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

			return save(d);
		}).orElseThrow(() -> new BusinessException("Permiso no encontrado con ID: " + id));
	}

	public Page<Permiso> getPageFiltered(PermisoDtoFilter dto, int page, int size, String order, boolean asc) {
		Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
		String currentUserNumber = SecurityUtils.getCurrentUserNumber();
		boolean isRrhh = SecurityUtils.isRRHH();
		boolean isSuperAdmin = SecurityUtils.isSuperAdmin();

		if (!isRrhh && !isSuperAdmin) {
			dto.setUsuarioNumero(currentUserNumber);
		}

		if (currentEmpresaId == null && !isSuperAdmin) {
			throw new BusinessException("No tienes permisos para acceder a esta información.");
		}

		Specification<Permiso> spec = specifications.buildSpecification(dto, currentEmpresaId, isSuperAdmin);
		Sort sort = asc ? Sort.by(order).ascending() : Sort.by(order).descending();
		
		return repository.findAll(spec, PageRequest.of(page, size, sort));
	}

	public List<Permiso> getListFiltered(PermisoDtoFilter dto) {
		Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
		String currentUserNumber = SecurityUtils.getCurrentUserNumber();
		boolean isRrhh = SecurityUtils.isRRHH();
		boolean isSuperAdmin = SecurityUtils.isSuperAdmin();

		if (!isRrhh && !isSuperAdmin) {
			dto.setUsuarioNumero(currentUserNumber);
		}

		if (currentEmpresaId == null && !isSuperAdmin) {
			throw new BusinessException("No tienes permisos para acceder a esta información.");
		}

		Specification<Permiso> spec = specifications.buildSpecification(dto, currentEmpresaId, isSuperAdmin);
		return repository.findAll(spec);
	}

	private void validateEmpresaAccess(Permiso d) {
		Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
		boolean isSuperAdmin = SecurityUtils.isSuperAdmin();

		if (!isSuperAdmin && currentEmpresaId != null && d.getEmpresa() != null &&
				!d.getEmpresa().getId().equals(currentEmpresaId)) {
			throw new BusinessException("No tienes permisos para modificar este registro.");
		}
	}

	@Override
	public Permiso save(Permiso entity) {
		// Validar solapamiento de permisos para el mismo usuario y día
		if (entity.getUsuario() != null && entity.getDia() != null) {
			List<Permiso> existentes = repository.findByUsuarioAndDia(entity.getUsuario(), entity.getDia());
			boolean solapa = existentes.stream().anyMatch(p -> 
				(entity.getHoraInicio().isBefore(p.getHoraFin()) && entity.getHoraFin().isAfter(p.getHoraInicio()))
			);
			
			if (solapa) {
				throw new BusinessException("Ya existe un permiso que se solapa con el horario solicitado para este día.");
			}
		}
		return super.save(entity);
	}

	public List<Permiso> findByUsuarioAprobado(Usuario usuario) {
		return repository.findByUsuarioAndAprobadoTrue(usuario);
	}

	public ChartDataDto numberOfPermisosLast12Months() {

		ChartDataDto result = new ChartDataDto();

		List<LocalDate> datesList = new ArrayList<>();
		List<Integer> countList = new ArrayList<>();

		LocalDate dateNow = LocalDate.now();

		for (int i = 12; i >= 1; i--) {

			LocalDate dateRequest = dateNow.minusMonths(i);

			Integer count = repository.countNumberOfPermisosOfMonth(
					dateRequest.getMonthValue(),
					dateRequest.getYear());

			datesList.add(dateRequest);
			countList.add(count);

		}
		result.setCantidades(countList);
		result.setFechas(datesList);

		return result;
	}

	public List<IUsuarioDtoEstadistica> numberOfPermisosPerUserLast12Months() {

		return repository.numberOfPermisosPerUserLast12Months(LocalDate.now());

	}

}
