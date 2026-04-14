package org.fichaje.controller;

import jakarta.validation.Valid;

import org.fichaje.service.SecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.fichaje.dto.entity.Mensaje;
import org.fichaje.dto.entity.UsuarioDTO;
import org.fichaje.dto.JwtDto;
import org.fichaje.dto.LoginUsuario;
import org.fichaje.config.security.jwt.JwtProvider;
import org.fichaje.service.UsuarioService;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.entity.Sede;
import org.fichaje.config.security.enums.RolNombre;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final UsuarioService usuarioService;
	private final JwtProvider jwtProvider;
	private final SecurityService securityService;

	public AuthController(AuthenticationManager authenticationManager,
						UsuarioService usuarioService,
						JwtProvider jwtProvider,
						SecurityService securityService) {
		this.authenticationManager = authenticationManager;
		this.usuarioService = usuarioService;
		this.jwtProvider = jwtProvider;
		this.securityService = securityService;
	}

	@PostMapping("/nuevo")
	public ResponseEntity<?> nuevo(
			@Valid @RequestBody UsuarioDTO nuevoUsuario,
			BindingResult bindingResult) {

        final ResponseEntity<Mensaje> BAD_REQUEST = validarUsuario(nuevoUsuario, bindingResult);
        if (BAD_REQUEST != null) return BAD_REQUEST;

		usuarioService.createNewUser(nuevoUsuario);

		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(new Mensaje("Usuario creado"));
	}


    @PostMapping("/login")
	public ResponseEntity<JwtDto> login(
			@Valid @RequestBody LoginUsuario loginUsuario,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return new ResponseEntity(new Mensaje("campos mal puestos"),
					HttpStatus.BAD_REQUEST);
		}

		Authentication authentication;
		try {
			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(
							loginUsuario.getNumero(),
							loginUsuario.getPassword()));
		} catch (AuthenticationException e) {
			return new ResponseEntity(new Mensaje("Número de empleado o contraseña incorrectos"), HttpStatus.UNAUTHORIZED);
		}
		SecurityContextHolder.getContext().setAuthentication(authentication);

		Usuario usuario = usuarioService.findByNumero(loginUsuario.getNumero()).orElse(null);
		if (usuario == null) {
			return new ResponseEntity(new Mensaje("Usuario no encontrado"), HttpStatus.NOT_FOUND);
		}

		boolean isAdmin = usuario.getRoles().stream()
				.anyMatch(r -> r.getRolNombre() == RolNombre.ROLE_SUPER_ADMIN);

		Long empresaId = loginUsuario.getEmpresaId();
		Long sedeId = loginUsuario.getSedeId();

		if (!isAdmin) {
			if (empresaId == null) {
				return new ResponseEntity(new Mensaje("El id de la empresa es obligatorio"), HttpStatus.BAD_REQUEST);
			}

			boolean perteneceAEmpresa = usuario.getEmpresas() != null && usuario.getEmpresas().stream()
					.anyMatch(e -> e.getId().equals(empresaId));

			if (!perteneceAEmpresa) {
				return new ResponseEntity(new Mensaje("El usuario no pertenece a la empresa seleccionada"), HttpStatus.FORBIDDEN);
			}

			if (sedeId != null) {
				final Long finalSedeId = sedeId;
				boolean perteneceASede = usuario.getSedes() != null && usuario.getSedes().stream()
						.anyMatch(s -> s.getId().equals(finalSedeId));
				if (!perteneceASede) {
					return new ResponseEntity(new Mensaje("El usuario no pertenece a la sede seleccionada"), HttpStatus.FORBIDDEN);
				}
			} else {
				sedeId = (usuario.getSedes() != null) ? usuario.getSedes().stream()
						.filter(s -> s.getEmpresa() != null && s.getEmpresa().getId().equals(empresaId))
						.findFirst()
						.map(Sede::getId)
						.orElse(null) : null;
			}
		}

		String jwt = jwtProvider.generateToken(authentication, empresaId, sedeId);
		JwtDto jwtDto = new JwtDto(jwt);
		return ResponseEntity.status(HttpStatus.OK).body(jwtDto);
	}

    private ResponseEntity<Mensaje> validarUsuario(UsuarioDTO nuevoUsuario, BindingResult bindingResult) {
        // Validar que los campos requeridos no están en blanco
        if (nuevoUsuario.getDni().isBlank() ||
                nuevoUsuario.getEmail().isBlank() ||
                nuevoUsuario.getNombreEmpleado().isBlank() ||
                nuevoUsuario.getNumero().isBlank()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new Mensaje("Los campos nombre, numero, email o dni no pueden estar en blanco"));
        }

        // Validar formato de email y otros campos
        if (bindingResult.hasErrors()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new Mensaje("Campos mal puestos o email inválido."));
        }

        // Validar que no existan duplicados
        if (usuarioService.existsByNumero(nuevoUsuario.getNumero())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new Mensaje("Ya existe el número de empleado."));
        }

        if (usuarioService.existsByDni(nuevoUsuario.getDni())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new Mensaje("Ya existe el dni del empleado."));
        }

        if (usuarioService.existsByEmail(nuevoUsuario.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new Mensaje("Email en uso."));
        }
        return null;
    }

}
