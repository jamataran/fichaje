package org.fichaje.config.security.apikey;

import org.fichaje.config.security.service.UserDetailsServiceImpl;
import org.fichaje.provider.db.entity.Usuario;
import org.fichaje.service.ApiKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Filtro para autenticación mediante API Key
 * Se ejecuta antes que el filtro JWT para permitir ambos métodos de autenticación
 */
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(ApiKeyAuthenticationFilter.class);
    private static final String API_KEY_HEADER = "X-API-KEY";

    @Autowired
    private ApiKeyService apiKeyService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String apiKey = extractApiKey(request);
            
            if (apiKey != null && !apiKey.isEmpty()) {
                // Log sin información sensible
                logger.debug("Intento de autenticación con API Key desde IP: {}", 
                        request.getRemoteAddr());
                
                Optional<Usuario> usuario = apiKeyService.validateApiKey(apiKey);
                
                if (usuario.isPresent()) {
                    String numero = usuario.get().getNumero();
                    UserDetails userDetails = userDetailsService.loadUserByUsername(numero);
                    
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, 
                                    null, 
                                    userDetails.getAuthorities()
                            );
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    // Log de auditoría seguro
                    logger.info("Autenticación exitosa vía API Key desde IP: {} para usuario ID: {}", 
                            request.getRemoteAddr(), usuario.get().getId());
                } else {
                    // Log genérico sin revelar información
                    logger.warn("Intento de autenticación fallido con API Key desde IP: {}", 
                            request.getRemoteAddr());
                }
            }
        } catch (Exception e) {
            // Log de error sin stack trace completo en producción
            logger.error("Error en el filtro de API Key: {}", e.getClass().getSimpleName());
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * Extrae la API Key del header X-API-KEY
     */
    private String extractApiKey(HttpServletRequest request) {
        String header = request.getHeader(API_KEY_HEADER);
        
        if (header != null && !header.isEmpty()) {
            return header.trim();
        }
        
        return null;
    }
}
