package org.fichaje.config.security.apikey;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fichaje.config.security.service.UserDetailsServiceImpl;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.service.ApiKeyService;
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
 * Filtro para autenticación mediante API Key.
 * Se ejecuta antes que el filtro JWT para permitir ambos métodos de autenticación.
 */
@Slf4j
@RequiredArgsConstructor
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-KEY";

    private final ApiKeyService apiKeyService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        String apiKey = extractApiKey(request);
        
        if (apiKey != null && !apiKey.isEmpty()) {
            log.debug("Intento de autenticación con API Key desde IP: {}", request.getRemoteAddr());
            
            apiKeyService.validateApiKey(apiKey).ifPresent(usuario -> {
                UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getNumero());
                
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, 
                                userDetails.getAuthorities()
                        );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Autenticación exitosa vía API Key para usuario: {} (ID: {})", 
                        usuario.getNombreEmpleado(), usuario.getId());
            });

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                log.warn("Intento de autenticación fallido con API Key desde IP: {}", request.getRemoteAddr());
            }
        }
        
        filterChain.doFilter(request, response);
    }

    private String extractApiKey(HttpServletRequest request) {
        String header = request.getHeader(API_KEY_HEADER);
        return (header != null && !header.isEmpty()) ? header.trim() : null;
    }
}
