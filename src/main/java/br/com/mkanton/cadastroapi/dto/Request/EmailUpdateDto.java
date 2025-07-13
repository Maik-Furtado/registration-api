package br.com.mkanton.cadastroapi.dto.Request;

import javax.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for updating email")
public class EmailUpdateDto {

    @Schema(description = "New email address", example = "newemail@example.com")
    @NotNull
    private String newEmail;

    @Schema(description = "Current password", example = "myPassword123")
    @NotNull
    private String password;

    public EmailUpdateDto() {

    }
    public EmailUpdateDto(String newEmail, String password) {
        this.newEmail = newEmail;
        this.password = password;
    }

    public String getNewEmail() {
        return newEmail;
    }
    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}
