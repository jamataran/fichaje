package org.fichaje.provider.mail;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;

	@Async
	public void sendEmail(String[] to, String subject, String body) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(to);
		msg.setSubject(subject);
		msg.setText(body);
		msg.setFrom("noreply@fichajespi.es");
		javaMailSender.send(msg);
	}

	@Async
	public void sendEmail(String to, String subject, String body) {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setTo(to);
		msg.setSubject(subject);
		msg.setText(body);
		msg.setFrom("noreply@fichajespi.es");
		javaMailSender.send(msg);
	}

	public String generateBodyForVacaciones(String nombre, String numero,
			String diaInicio, String diaFin, String estado) {
		StringBuilder sb = new StringBuilder();
		sb.append("El usuario " + nombre);
		sb.append(" con número " + numero);
		sb.append(
				" ha solicitado vacaciones en el siguiente periodo:\n\n");
		sb.append("Día inicio: " + diaInicio + "\n");
		sb.append("Día fin: " + diaFin + "\n\n");
		sb.append("El estado de esta petición es: " + estado);
		return sb.toString();
	}

	public String generateBodyForPermiso(String nombre, String numero,
			String desc, String dia, String inicio, String fin, String estado) {
		StringBuilder sb = new StringBuilder();
		sb.append("El usuario " + nombre);
		sb.append(" con número " + numero);
		sb.append(
				" ha solicitado un permiso para ausentarse con la siguiente descripción:\n\n");
		sb.append(desc + "\n\n");
		sb.append("Día: " + dia + "\n");
		sb.append("Hora inicio: " + inicio + "\n");
		sb.append("Hora fin: " + fin + "\n\n");
		sb.append("El estado de esta petición es: " + estado);
		return sb.toString();
	}
}
