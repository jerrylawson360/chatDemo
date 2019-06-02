package com.demo.server;

import com.sun.net.httpserver.HttpHandler;

public interface ContextMapper {
    String getContextPath();
    HttpHandler getHandler();
    void addEndpointHandler(final EndpointHandler handler);
    void start();
}
