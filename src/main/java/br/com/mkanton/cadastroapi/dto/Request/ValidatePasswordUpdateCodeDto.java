package br.com.mkanton.cadastroapi.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

@Schema(description = "DTO for validating a recovery code during password update")
public class ValidatePasswordUpdateCodeDto {

    @NotBlank
    @Schema(description = "Recovery code sent to the user's email", example = "ABC123")
    private String code;

    public ValidatePasswordUpdateCodeDto() {
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
}
