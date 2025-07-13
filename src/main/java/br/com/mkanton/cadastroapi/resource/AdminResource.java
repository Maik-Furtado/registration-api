package br.com.mkanton.cadastroapi.resource;

import br.com.mkanton.cadastroapi.domain.User;
import br.com.mkanton.cadastroapi.dto.Request.PromoteUserDto;
import br.com.mkanton.cadastroapi.dto.Request.QuickSearchDto;
import br.com.mkanton.cadastroapi.dto.error.ErrorResponse;
import br.com.mkanton.cadastroapi.dto.response.PageResponseDto;
import br.com.mkanton.cadastroapi.service.interfaces.IAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/admin")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed("ADMIN")
@Tag(name = "Admin", description = "Operations available to system administrators")
public class AdminResource {

    @Inject
    private IAdminService adminService;

    private static final Logger logger = LoggerFactory.getLogger(AdminResource.class);


    @GET
    @Path("/search")
    @Operation(summary = "Search users", description = "Allows admin to search users by ID, email, or username. Supports pagination and partial matching."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results", content = @Content(mediaType = "application/json", schema = @Schema(implementation = PageResponseDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid search parameters", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response searchUsers(
            @Parameter(description = "Search criteria with optional value, page and size")
            @BeanParam @Valid QuickSearchDto searchCriteria) {

        try {
            PageResponseDto<User> response = adminService.search(searchCriteria);
            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid search parameters: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        }

    }


    @PUT
    @Path("/promote")
    @Operation(
            summary = "Promote user to admin",
            description = "Promotes a regular user to administrator role by their ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User successfully promoted to admin"),
            @ApiResponse(responseCode = "400", description = "User not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "User is already an admin", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public Response promoteUser(
            @Parameter(description = "DTO with user ID to promote", required = true)
            @Valid PromoteUserDto dto) {

        try {
            adminService.promoteUserToAdmin(dto.getUserId());
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.warn("User not found: {}", dto.getUserId());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse("User not found"))
                    .build();
        } catch (IllegalStateException e) {
            logger.warn("User already admin: {}", dto.getUserId());
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse("User is already an admin"))
                    .build();
        }
    }
}
