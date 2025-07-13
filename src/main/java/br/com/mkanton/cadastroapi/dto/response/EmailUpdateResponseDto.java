package br.com.mkanton.cadastroapi.dto.response;

import br.com.mkanton.cadastroapi.domain.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;

@Schema(description = "Response DTO for email update requests")
public class EmailUpdateResponseDto {


    @NotNull
    @Schema(description = "Current email address", example = "old@example.com")
    private String currentEmail;

    @NotNull
    @Schema(description = "Email waiting for confirmation", example = "new@example.com")
    private String pendingEmail;

    @NotNull
    @Schema(description = "Current status of the user")
    private Status status;

    public EmailUpdateResponseDto(){}

    public EmailUpdateResponseDto( String currentEmail, String pendingEmail, Status status) {
        this.currentEmail = currentEmail;
        this.pendingEmail = pendingEmail;
        this.status = status;
    }

    public String getCurrentEmail() {
        return currentEmail;
    }

    public String getPendingEmail() {
        return pendingEmail;
    }

    public Status getStatus() {
        return status;
    }

}
