package org.fichaje.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import org.fichaje.config.security.enums.RolNombre;
import org.fichaje.provider.db.entity.Rol;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.mail.EmailService;

@Service
public class NotificationService {

	@Autowired
	private UsuarioService usuarioService;

	@Autowired
	private RolService rolService;

	@Autowired
	private EmailService emailService;

	/**
	 * Obtiene los destinatarios para una notificación (usuarios RRHH + el usuario causante)
	 *
	 * @param usuario Usuario causante de la notificación
	 * @return Array de emails de destinatarios
	 */
	public String[] getDestinatarios(Usuario usuario) {
		// Obtenemos el rol de rrhh
		Rol rol = rolService.findByRolNombre(RolNombre.ROLE_RRHH)
			.orElseThrow(() -> new RuntimeException("Rol ROLE_RRHH no encontrado"));

		// Lo añadimos a un hashset para realizar una búsqueda
		Set<Rol> roles = new HashSet<>();
		roles.add(rol);

		// Obtenemos los usuarios con rol de rrhh
		List<Usuario> usuariosRrhh = usuarioService.findByRoles(roles);

		// Obtenemos sus emails
		List<String> destinatariosList = usuariosRrhh.stream()
			.map(u -> u.getEmail())
			.collect(Collectors.toList());

		// Añadimos el email del usuario causante de la incidencia
		destinatariosList.add(usuario.getEmail());

		Object[] objArr = destinatariosList.toArray();
		return Arrays.copyOf(objArr, objArr.length, String[].class);
	}

	/**
	 * Envía una notificación a los usuarios RRHH y al usuario causante
	 *
	 * @param numeroUsuario Número del usuario causante
	 * @param subject Asunto del email
	 * @param body Cuerpo del email
	 */
	@Async
	public void sendNotification(String numeroUsuario, String subject, String body) {
		Usuario usuario = usuarioService.findByNumero(numeroUsuario)
			.orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + numeroUsuario));

		String[] destinatarios = getDestinatarios(usuario);
		emailService.sendEmail(destinatarios, subject, body);
		System.out.println("Notificación enviada a " + destinatarios.length + " destinatarios");
	}

}

