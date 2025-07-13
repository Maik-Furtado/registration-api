package br.com.mkanton.cadastroapi.config;

import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/actrl/api")
@OpenAPIDefinition(
        info = @Info(
                title       = "Register API",
                version     = "1.0.0",
                description = "API for user management",
                contact     = @Contact(name = "Maik", email = "maikgafurtado@gmail.com")
        ),
        servers = {
                @Server(url = "https://localhost:8443/cadastroapi/actrl/api",
                        description = "Local HTTPS server")
        }
)

public class SwaggerConfig extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        classes.add(br.com.mkanton.cadastroapi.resource.UserResource.class);
        classes.add(br.com.mkanton.cadastroapi.resource.AdminResource.class);
        classes.add(br.com.mkanton.cadastroapi.resource.AuthResource.class);
        classes.add(br.com.mkanton.cadastroapi.resource.UpdatePasswordResource.class);

        classes.add(OpenApiResource.class);
        classes.add(AcceptHeaderOpenApiResource.class);

        return classes;
    }
}
