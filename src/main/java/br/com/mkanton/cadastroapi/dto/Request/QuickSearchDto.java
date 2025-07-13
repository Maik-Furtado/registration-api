package br.com.mkanton.cadastroapi.dto.Request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;


@Schema(description = "DTO for quick user searches with optional filters and pagination")
public class QuickSearchDto {

    @QueryParam("value")
    @DefaultValue("")
    @Parameter(description = "ID, username or partial email to filter")
    private String value;

    @QueryParam("page")
    @DefaultValue("0")
    @Parameter(description = "Page number (zero-based)")

    private int page = 0;

    @QueryParam("size")
    @DefaultValue("10")
    @Parameter(description = "Number of results per page")
    private int size = 10;
    public QuickSearchDto() {
    }

    public QuickSearchDto(String value, int page, int size) {
        this.value = value;
        this.page = page;
        this.size = size;
    }


    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }

}
