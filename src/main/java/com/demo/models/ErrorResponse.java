package com.demo.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Model for error response message
 */
public class ErrorResponse {
    private List<Error> errors = new ArrayList<>();

    public List<Error> getErrors() {
        return this.errors;
    }

    public void addError(final Error error) {
        this.errors.add(error);
    }

    public static class Error {
        private final String message;

        public Error(final String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

    }
}
