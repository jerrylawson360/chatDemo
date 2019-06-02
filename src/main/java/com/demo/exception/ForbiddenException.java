package com.demo.exception;

import com.demo.models.ErrorResponse;

public class ForbiddenException extends Exception {
    private ErrorResponse errorResponse = new ErrorResponse();

    public ForbiddenException() {
        super("Forbidden");
    }

    public ForbiddenException(final ErrorResponse.Error error) {
        this();
        this.errorResponse.addError(error);
    }

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }
}
