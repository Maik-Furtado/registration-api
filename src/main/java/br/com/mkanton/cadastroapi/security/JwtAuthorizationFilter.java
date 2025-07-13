package br.com.mkanton.cadastroapi.security;

import br.com.mkanton.cadastroapi.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.Provider;
import java.io.IOException;


/**
 * Authorization filter that validates JWT tokens and sets the security context.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthorizationFilter implements ContainerRequestFilter {

    Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    @Inject
    private AuthService authService;

    @Context
    private UriInfo uriInfo;

    @Override
    public void filter(ContainerRequestContext requestContext)throws IOException {

        String path = uriInfo.getPath();

        logger.info("Intercepted path: {}", path);

        logger.info("🔥 JwtAuthorizationFilter add for path: {}", path);


        //exclude public URLs from needing jwt tokens
        if (path.startsWith("/auth/login")||
                path.startsWith("/users/register")||
                path.startsWith("/update/password/start") ) {
            return;
        }


        //Retrieves the Authorization header
        String authHeader = requestContext.getHeaders().getFirst("Authorization");

        //Checks if the header is present and starts with "Bearer"
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Request rejected: Authorization header missing or incorrectly informed.");
            requestContext.abortWith(Response.status(401).entity("Missing or invalid token").build());
            return;
        }
        String token = authHeader.substring("Bearer ".length());

        //Checks if token was revoked by logout
        if (authService.isTokenBlacklisted(token)) {
            logger.warn("Invalid token: token was revoked by logout.");
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Token revoked. Please log in again.").build());
            return;
        }
        try{
            // Validate and extract information from the token
            String username =JwtUtil.getUsernameFromToken(token);
            String role = JwtUtil.getRoleFromToken(token);
            //Checks if the request is secure (https)
            boolean isSecure = "https".equalsIgnoreCase(uriInfo.getRequestUri().getScheme());
            //Creates and defines the security context
            SecurityContext securityContext = new JwtSecurityContext(username, role, isSecure);
            requestContext.setSecurityContext(securityContext);

            logger.info("Request accepted: Jwt token valid for the user '{}', role '{}'.", username, role);

        } catch (Exception e) {
            //Invalid token  response 401 (Unauthorized)
            logger.warn("Invalid JWT token: {}", e.getMessage());
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Missing or invalid token").build());

        }
    }
}


