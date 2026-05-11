package org.fichaje.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import org.fichaje.converter.VacacionesDtoConverter;
import org.fichaje.dto.entity.VacacionesDto;
import org.fichaje.exception.BusinessException;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.entity.Vacaciones;
import org.fichaje.provider.db.entity.enums.EstadosPeticion;
import org.fichaje.provider.db.repository.VacacionesRepository;
import org.fichaje.provider.mail.EmailService;
import org.fichaje.util.SecurityUtils;

@Service
@Transactional
public class VacacionesService
		extends CommonServiceImpl<Vacaciones, VacacionesRepository>{

	private final VacacionesDtoConverter dtoConverter;
	private final EmailService emailService;
	private final NotificationService notificationService;

	public VacacionesService(VacacionesDtoConverter dtoConverter,
							 EmailService emailService,
							 NotificationService notificationService) {
		this.dtoConverter = dtoConverter;
		this.emailService = emailService;
		this.notificationService = notificationService;
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

	public List<Vacaciones> findByUsuarioSinAgotar(Usuario usuario) {
		return repository.findByUsuarioAndConsumidasFalseAndAprobadoTrue(usuario);
	}

	public List<Vacaciones> findByUser(Long id) {
		return repository.findByUsuarioId(id);
	}
}
