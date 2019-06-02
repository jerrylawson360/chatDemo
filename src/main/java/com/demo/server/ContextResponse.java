package com.demo.server;

import java.util.Optional;

public interface ContextResponse {
    Optional<Object> getResponseValue();
    int getResponseStatus();
    default String getResponseFormat() {
        return "application/json";
    }
}
