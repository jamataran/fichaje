package org.fichaje.util;

import org.fichaje.provider.db.entity.TenantEntity;
import org.fichaje.provider.db.entity.UsuarioPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.function.Supplier;

@Component
public class SecurityUtils {

    public static Long getCurrentEmpresaId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UsuarioPrincipal principal) {
            return principal.getEmpresaId();
        }
        return null;
    }

    public static String getCurrentUserNumber() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }

    public static boolean isRRHH() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_RRHH"));
        }
        return false;
    }

    public static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        }
        return false;
    }

    public static boolean isSuperAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));
        }
        return false;
    }

    public static void checkTenantAccess(Object entity) {
        if (entity instanceof TenantEntity tenantEntity) {
            Long currentEmpresaId = getCurrentEmpresaId();
            if (!isSuperAdmin() && currentEmpresaId != null && tenantEntity.getEmpresa() != null &&
                    !tenantEntity.getEmpresa().getId().equals(currentEmpresaId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes acceso a este recurso");
            }
        }
    }

    public static <T, ID> T checkTenantAccessById(ID id, JpaRepository<T, ID> repository, Supplier<? extends RuntimeException> notFoundException) {
        T entity = repository.findById(id).orElseThrow(notFoundException);
        checkTenantAccess(entity);
        return entity;
    }
}
