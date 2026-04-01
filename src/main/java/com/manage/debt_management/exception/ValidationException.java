package com.manage.debt_management.exception;

import java.util.Collections;
import java.util.Map;

public class ValidationException extends RuntimeException {

    private final Map<String, String> fieldErrors;

    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors == null ? Collections.emptyMap() : fieldErrors;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}