package com.demo.server.impl;

public class Handle404 extends HandleErrorResponse {
    public Handle404() {
        this("Request path not found");
    }

    public Handle404(final String message) {
        super(404, message);
    }
}
