package br.com.mkanton.cadastroapi.dto.Request;

import javax.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

     @Schema(description = "DTO for login request")
    public class LoginDto {

        @Schema(description = "Username or email", example = "email@example.com")
        @NotNull
        private String value;

        @Schema(description = "User password", example = "myPassword123")
        @NotNull
        private String password;


        public LoginDto() {}

        public LoginDto(String value, String password) {
            this.value = value;
            this.password = password;
        }

        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }

