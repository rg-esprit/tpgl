package com.tpgl.tpgl.aspect;

import com.tpgl.tpgl.domain.RoleEmploye;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;

/**
 * Aspect de sécurité basé sur les rôles (Séance 12 - Spring AOP).
 * Intercepte les méthodes annotées avec {@link RequiresRole} et vérifie l'en-tête HTTP
 * optionnel {@code X-AutoLoc-Role} lorsqu'il est présent dans le contexte de requête.
 */
@Aspect
@Component
@Slf4j
public class RoleSecurityAspect {

    public static final String ROLE_HEADER = "X-AutoLoc-Role";

    private static final ThreadLocal<String> CURRENT_ROLE_CONTEXT = new ThreadLocal<>();

    public static void setCurrentRole(String role) {
        CURRENT_ROLE_CONTEXT.set(role);
    }

    public static void setCurrentRole(RoleEmploye role) {
        CURRENT_ROLE_CONTEXT.set(role != null ? role.name() : null);
    }

    public static void clearCurrentRole() {
        CURRENT_ROLE_CONTEXT.remove();
    }

    @Before("@annotation(requiresRole)")
    public void checkRoleAccess(JoinPoint joinPoint, RequiresRole requiresRole) {
        String roleHeader = resolveRoleHeader();
        if (roleHeader == null || roleHeader.isBlank()) {
            // En l'absence d'en-tête dans les appels internes ou tests de service, autoriser par défaut
            return;
        }

        String normalizedRole = roleHeader.trim().toUpperCase();
        String methodName = joinPoint.getSignature().toShortString();

        if (requiresRole.adminOnly()) {
            if (!RoleEmploye.MANAGER.name().equals(normalizedRole) && !"ADMIN".equals(normalizedRole)) {
                log.warn("[AOP-SECURITY] Accès refusé à {} pour le rôle '{}' (MANAGER requis)", methodName, roleHeader);
                throw new SecurityException("Accès refusé : rôle MANAGER requis pour exécuter " + methodName);
            }
            return;
        }

        RoleEmploye[] allowedRoles = requiresRole.value();
        if (allowedRoles.length > 0) {
            boolean authorized = Arrays.stream(allowedRoles)
                    .anyMatch(r -> r.name().equalsIgnoreCase(normalizedRole));
            if (!authorized) {
                log.warn("[AOP-SECURITY] Accès refusé à {} pour le rôle '{}'. Rôles autorisés : {}",
                        methodName, roleHeader, Arrays.toString(allowedRoles));
                throw new SecurityException("Accès refusé pour le rôle '" + roleHeader
                        + "'. Rôles autorisés : " + Arrays.toString(allowedRoles));
            }
        }
    }

    private String resolveRoleHeader() {
        String threadRole = CURRENT_ROLE_CONTEXT.get();
        if (threadRole != null) {
            return threadRole;
        }
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (attributes instanceof ServletRequestAttributes servletAttributes) {
            HttpServletRequest request = servletAttributes.getRequest();
            if (request != null) {
                return request.getHeader(ROLE_HEADER);
            }
        }
        return null;
    }
}
