package com.sba302.electroshop.exception;

import java.util.List;

public class ApiException extends RuntimeException {
    private final List<String> errors;

    public ApiException(List<String> errors) {
        super(String.join("; ", errors));
        this.errors = errors;
    }

    public ApiException(String message) {
        super(message);
        this.errors = List.of(message);
    }

    public List<String> getErrors() {
        return errors;
    }
}
