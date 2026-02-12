package com.lifemap.model;

public enum SortBy {
    NAME_ASC("name", "asc"),
    NAME_DESC("name", "desc"),
    RATE_ASC("rate", "asc"),
    RATE_DESC("rate", "desc");

    private final String field;
    private final String order;

    SortBy(String field, String order) {
        this.field = field;
        this.order = order;
    }
}
