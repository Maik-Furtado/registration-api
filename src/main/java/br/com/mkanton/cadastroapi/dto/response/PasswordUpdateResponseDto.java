package br.com.mkanton.cadastroapi.dto.response;

import br.com.mkanton.cadastroapi.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response for password update requests.")
public class PasswordUpdateResponseDto {
    private String token;



    public PasswordUpdateResponseDto(String token) {
        this.token = token;

    }


    public String getToken() {
        return token;
    }

}
