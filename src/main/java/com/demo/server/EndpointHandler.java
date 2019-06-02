package com.demo.server;

import com.sun.net.httpserver.HttpExchange;

public interface EndpointHandler {
    /**
     * Get HTTP method, eg., get, put, post, delete
     * @return HTTP method name
     */
    String getMethod();

    /**
     * Get path regexp
     * @return The path of this endpoint, which can be a regexp string
     */
    String getPath();

    /**
     * Get context path
     * @return
     */
    String getContextPath();

    /**
     * Get user-friendly name (appropriate for logging)
     * @return Name of this handler
     */
    String getName();

    /**
     * Handle the incoming HttpExchange.
     * @param rootPath The registered root path of the application
     * @param exchange The incoming HttpExchange.
     * @return response, non-null if handled
     */
    ContextResponse handle(final String rootPath, final HttpExchange exchange) throws Exception;
}
