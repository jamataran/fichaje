package org.fichaje.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.fichaje.provider.db.entity.Rol;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.entity.RrhhDto;
import org.fichaje.config.security.enums.RolNombre;
import org.fichaje.config.security.jwt.JwtProvider;

@Service
public class SecurityService {

    private final UsuarioService usuarioService;
    private final JwtProvider jwtProvider;

    public SecurityService(UsuarioService usuarioService,
                           JwtProvider jwtProvider) {
        this.usuarioService = usuarioService;
        this.jwtProvider = jwtProvider;
    }

    public RrhhDto rrhhInfo(String token) {

        token = token.replace("Bearer ", "");

        boolean response = true;  // Por defecto SÍ es RRHH (lógica original)
        String numeroUsuario = "";

        if (jwtProvider.validateToken(token)) {
            numeroUsuario = jwtProvider.getSubjectFromToken(token);
            Usuario usuario = usuarioService.findByNumero(numeroUsuario).orElse(null);
            if (usuario != null) {
                for (Rol rol : usuario.getRoles()) {
                    if (rol.getRolNombre() == RolNombre.ROLE_RRHH)
                        response = false;  // Si tiene rol RRHH, marcar como false (lógica original del proyecto)
                }
            }
        }

        return new RrhhDto(response, numeroUsuario);
    }
}
