package br.com.mkanton.cadastroapi.resource;

import br.com.mkanton.cadastroapi.domain.User;
import br.com.mkanton.cadastroapi.dto.Request.NewPasswordDto;;
import br.com.mkanton.cadastroapi.dto.Request.StartUpdatePasswordDto;
import br.com.mkanton.cadastroapi.dto.Request.ValidatePasswordUpdateCodeDto;
import br.com.mkanton.cadastroapi.dto.error.ErrorResponse;
import br.com.mkanton.cadastroapi.dto.response.PasswordUpdateResponseDto;
import br.com.mkanton.cadastroapi.dto.response.UserResponseDto;
import br.com.mkanton.cadastroapi.security.JwtUtil;
import br.com.mkanton.cadastroapi.service.interfaces.IUpdatePasswordService;
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
import javax.ws.rs.core.*;


@Path("/update/password")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Password", description = "Endpoint to update user password using recovery code")
public class UpdatePasswordResource {

    @Inject
    private IUpdatePasswordService updatePasswordService;

    @Context
    private SecurityContext securityContext;

    private static final Logger logger = LoggerFactory.getLogger(UpdatePasswordResource.class);

    @POST
    @Path("/start")
    @Operation(
            summary = "Start password update",
            description = "Initiates the password recovery process by sending a code to the user's email."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recovery initiated, token returned", content = @Content(schema = @Schema(implementation = PasswordUpdateResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(example = "User not found"))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(example = "Error processing request")))
    })
    public Response startPasswordUpdate(StartUpdatePasswordDto dto) {
        try {
            User user = updatePasswordService.startPasswordUpdate(dto);
            String token = JwtUtil.generateToken(user.getUsername(), user.getRole().name());

            return Response.ok(new PasswordUpdateResponseDto(token)).build();

        } catch (IllegalArgumentException e) {
            logger.warn("User Not found: {}", dto.getEmail());
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e) {
            logger.error("Error starting password update", e);
            return Response.serverError().entity("Error processing request").build();
        }
    }


    @POST
    @Path("/validate/code")
    @Operation(
            summary = "Validate recovery code",
            description = "Validates the recovery code provided by the user before allowing password update."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code validated successfully", content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or expired recovery code", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response validateCode(@Valid ValidatePasswordUpdateCodeDto dto) {
        try {

            String username = securityContext.getUserPrincipal().getName();


            boolean isValid = updatePasswordService.validateCodePswUpdate(username, dto);

            if (isValid) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(new ErrorResponse("Invalid or expired recovery code")).build();
            }

        } catch (IllegalArgumentException e) {
            logger.error("User not found or invalid data: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorResponse("User not found")).build();

        } catch (Exception e) {
            logger.error("Unexpected error during code validation", e);
            return Response.serverError()
                    .entity(new ErrorResponse("Internal server error while validating code")).build();
        }
    }



    @PUT
    @Path("/new/password")
    @Operation(
            summary = "Update password",
            description = "Updates the user's password after validating the recovery code."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Operation successful"),
            @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response updatePassword(@Valid NewPasswordDto dto) {
        try {
            String username = securityContext.getUserPrincipal().getName();

            User updatedUser = updatePasswordService.updatePassword(dto, username);

            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("Invalid data")).build();
        } catch (IllegalStateException e) {
            return Response.serverError()
                    .entity(new ErrorResponse("Error updating password")).build();
        } catch (Exception e) {
            return Response.serverError()
                    .entity(new ErrorResponse("Internal failure")).build();
        }

    }
}
