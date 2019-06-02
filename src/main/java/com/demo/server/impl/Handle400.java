package com.demo.server.impl;

import com.demo.exception.BadRequestException;

public class Handle400 extends HandleErrorResponse {
    public Handle400(final BadRequestException e) {
        super(400, e.getErrorResponse());
    }
}
