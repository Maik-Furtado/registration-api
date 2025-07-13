package br.com.mkanton.cadastroapi.dto.response;

import br.com.mkanton.cadastroapi.domain.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

@Schema(description = "DTO representing user details.")
public class UserResponseDto {

    @NotNull
    @Schema(description = "Email of the user.", example = "user@example.com")
    private String email;

    @NotNull
    @Schema(description = "User status (e.g., ACTIVE, VALIDATING, etc.)", example = "ACTIVE")
    private Status status;

    public UserResponseDto() {}

    public UserResponseDto(String email, Status status) {
        this.email = email;
        this.status = status;
    }


    public String getEmail() {
        return email;
    }

    public Status getStatus() {
        return status;
    }

}
