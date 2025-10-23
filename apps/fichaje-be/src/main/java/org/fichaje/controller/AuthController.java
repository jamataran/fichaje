package org.fichaje.controller;

import javax.validation.Valid;

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

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginUsuario.getNumero(),
						loginUsuario.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtProvider.generateToken(authentication);
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
