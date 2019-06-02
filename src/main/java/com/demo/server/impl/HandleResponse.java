package com.demo.server.impl;

import com.demo.config.AppConfig;
import com.demo.server.ContextResponse;
import com.demo.util.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.OutputStream;

public class HandleResponse implements HttpHandler {
    private final ContextResponse response;
    private final ObjectMapper objectMapper = AppConfig.getInstance().getObjectMapper();

    public HandleResponse(final ContextResponse response) {
        this.response = response;
    }

    @Override
    public void handle(final HttpExchange exchange) {
        OutputStream os = null;

        try {
            final Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", response.getResponseFormat());

            exchange.sendResponseHeaders(response.getResponseStatus(), 0);

            // TODO: handle content-type other than application/json
            if (response.getResponseValue().isPresent()) {
                final ObjectWriter writer = objectMapper.writerWithDefaultPrettyPrinter();
                os = exchange.getResponseBody();
                writer.writeValue(os, this.response.getResponseValue().get());
            }

        } catch (Exception e) {
            System.out.println("Caught e " +e);
        } finally {
            IOUtils.close(os);
        }
    }
}
