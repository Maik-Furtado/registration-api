package br.com.mkanton.cadastroapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

@Schema(description = "Response after updating username.")
public class UserNameUpdateResponseDto {

    @NotNull
    @Schema(description = "New username after update.", example = "newUsername")
    private String newUserName;

    public UserNameUpdateResponseDto() {}

    public UserNameUpdateResponseDto( String newUserName) {

        this.newUserName = newUserName;
    }
    public String getNewUserName() {
        return newUserName;
    }

}
