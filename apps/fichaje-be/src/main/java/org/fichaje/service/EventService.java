package org.fichaje.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.fichaje.provider.db.entity.Incidencia;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.mail.EmailService;

@Component
public class EventService {

	@Autowired
	private IncidenciaService incidenciaService;
	@Autowired
	private NotificationService notificationService;

	public void createEvent(String subject, String descripcion, LocalDate dia,
			Usuario usuario, String resumen) {

		// Creamos la incidencia
		Incidencia incidencia = new Incidencia();
		incidencia.setDescripcion(descripcion);
		incidencia.setDia(dia);
		incidencia.setUsuario(usuario);
		incidencia.setResumen(resumen);
		incidencia.setResuelta(false);
		incidencia.setExplicacion("");
		incidenciaService.save(incidencia);

		// Enviar notificación a RRHH y al usuario
		notificationService.sendNotification(usuario.getNumero(), subject, descripcion);
	}

}
