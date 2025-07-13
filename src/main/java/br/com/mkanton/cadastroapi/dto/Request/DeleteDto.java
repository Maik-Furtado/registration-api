package br.com.mkanton.cadastroapi.dto.Request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for deleting a user")
public class DeleteDto {

    @NotBlank
    @Schema(description = "User's current password", example = "myPassword123")
    private String password;

    public DeleteDto() {}
    public DeleteDto( String password) {
        this.password = password;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
