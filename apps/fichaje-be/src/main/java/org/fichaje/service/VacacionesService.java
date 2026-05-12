package org.fichaje.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.fichaje.converter.VacacionesDtoConverter;
import org.fichaje.dto.entity.VacacionesDto;
import org.fichaje.dto.entity.VacacionesDtoFilter;
import org.fichaje.exception.BusinessException;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.entity.Vacaciones;
import org.fichaje.provider.db.entity.enums.EstadosPeticion;
import org.fichaje.provider.db.repository.VacacionesRepository;
import org.fichaje.provider.db.specifications.VacacionesSpecifications;
import org.fichaje.provider.mail.EmailService;
import org.fichaje.util.SecurityUtils;

@Service
@Transactional
public class VacacionesService
		extends CommonServiceImpl<Vacaciones, VacacionesRepository>{

	private final VacacionesDtoConverter dtoConverter;
	private final EmailService emailService;
	private final NotificationService notificationService;
	private final VacacionesSpecifications specifications;

	public VacacionesService(VacacionesRepository repository,
							 VacacionesDtoConverter dtoConverter,
							 EmailService emailService,
							 NotificationService notificationService,
							 VacacionesSpecifications specifications) {
		super(repository);
		this.dtoConverter = dtoConverter;
		this.emailService = emailService;
		this.notificationService = notificationService;
		this.specifications = specifications;
	}

	public Vacaciones createVacaciones(VacacionesDto dto) {
		notificationService.sendNotification(dto.getNumeroUsuario(),
				"Petición de vacaciones",
				emailService.generateBodyForVacaciones(dto.getNombreUsuario(),
						dto.getNumeroUsuario(),
						dto.getInicio().toString(),
						dto.getFin().toString(),
						EstadosPeticion.PENDIENTE.toString()));

		Vacaciones vacaciones = dtoConverter.transform(dto);
		if (vacaciones == null) {
			throw new BusinessException("La fecha de fin debe ser posterior a la de inicio.");
		}

		// Validar solapamiento de vacaciones para el mismo usuario
		if (vacaciones.getUsuario() != null) {
			List<Vacaciones> existentes = repository.findByUsuarioAndInicioBeforeAndFinAfter(
					vacaciones.getUsuario(), vacaciones.getFin().plusDays(1), vacaciones.getInicio().minusDays(1));
			if (!existentes.isEmpty()) {
				throw new BusinessException("Ya tienes una petición de vacaciones que se solapa con las fechas solicitadas.");
			}
		}

		// Aislamiento Multi-empresa: Asignar la empresa del usuario a las vacaciones
		if (vacaciones.getUsuario() != null && !vacaciones.getUsuario().getEmpresas().isEmpty()) {
			Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
			if (currentEmpresaId != null) {
				vacaciones.setEmpresa(vacaciones.getUsuario().getEmpresas().stream()
						.filter(e -> e.getId().equals(currentEmpresaId))
						.findFirst()
						.orElse(vacaciones.getUsuario().getEmpresas().iterator().next()));
			} else {
				vacaciones.setEmpresa(vacaciones.getUsuario().getEmpresas().iterator().next());
			}
		}

		return save(vacaciones);
	}

	public Vacaciones aprobar(Long id) {
		return repository.findById(id).map(d -> {
			validateEmpresaAccess(d);

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

			return save(d);
		}).orElseThrow(() -> new BusinessException("Petición de vacaciones no encontrada con ID: " + id));
	}

	public Vacaciones denegar(Long id) {
		return repository.findById(id).map(d -> {
			validateEmpresaAccess(d);

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

			return save(d);
		}).orElseThrow(() -> new BusinessException("Petición de vacaciones no encontrada con ID: " + id));
	}

	public Page<Vacaciones> getPageFiltered(VacacionesDtoFilter dto, int page, int size, String order, boolean asc) {
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

		Specification<Vacaciones> spec = specifications.buildSpecification(dto, currentEmpresaId, isSuperAdmin);
		Sort sort = asc ? Sort.by(order).ascending() : Sort.by(order).descending();
		
		return repository.findAll(spec, PageRequest.of(page, size, sort));
	}

	public List<Vacaciones> getListFiltered(VacacionesDtoFilter dto) {
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

		Specification<Vacaciones> spec = specifications.buildSpecification(dto, currentEmpresaId, isSuperAdmin);
		return repository.findAll(spec);
	}

	private void validateEmpresaAccess(Vacaciones d) {
		Long currentEmpresaId = SecurityUtils.getCurrentEmpresaId();
		boolean isSuperAdmin = SecurityUtils.isSuperAdmin();

		if (!isSuperAdmin && currentEmpresaId != null && d.getEmpresa() != null &&
				!d.getEmpresa().getId().equals(currentEmpresaId)) {
			throw new BusinessException("No tienes permisos para modificar este registro.");
		}
	}

	public List<Vacaciones> findByUsuarioSinAgotar(Usuario usuario) {
		return repository.findByUsuarioAndConsumidasFalseAndAprobadoTrue(usuario);
	}

	public List<Vacaciones> findByUser(Long id) {
		return repository.findByUsuarioId(id);
	}

	public void managePeriodoVacaciones(Usuario u, java.time.LocalDate dia) {
		List<Vacaciones> vacaciones = findByUsuarioSinAgotar(u);
		vacaciones.forEach(v -> {
			int compareIni = dia.compareTo(v.getInicio());
			int compareFin = dia.compareTo(v.getFin());

			if (compareIni >= 0 && compareFin <= 0) {
				// el usuario está de vacaciones
				u.setEnVacaciones(true);
			} else if (compareFin > 0) {
				// las vacaciones se pasaron y las marcamos como agotadas
				v.setConsumidas(true);
				save(v);
				u.setEnVacaciones(false);
			}
		});
	}
}
