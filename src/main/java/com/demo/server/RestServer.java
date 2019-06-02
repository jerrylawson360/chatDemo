package com.demo.server;

import java.io.IOException;
import java.util.concurrent.Executor;

public interface RestServer {
    /**
     * Set Executor to handle incoming requests.
     */
    RestServer setExecutor(final Executor executor) throws IllegalStateException;

    /**
     * Add EndpointHandler
     */
    RestServer addEndpointHandler(final EndpointHandler handler) throws IllegalStateException;

    /**
     * Set listen port
     */
    RestServer setListenPort(int port) throws IllegalStateException;

    /**
     * Set TCP connection backlog
     */
    RestServer setBacklog(int maxTCPBacklog) throws IllegalStateException;

    /**
     * Start server
     */
    void start() throws IOException;

    /**
     * Stop server
     */
    void stop(int maxSecondsToWait);
}
