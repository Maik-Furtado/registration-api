package br.com.mkanton.cadastroapi.dto.error;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Error response details")
public class ErrorResponse {

    @NotNull
    @Schema(description = "Error message", example = "Invalid credentials")
    private String message;

    @NotNull
    @Schema(description = "Timestamp of the error", example = "2025-07-02T15:30:00")
    private LocalDateTime timestamp = LocalDateTime.now();

    public ErrorResponse(){}
    public ErrorResponse(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

}
