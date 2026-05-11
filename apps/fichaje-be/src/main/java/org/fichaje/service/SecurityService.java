package org.fichaje.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fichaje.config.security.enums.RolNombre;
import org.fichaje.config.security.jwt.JwtProvider;
import org.fichaje.dto.JwtResponse;
import org.fichaje.dto.entity.EmpresaDTOWithoutSedes;
import org.fichaje.provider.db.entity.RrhhDto;
import org.fichaje.provider.db.entity.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UsuarioService usuarioService;
    private final JwtProvider jwtProvider;

    public RrhhDto rrhhInfo(String token) {
        String cleanToken = token.replace("Bearer ", "");

        boolean isRrhh = false;
        String numeroUsuario = "";

        if (jwtProvider.validateToken(cleanToken)) {
            numeroUsuario = jwtProvider.getSubjectFromToken(cleanToken);
            isRrhh = usuarioService.findByNumero(numeroUsuario)
                    .map(u -> u.getRoles().stream()
                            .anyMatch(rol -> rol.getRolNombre() == RolNombre.ROLE_RRHH))
                    .orElse(false);
        }

        return new RrhhDto(isRrhh, numeroUsuario);
    }

    public JwtResponse generateEmpresaToken(Authentication authentication, Long empresaId) {
        if (authentication == null || empresaId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        String numeroUsuario = authentication.getName();
        if (!usuarioService.belongsToEmpresa(numeroUsuario, empresaId)) {
            log.warn("Usuario {} intentó acceder a empresa {} sin pertenecer a ella", numeroUsuario, empresaId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "El usuario no pertenece a la empresa seleccionada");
        }

        String token = jwtProvider.generateToken(authentication, empresaId);
        return new JwtResponse(token);
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
