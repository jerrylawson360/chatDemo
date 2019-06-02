package com.demo.server.impl;

public class Handle500 extends HandleErrorResponse {
    public Handle500(final Exception e) {
        super(500, e.getMessage());
    }

    public Handle500(final String error) {
        super(500, error);
    }
}
