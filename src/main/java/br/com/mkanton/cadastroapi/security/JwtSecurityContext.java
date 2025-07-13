package br.com.mkanton.cadastroapi.security;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class JwtSecurityContext implements SecurityContext {

    private final String username;
    private final String role;
    private final boolean isSecure;

    public JwtSecurityContext(String username, String role, boolean isSecure) {
        this.username = username;
        this.role = role;
        this.isSecure = isSecure;
    }

    @Override
    public Principal getUserPrincipal() {
        return () -> username;
    }

    @Override
    public boolean isUserInRole(String role) {
        return this.role != null && this.role.equals(role);
    }

    @Override
    public boolean isSecure() {
        return isSecure;
    }

    @Override
    public String getAuthenticationScheme() {
        return "Bearer";
    }

}
