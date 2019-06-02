package com.demo.endpoints;

import com.demo.config.AppConfig;
import com.demo.util.IOUtils;
import com.demo.validation.Validator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class HandlerBase {
    private final Logger logger;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    HandlerBase(final Logger logger) {
        this.logger = logger;
        objectMapper = AppConfig.getInstance().getObjectMapper();
        validator = new Validator(objectMapper);
    }

    protected Logger getLogger() {
        return this.logger;
    }

    protected ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    protected Validator getValidator() {
        return this.validator;
    }

    protected String[] getPathParts(final String rootPath, final HttpExchange exchange) {
        final String normalizedPath = exchange.getRequestURI().getPath().substring(rootPath.length());
        return normalizedPath.split("/");
    }


    protected Map<String, String> parseQueryParams(final String queryString) {
        return Arrays.asList(queryString.split("&"))
            .stream()
            .map(value -> value.split("="))
            .collect(Collectors.toMap(value -> value[0].toLowerCase(), value -> value.length > 1 ? value[1] : ""));
    }

    protected Map<String, String> getQueryParams(final HttpExchange exchange) {
        return parseQueryParams(Optional.ofNullable(exchange.getRequestURI().getQuery()).orElse(""));
    }
}
