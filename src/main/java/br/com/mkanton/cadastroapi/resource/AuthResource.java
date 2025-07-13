package br.com.mkanton.cadastroapi.resource;

import br.com.mkanton.cadastroapi.domain.User;
import br.com.mkanton.cadastroapi.dto.Request.LoginDto;
import br.com.mkanton.cadastroapi.dto.response.AuthResponseDto;
import br.com.mkanton.cadastroapi.security.JwtUtil;
import br.com.mkanton.cadastroapi.service.AuthService;
import br.com.mkanton.cadastroapi.service.interfaces.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication", description = "Endpoints for user authentication (login and logout)")
public class AuthResource {

    @Inject
    private IAuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(AuthResource.class.getName());

    @POST
    @Path("/login")
    @Operation(summary = "Login", description = "Authenticates the user and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials", content = @Content(schema = @Schema(implementation = String.class)))
    })
    public Response login( @RequestBody(required = true, description = "Login credentials (username/email and password)", content = @Content(schema = @Schema(implementation = LoginDto.class)))@Valid LoginDto loginDto) {

        try {
            User user = authService.authenticate(loginDto);
            String token = JwtUtil.generateToken(user.getUsername(),user.getRole().name());

            return Response.ok(new AuthResponseDto(token,user)).build();

        }catch (IllegalArgumentException e){
            logger.warn("Failed login attempt:{}", loginDto.getValue());
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }


    @POST
    @Path("/logout")
    @Operation(summary = "Logout", description = "Logs out the user by invalidating the current token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logout successful", content = @Content(schema = @Schema(example = "Logout successful."))),
            @ApiResponse(responseCode = "400", description = "Invalid or missing token", content = @Content(schema = @Schema(example = "Missing or invalid authorization header.")))
    })
    public Response logout(@HeaderParam("Authorization") @Parameter(description = "Bearer token (e.g., 'Bearer eyJhbGciOi...')", required = true) String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing or invalid authorization header.").build();
        }

        String token = authHeader.substring("Bearer ".length());
        authService.logout(token);

        return Response.ok("Logout successful.").build();
    }

}
