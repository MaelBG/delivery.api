package com.deliverytech.delivery_api.security;

import com.deliverytech.delivery_api.entity.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public static Usuario getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (Usuario) authentication.getPrincipal();
        }
        throw new RuntimeException("Usuário não autenticado");
    }

    public static Long getCurrentUserld() {
        return getCurrentUser().getId();
    }

    public static String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }

    public static String getCurrentUserRole() {
        return getCurrentUser().getRole().name();
    }

    public static Long getCurrentRestauranteld() {
        Usuario usuario = getCurrentUser();
        return usuario.getRestauranteld();
    }

    public static boolean hasRole(String role) {
        try {
            Usuario usuario = getCurrentUser();
            return usuario.getRole().name().equals(role);
        } catch (Exception e) {
            return false;
        }
    }
}