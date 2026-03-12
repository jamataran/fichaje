package org.fichaje;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.fichaje.dto.entity.UsuarioDTO;
import org.fichaje.provider.db.entity.Rol;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.config.security.enums.RolNombre;
import org.fichaje.service.RolService;
import org.fichaje.service.UsuarioService;

@SpringBootApplication
@EnableScheduling
public class Application {

	@Autowired
	RolService rolService;
	@Autowired
	UsuarioService usuarioService;

	@PostConstruct
	public void init() {
//		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Madrid")); // It will
//																	// set UTC
//																	// timezone
//		System.out.println("Spring boot application running in UTC timezone :"
//				+ new Date()); // It will print UTC timezone

		/*
		 * Crear los roles si es la primera vez que se ejecuta la app y la tabla no
		 * tiene datos
		 */
		if (rolService.findByRolNombre(RolNombre.ROLE_SUPER_ADMIN).isEmpty()) {
			Rol rolSuperAdmin = new Rol();
			rolSuperAdmin.setRolNombre(RolNombre.ROLE_SUPER_ADMIN);
			rolService.save(rolSuperAdmin);
			System.out.println("Rol SUPER_ADMIN creado");
		} else {
			System.out.println("Rol SUPER_ADMIN ya existe");
		}
		if (rolService.findByRolNombre(RolNombre.ROLE_USER).isEmpty()) {
			Rol rolUser = new Rol();
			rolUser.setRolNombre(RolNombre.ROLE_USER);
			rolService.save(rolUser);
			System.out.println("Rol USER creado");
		} else {
			System.out.println("Rol USER ya existe");
		}

		if (rolService.findByRolNombre(RolNombre.ROLE_RRHH).isEmpty()) {
			Rol rolRrhh = new Rol();
			rolRrhh.setRolNombre(RolNombre.ROLE_RRHH);
			rolService.save(rolRrhh);
			System.out.println("Rol RRHH creado");
		} else {
			System.out.println("Rol RRHH ya existe");
		}
		
//		Creamos el usuario admin si no existe para poder tener un usuario con privilegios
		String adminCredential = "fichajesPi000";

		Usuario admin = usuarioService.findByNumero(adminCredential).orElse(null);
		if (admin == null) {
			System.out.println("No existe usuario admin");
			
			List<String> rolesAdmin = new ArrayList<>();
//			rolesAdmin.add("admin");
			rolesAdmin.add("super_admin");

			UsuarioDTO adminDto = new UsuarioDTO().builder()
					.nombreEmpleado("AdminFichaje")
					.numero(adminCredential)
					.password(adminCredential)
					.email("fichajespi@fichajespi.com")
					.roles(rolesAdmin)
					.dni("zzz")
					.build();

			usuarioService.createNewAdminUser(adminDto);
			System.out.println(
					"Usuario admin creado con número: " + adminCredential + " y pass: "
							+ adminCredential);
		} else {
			System.out.println("Existe usuario admin");
		}

	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
