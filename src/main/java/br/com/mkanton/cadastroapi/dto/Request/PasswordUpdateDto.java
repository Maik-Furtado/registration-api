package br.com.mkanton.cadastroapi.dto.Request;

import javax.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for updating password with recovery code")
public class PasswordUpdateDto {
    @Schema(description = "Email address", example = "user@example.com")
    @NotNull
    private String email;

    @Schema(description = "Recovery code sent via email", example = "123456")
    @NotNull
    private String code;

    @Schema(description = "New password", example = "newPassword123")
    @NotNull
    private String newPassword;

    @Schema(description = "Confirmation of new password", example = "newPassword123")
    @NotNull
    private String confirmPassword;

    public PasswordUpdateDto() {}

    public PasswordUpdateDto(String email, String code, String newPassword, String confirmPassword) {
        this.email = email;
        this.code = code;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
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
