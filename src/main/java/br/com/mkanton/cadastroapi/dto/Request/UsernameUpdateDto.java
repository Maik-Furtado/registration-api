package br.com.mkanton.cadastroapi.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

@Schema(description = "DTO for updating a user's username")
public class UsernameUpdateDto {


@NotNull
@Schema(description = "New username", example = "new_username")
private String newUsername;

@NotNull
@Schema(description = "Current password", example = "user_password")
private String password;


public UsernameUpdateDto() {

}
public UsernameUpdateDto(String newUsername, String password) {
    this.newUsername = newUsername;
    this.password = password;
}
public String getNewUsername() {
    return newUsername;
}
public void setNewUsername(String newUsername) {
    this.newUsername = newUsername;
}
public String getPassword() {
    return password;
}
public void setPassword(String password) {
    this.password = password;
}

}
