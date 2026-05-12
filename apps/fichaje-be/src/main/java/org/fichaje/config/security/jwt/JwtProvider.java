package org.fichaje.config.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.fichaje.provider.db.entity.UsuarioPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * Proveedor de tokens JWT.
 * Maneja la generación, validación y extracción de datos de los tokens.
 */
@Slf4j
@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private int expiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret.getBytes(StandardCharsets.UTF_8));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Authentication authentication) {
        return generateToken(authentication, null);
    }

    public String generateToken(Authentication authentication, Long empresaId) {
        UsuarioPrincipal usuarioPrincipal = (UsuarioPrincipal) authentication.getPrincipal();
        List<String> roles = usuarioPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        JwtBuilder builder = Jwts.builder()
                .subject(usuarioPrincipal.getUsername())
                .claim("roles", roles)
                .claim("nombre", sanitizeString(usuarioPrincipal.getNombre()))
                .claim("id", usuarioPrincipal.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration * 1000L))
                .signWith(getSigningKey());

        if (empresaId != null) {
            builder.claim("empresaId", empresaId);
        }

        return builder.compact();
    }

    public String getSubjectFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public Long getEmpresaIdFromToken(String token) {
        Object empresaId = parseClaims(token).get("empresaId");
        if (empresaId instanceof Number number) {
            return number.longValue();
        }
        return null;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Token JWT mal formado");
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT no soportado");
        } catch (ExpiredJwtException e) {
            log.error("Token JWT expirado");
        } catch (IllegalArgumentException e) {
            log.error("Token JWT vacío");
        } catch (SecurityException e) {
            log.error("Fallo en la firma del token JWT");
        }
        return false;
    }

    /**
     * Normaliza y limpia una cadena de texto (elimina acentos y caracteres especiales).
     */
    private String sanitizeString(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                         .replace("Ñ", "N")
                         .replace("ñ", "n");
    }
}
