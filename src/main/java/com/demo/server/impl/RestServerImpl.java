package com.demo.server.impl;

import com.demo.server.ContextMapper;
import com.demo.server.EndpointHandler;
import com.demo.server.RestServer;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.logging.Logger;

/**
 * RestServer implementation.
 */
public class RestServerImpl implements RestServer {
    private final static Logger logger = Logger.getLogger(RestServer.class.getName());
    protected final String rootPath;
    protected HttpServer server;
    protected Executor executor;
    protected Map<String, ContextMapper> contextMappers = new HashMap<>();
    protected int port = 8080;
    protected int backlog = 100;

    public RestServerImpl(final String rootPath) {
        if (!rootPath.startsWith("/")) {
            throw new IllegalArgumentException("rootPath must begin with '/'");
        }

        // Ensure that rootPath ends with /
        if (!rootPath.endsWith("/")) {
            this.rootPath = rootPath + "/";
        } else {
            this.rootPath = rootPath;
        }

        // Default context mapper, to ensure we handle all unknown requests ourselves
        contextMappers.put("", new ContextMapperImpl(rootPath, ""));
    }

    private void checkStarted() throws IllegalStateException {
        if (server != null) {
            throw new IllegalStateException("Server has already started");
        }
    }

    @Override
    public RestServer setExecutor(final Executor executor)  throws IllegalStateException {
        checkStarted();
        this.executor = executor;
        return this;
    }

    @Override
    public RestServer addEndpointHandler(final EndpointHandler handler)  throws IllegalStateException {
        checkStarted();

        if (handler.getPath().startsWith("/")) {
            throw new IllegalArgumentException(String.format("handler path must NOT begin with '/' (%s)",
                handler.getPath()));
        }

        if (handler.getContextPath().startsWith("/")) {
            throw new IllegalArgumentException(String.format("handler contextPath must NOT begin with '/' (%s)",
                handler.getContextPath()));
        }

        // Get existing ContextMapper, or create a new one as needed.
        ContextMapper mapper = contextMappers.get(handler.getContextPath());
        if (mapper == null) {
            mapper = new ContextMapperImpl(rootPath, handler.getContextPath());

            contextMappers.put(mapper.getContextPath(), mapper);
        }
        mapper.addEndpointHandler(handler);

        return this;
    }

    @Override
    public RestServer setListenPort(final int port)  throws IllegalStateException {
        checkStarted();
        this.port = port;
        return this;
    }

    @Override
    public RestServer setBacklog(final int maxTCPBacklog) throws IllegalStateException {
        checkStarted();
        this.backlog = maxTCPBacklog;
        return this;
    }

    @Override
    public void start() throws IOException {
        checkStarted();

        server = HttpServer.create(new InetSocketAddress(port), backlog);
        Optional.ofNullable(this.executor)
            .ifPresent(server::setExecutor);

        // Register each ContextMapper's ContextHandler at the path following our rootPath.
        contextMappers.values().stream()
            .forEach(mapper -> {
                final String contextPath = this.rootPath + mapper.getContextPath();
                logger.info("Registering context path: " + contextPath);
                server.createContext(contextPath, mapper.getHandler());
            });

        logger.info("Starting server on port " + this.port);
        server.start();
    }

    @Override
    public void stop(int maxSecondsToWait) {
        if (server != null) {
            server.stop(maxSecondsToWait);
            server = null;
        }
    }
}
