package br.com.mkanton.cadastroapi.dto.response;

import br.com.mkanton.cadastroapi.domain.User;
import br.com.mkanton.cadastroapi.domain.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

@Schema(description = "Authentication response returned after successful login")
public class AuthResponseDto {

    @NotNull
    @Schema(description = "User ID", example = "1")
    private Long id;

    @NotNull
    @Schema(description = "JWT token for authenticated access")
    private String token;

    @NotNull
    @Schema(description = "Email of the user", example = "email@example.com")
    private String email;

    @NotNull
    @Schema(description = "Username of the user", example = "maik")
    private String username;
    public AuthResponseDto(String token, User user) {
        this.token = token;
        this.email = user.getEmail();
        this.username = user.getUsername();;
    }
    public String getToken() {
        return token;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

}
