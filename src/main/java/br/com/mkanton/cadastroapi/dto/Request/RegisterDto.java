package br.com.mkanton.cadastroapi.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;

@Schema(description = "DTO used to register new users")
public class RegisterDto {

    @NotNull
    @Schema(description = "Username of the new user", example = "maik")
    private String username;

    @NotNull
    @Schema(description = "Email of the new user", example = "email@example.com")
    private String email;

    @NotNull
    @Schema(description = "Password for the new user", example = "12345678")
    private String password;

    public RegisterDto() {

    }
    public RegisterDto(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

}
