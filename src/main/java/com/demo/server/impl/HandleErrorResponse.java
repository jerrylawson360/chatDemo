package com.demo.server.impl;

import com.demo.config.AppConfig;
import com.demo.models.ErrorResponse;
import com.demo.util.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.OutputStream;

public class HandleErrorResponse implements HttpHandler {
    private final int status;
    private final ErrorResponse errorResponse;
    private final ObjectMapper objectMapper = AppConfig.getInstance().getObjectMapper();

    public HandleErrorResponse(final int status, final ErrorResponse errorResponse) {
        this.status = status;
        this.errorResponse = errorResponse;
    }

    public HandleErrorResponse(final int status, final String message) {
        this.status = status;
        this.errorResponse = new ErrorResponse();
        this.errorResponse.getErrors().add(new ErrorResponse.Error(message));
    }

    @Override
    public void handle(final HttpExchange exchange) {
        OutputStream os = null;

        try {
            final Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", "application/json");

            exchange.sendResponseHeaders(status, 0);

            final ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
            os = exchange.getResponseBody();
            writer.writeValue(os, this.errorResponse);

        } catch (Exception e) {
            System.out.println("Caught e " +e);
        } finally {
            IOUtils.close(os);
        }
    }
}
