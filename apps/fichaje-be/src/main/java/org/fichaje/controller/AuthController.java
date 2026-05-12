package org.fichaje.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.fichaje.dto.AuthEmpresaRequest;
import org.fichaje.dto.JwtResponse;
import org.fichaje.dto.LoginRequest;
import org.fichaje.dto.entity.EmpresaDTOWithoutSedes;
import org.fichaje.dto.entity.Mensaje;
import org.fichaje.dto.entity.UsuarioDTO;
import org.fichaje.service.AuthService;
import org.fichaje.service.SecurityService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para autenticación y gestión de sesiones.
 * Maneja el registro, inicio de sesión y cambio de contexto de empresa.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "${client.url}")
@Tag(name = "Autenticación", description = "Endpoints para registro, login y gestión de tokens")
public class AuthController {

    private final AuthService authService;
    private final SecurityService securityService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un nuevo usuario en el sistema. Requiere rol RRHH.")
    public Mensaje register(@Valid @RequestBody UsuarioDTO nuevoUsuario) {
        authService.register(nuevoUsuario);
        return new Mensaje("Usuario creado correctamente");
    }

    /**
     * @deprecated Usar /register en su lugar.
     */
    @Deprecated
    @PostMapping("/nuevo")
    @ResponseStatus(HttpStatus.CREATED)
    public Mensaje nuevo(@Valid @RequestBody UsuarioDTO nuevoUsuario) {
        return register(nuevoUsuario);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario devolviendo un token JWT.")
    public JwtResponse login(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/empresa")
    @Operation(summary = "Seleccionar empresa", description = "Genera un nuevo token JWT con el contexto de la empresa seleccionada.")
    public JwtResponse authByEmpresa(@Valid @RequestBody AuthEmpresaRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return securityService.generateEmpresaToken(authentication, request.empresaId());
    }

    @GetMapping("/empresas")
    @Operation(summary = "Listar mis empresas", description = "Obtiene la lista de empresas a las que el usuario autenticado pertenece.")
    public List<EmpresaDTOWithoutSedes> getMyEmpresas() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return securityService.getEmpresasForAuthenticatedUser(authentication);
    }
}
