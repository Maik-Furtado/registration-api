package br.com.mkanton.cadastroapi.resource;


import br.com.mkanton.cadastroapi.domain.User;
import br.com.mkanton.cadastroapi.dto.Request.*;
import br.com.mkanton.cadastroapi.dto.error.ErrorResponse;
import br.com.mkanton.cadastroapi.dto.response.RegisterResponseDto;
import br.com.mkanton.cadastroapi.dto.response.UserResponseDto;
import br.com.mkanton.cadastroapi.dto.response.ValidationResponseDto;
import br.com.mkanton.cadastroapi.security.JwtSecurityContext;
import br.com.mkanton.cadastroapi.security.JwtUtil;
import br.com.mkanton.cadastroapi.service.interfaces.IAuthService;
import br.com.mkanton.cadastroapi.service.interfaces.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "User Resource", description = "Endpoints for user registration, update, and validation")
public class UserResource {

    @Inject
    private IUserService userService;

    @Inject
    private IAuthService authService;

    @Context
    private SecurityContext securityContext;

   private static final Logger logger = LoggerFactory.getLogger(UserResource.class);

    @POST
    @Path("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user with provided email, username, and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response registerUser(@Valid RegisterDto dto) {
        System.out.println(" Entrou no registerUser()");
        try {
            User user = userService.register(dto);
            String token = JwtUtil.generateToken(user.getUsername(), user.getRole().name());
            return Response.status(Response.Status.CREATED).entity(new RegisterResponseDto(user.getEmail(), user.getStatus(), token)).build();

        } catch (IllegalArgumentException e) {
            logger.warn("Registration failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse(e.getMessage())).build();
        }
    }
    @DELETE
    @Path("/delete")
    @Operation(summary = "Deletes a user", description = "Deletes a user by validating their credentials")
    public Response delete(@Valid DeleteDto dto) {
        String username = securityContext.getUserPrincipal().getName();
        try {
            userService.deleteUser(username, dto.getPassword());
            return Response.status(Response.Status.NO_CONTENT).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Delete failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse(e.getMessage())).build();
        } catch (SecurityException e) {
            logger.warn("Unauthorized delete attempt");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @PUT
    @Path("/update/email")
    @Operation(summary = "Updates user's email", description = "Initiates email update process by sending a confirmation code")
    public Response emailUpdate(@Valid EmailUpdateDto dto) {
        String username = securityContext.getUserPrincipal().getName();
        try {
            User user = userService.updateEmail(username, dto.getNewEmail(), dto.getPassword());
            return Response.ok(new UserResponseDto(user.getPendingEmail(), user.getStatus())).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Email update failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse(e.getMessage())).build();
        } catch (SecurityException e) {
            logger.warn("Unauthorized email update attempt");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @PUT
    @Path("/update/username")
    @Operation(summary = "Updates user's username", description = "Changes the username after authentication")
    public Response updateUsername(@Valid UsernameUpdateDto dto) {
        String username = securityContext.getUserPrincipal().getName();
        try {
            User user = userService.updateUsername(username, dto.getNewUsername(), dto.getPassword());
            return Response.ok(new UserResponseDto(user.getEmail(), user.getStatus())).build();
        } catch (IllegalArgumentException e) {
            logger.warn("Update failed: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse(e.getMessage())).build();
        } catch (SecurityException e) {
            logger.warn("Unauthorized update attempt");
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorResponse(e.getMessage())).build();
        }
    }

    @POST
    @Path("/validate/register")
    @Operation(summary = "Validates registration code", description = "Checks the user code and removes the temporary token on success")
    public Response validateRegistrationCode(@Valid ValidateCodeDto dto, @HeaderParam("Authorization") String authHeader) {
        String username = securityContext.getUserPrincipal().getName();
        boolean isValid = userService.validateRegistrationCode(username, dto.getCode());
        if (isValid && authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length());
            authService.logout(token);
        }
        return Response.ok(new ValidationResponseDto(isValid)).build();
    }

    @POST
    @Path("/validate/email")
    @Operation(summary = "Validates email update code", description = "Checks if the code for confirming email update is valid")
    public Response validateEmailUpdateCode(@Valid ValidateCodeDto dto) {
        String username = securityContext.getUserPrincipal().getName();
        System.out.println("SECURITY: " + securityContext);
        System.out.println("PRINCIPAL: " +
                (securityContext != null ? securityContext.getUserPrincipal() : "null"));

        boolean isValid = userService.validateEmailUpdate(username, dto.getCode());
        return Response.ok(new ValidationResponseDto(isValid)).build();
    }

}
