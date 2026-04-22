package com.commerce.ingestion_service.config;

import com.commerce.ingestion_service.tenant.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.UUID;

@Component
public class TenantInterceptor implements HandlerInterceptor {

    private static Map<String, UUID> USER_TENANT_MAP = Map.of(
        "admin@acme.com", UUID.fromString("11111111-1111-1111-1111-111111111111"),
        "admin@globex.com", UUID.fromString("22222222-2222-2222-2222-222222222222")
    );

    public static void setTestMap(Map<String, UUID> testMap) {
        USER_TENANT_MAP = testMap;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName();
            UUID orgId = USER_TENANT_MAP.get(email);
            
            if (orgId != null) {
                TenantContext.setTenantId(orgId);
            } else {
                throw new AccessDeniedException("User not associated with a valid Organization");
            }
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContext.clear();
    }
}
