package br.com.mkanton.cadastroapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;


@Schema(description = "DTO representing the result of a code validation.")
public class ValidationResponseDto {

    @NotNull
    @Schema(description = "Indicates whether the code is valid.", example = "true")
    private boolean valid;
    
    public ValidationResponseDto(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }
    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
