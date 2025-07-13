package br.com.mkanton.cadastroapi.dto.Request;

import javax.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO for promoting a user to admin")
public class PromoteUserDto {

    @Schema(description = "User ID to promote", example = "7")
    @NotNull(message = "UserId is required.")
    private Long userId;

    public PromoteUserDto() {}

    public PromoteUserDto(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
