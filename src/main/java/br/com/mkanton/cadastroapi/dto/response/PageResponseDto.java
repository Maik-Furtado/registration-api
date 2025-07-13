package br.com.mkanton.cadastroapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO for paginated API responses.")
public class PageResponseDto<T> {

    @Schema(description = "List of items on the current page.")
    private List<T> content;

    @Schema(description = "Current page number (zero-based).", example = "0")
    private int currentPage;

    @Schema(description = "Number of items per page.", example = "10")
    private int pageSize;

    @Schema(description = "Total number of items available.", example = "100")
    private int totalItems;

    public PageResponseDto() {}

    public PageResponseDto(List<T> content, int currentPage, int pageSize, int totalItems) {
        this.content = content;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
    public int getCurrentPage() {
        return currentPage;
    }
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }
    public int getPageSize() {
        return pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    public int getTotalItems() {
        return totalItems;
    }
    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }
}
