package com.demo.exception;

import com.demo.models.ErrorResponse;

public class BadRequestException extends Exception {
    private ErrorResponse errorResponse = new ErrorResponse();

    public BadRequestException() {
        super("Bad Request");
    }

    public BadRequestException(final ErrorResponse errorResponse) {
        this();
        this.errorResponse = errorResponse;
    }

    public BadRequestException(final ErrorResponse.Error error) {
        this();
        this.errorResponse.addError(error);
    }

    public ErrorResponse getErrorResponse() {
        return this.errorResponse;
    }
}
