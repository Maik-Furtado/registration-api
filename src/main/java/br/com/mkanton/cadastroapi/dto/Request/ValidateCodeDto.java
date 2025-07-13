package br.com.mkanton.cadastroapi.dto.Request;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

@Schema(description = "DTO to validate a code sent to the user")
public class ValidateCodeDto {

    @NotNull(message = "Code is Required.")
    @Schema(description = "Code sent to the user", example = "ABC123")
    private String code;

        public ValidateCodeDto() {}

        public ValidateCodeDto( String code) {
            this.code = code;
        }
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

    }
