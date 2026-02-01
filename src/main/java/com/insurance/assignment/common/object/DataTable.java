package com.insurance.assignment.common.object;


import org.springframework.data.domain.Page;

import java.util.List;

public class DataTable {
    private final Long totalElements;
    private final int totalPages;

    private List<?> content;

    public DataTable(Page<?> page) {
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.content = content;
    }

    public DataTable(Long totalElements, int totalPages, List<?> content) {
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.content = content;
    }
}
