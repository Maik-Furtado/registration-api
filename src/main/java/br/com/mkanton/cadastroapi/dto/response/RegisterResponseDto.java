package br.com.mkanton.cadastroapi.dto.response;

import br.com.mkanton.cadastroapi.domain.enums.Status;

public class RegisterResponseDto {


    private String email;

    private Status status;

    private String token;

    public RegisterResponseDto() {
    }
    public RegisterResponseDto(String email, Status status, String token) {
        this.email = email;
        this.status = status;
        this.token = token;
    }
    public String getEmail() {
        return email;
    }

    public Status getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }
}
