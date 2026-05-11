package org.fichaje.config.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fichaje.config.security.service.UserDetailsServiceImpl;
import org.fichaje.provider.db.entity.UsuarioPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtro que intercepta las peticiones HTTP para validar el token JWT.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) 
            throws ServletException, IOException {
        try {
            String token = extractToken(req);
            if (token != null && jwtProvider.validateToken(token)) {
                String username = jwtProvider.getSubjectFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Establecer el ID de empresa en el principal si existe en el token
                Long empresaId = jwtProvider.getEmpresaIdFromToken(token);
                if (userDetails instanceof UsuarioPrincipal principal) {
                    principal.setEmpresaId(empresaId);
                }

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            log.error("Error en el filtro JWT: {}", e.getMessage());
        }
        filterChain.doFilter(req, res);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
