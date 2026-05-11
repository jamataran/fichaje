package org.fichaje.service;

import java.util.Optional;
import java.util.List;

import org.fichaje.dto.JwtDto;
import org.fichaje.dto.entity.EmpresaDTOWithoutSedes;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import org.fichaje.provider.db.entity.Rol;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.provider.db.entity.RrhhDto;
import org.fichaje.config.security.enums.RolNombre;
import org.fichaje.config.security.jwt.JwtProvider;
import org.springframework.web.server.ResponseStatusException;

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

    public JwtDto generateEmpresaToken(Authentication authentication, Long empresaId) {
        if (authentication == null || empresaId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        String numeroUsuario = authentication.getName();
        if (!usuarioService.belongsToEmpresa(numeroUsuario, empresaId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "El usuario no pertenece a la empresa seleccionada");
        }

        return new JwtDto(jwtProvider.generateToken(authentication, empresaId));
    }

    public List<EmpresaDTOWithoutSedes> getEmpresasForAuthenticatedUser(Authentication authentication) {
        if (authentication == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        Usuario usuario = usuarioService.findByNumero(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Usuario no encontrado"));

        return usuarioService.findEmpresasByUsuarioIdWithoutSedes(usuario.getId());
    }
}
