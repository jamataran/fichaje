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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.fichaje.dto.entity.Mensaje;
import org.fichaje.dto.entity.EmpresaDTOWithoutSedes;
import org.fichaje.dto.entity.UsuarioDTO;
import org.fichaje.dto.AuthEmpresaRequest;
import org.fichaje.dto.JwtDto;
import org.fichaje.dto.LoginUsuario;
import org.fichaje.config.security.jwt.JwtProvider;
import org.fichaje.service.UsuarioService;

import java.util.List;

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
	public ResponseEntity<Mensaje> nuevo(
			@Valid @RequestBody UsuarioDTO nuevoUsuario,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(new Mensaje("Campos mal puestos o email inválido."));
		}

		usuarioService.validateAndRegister(nuevoUsuario);

		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(new Mensaje("Usuario creado"));
	}


    @PostMapping("/login")
	public ResponseEntity<?> login(
			@Valid @RequestBody LoginUsuario loginUsuario,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(new Mensaje("campos mal puestos"));
		}

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginUsuario.getNumero(),
						loginUsuario.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtProvider.generateToken(authentication);
		return ResponseEntity.ok(new JwtDto(jwt));
	}

	@PostMapping("/empresa")
	public ResponseEntity<?> authByEmpresa(
			@Valid @RequestBody AuthEmpresaRequest request,
			BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(new Mensaje("empresaId es obligatorio"));
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return ResponseEntity.ok(securityService.generateEmpresaToken(authentication, request.getEmpresaId()));
	}

	@GetMapping("/empresas")
	public ResponseEntity<List<EmpresaDTOWithoutSedes>> authEmpresas() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return ResponseEntity.ok(securityService.getEmpresasForAuthenticatedUser(authentication));
	}

}
