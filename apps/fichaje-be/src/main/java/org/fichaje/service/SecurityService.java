package org.fichaje.service;

import java.util.Optional;
import java.util.List;

import org.fichaje.dto.JwtDto;
import org.fichaje.dto.entity.EmpresaDTOWithoutSedes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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

        boolean response = false;  // Por defecto NO es RRHH
        String numeroUsuario = "";

        if (jwtProvider.validateToken(token)) {
            numeroUsuario = jwtProvider.getSubjectFromToken(token);
            Usuario usuario = usuarioService.findByNumero(numeroUsuario).orElse(null);
            if (usuario != null) {
                // Verificar si el usuario tiene el rol RRHH
                // Si tiene RRHH, response = true
                response = usuario.getRoles().stream()
                    .anyMatch(rol -> rol.getRolNombre() == RolNombre.ROLE_RRHH);
            }
        }

        return new RrhhDto(response, numeroUsuario);
    }

    public Optional<JwtDto> generateEmpresaToken(Authentication authentication, Long empresaId) {
        if (authentication == null || empresaId == null) {
            return Optional.empty();
        }

        String numeroUsuario = authentication.getName();
        boolean belongs = usuarioService.belongsToEmpresa(numeroUsuario, empresaId);
        if (!belongs) {
            return Optional.empty();
        }

        String jwt = jwtProvider.generateToken(authentication, empresaId);
        return Optional.of(new JwtDto(jwt));
    }

    public Optional<List<EmpresaDTOWithoutSedes>> getEmpresasForAuthenticatedUser(Authentication authentication) {
        if (authentication == null) {
            return Optional.empty();
        }

        String numeroUsuario = authentication.getName();
        Usuario usuario = usuarioService.findByNumero(numeroUsuario).orElse(null);
        if (usuario == null) {
            return Optional.empty();
        }

        return Optional.of(usuarioService.findEmpresasByUsuarioIdWithoutSedes(usuario.getId()));
    }
}
