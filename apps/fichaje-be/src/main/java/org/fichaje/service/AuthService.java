package org.fichaje.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fichaje.config.security.jwt.JwtProvider;
import org.fichaje.dto.JwtResponse;
import org.fichaje.dto.LoginRequest;
import org.fichaje.dto.entity.UsuarioDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de la lógica de autenticación.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtProvider jwtProvider;

    /**
     * Autentica a un usuario y devuelve un token JWT.
     */
    public JwtResponse login(LoginRequest loginRequest) {
        log.info("Autenticando usuario: {}", loginRequest.numero());
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.numero(),
                        loginRequest.password()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);
        
        return new JwtResponse(jwt);
    }

    /**
     * Registra un nuevo usuario en el sistema.
     */
    public void register(UsuarioDTO nuevoUsuario) {
        log.info("Registrando nuevo usuario: {} ({})", nuevoUsuario.getNombreEmpleado(), nuevoUsuario.getNumero());
        usuarioService.validateAndRegister(nuevoUsuario);
    }
}
