package br.com.mkanton.cadastroapi.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

/**
 * DTO to confirm a new password after validation.
 */
@Schema(description = "DTO for providing a new password after code validation.")
public class NewPasswordDto {


    @NotBlank
    @Schema(description = "New password.", example = "newPassword123", required = true)
    private String newPassword;

    @NotBlank
    @Schema(description = "Confirm new password.", example = "newPassword123", required = true)
    private String confirmPassword;

    public NewPasswordDto() {}

    public NewPasswordDto( String newPassword, String confirmPassword) {

        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
