package com.manage.debt_management.exception;

public class ConflictException extends RuntimeException {

    private final String field;
    private final String value;

    public ConflictException(String message, String field, String value) {
        super(message);
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }
}
