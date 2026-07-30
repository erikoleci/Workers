package com.buildcrew.common.dto;

import java.util.List;

public class PageResponse<T> {

    public List<T> items;
    public int page;
    public int size;
    public long total;
    public int totalPages;

    public PageResponse(List<T> items, int page, int size, long total) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.total = total;
        this.totalPages = (int) Math.ceil((double) total / size);
    }
}
