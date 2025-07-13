package br.com.mkanton.cadastroapi.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * DTO to request a password reset email.
 */
@Schema(description = "DTO to initiate a password reset by providing the user's email.")
public class StartUpdatePasswordDto {

    @NotBlank
    @Email
    @Schema(description = "Email address to receive the password reset code.", example = "user@example.com", required = true)
    private String email;

    public StartUpdatePasswordDto() {}

    public StartUpdatePasswordDto(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
