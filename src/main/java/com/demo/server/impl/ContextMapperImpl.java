package com.demo.server.impl;

import com.demo.config.AppConfig;
import com.demo.exception.BadRequestException;
import com.demo.exception.ForbiddenException;
import com.demo.server.ContextMapper;
import com.demo.server.ContextResponse;
import com.demo.server.EndpointHandler;
import com.demo.util.IOUtils;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Implementation of ContextMapper.
 * This implementation assumes that no new handlers will be added once the owning RestServer has started.
 */
public class ContextMapperImpl implements ContextMapper, HttpHandler {
    private final static Logger logger = Logger.getLogger(ContextMapperImpl.class.getName());
    private final String rootPath;
    private final String contextPath;
    private Map<String, List<EndpointHandler>> handlersByMethod = new HashMap<>();

    public ContextMapperImpl(final String rootPath, final String contextPath) {
        this(rootPath, contextPath, Set.of("get", "put", "post", "delete"));
    }

    public ContextMapperImpl(final String rootPath, final String contextPath, final Set<String> validMethods) {
        this.rootPath = rootPath;
        this.contextPath = contextPath;

        // Create lists ahead of time, one per method.
        validMethods.stream()
            .map(s -> s.toUpperCase())
            .forEach(method -> handlersByMethod.put(method, new ArrayList<>()));
    }

    @Override
    public void start() {
        handlersByMethod = Map.copyOf(handlersByMethod);
    }

    @Override
    public String getContextPath() {
        return this.contextPath;
    }

    @Override
    public HttpHandler getHandler() {
        return this;
    }

    private String formatContextObject(final HttpExchange exchange, final String name) {
        return IOUtils.formatObject(exchange.getAttribute(name));
    }

    // TODO: check for path conflicts
    @Override
    public void addEndpointHandler(final EndpointHandler handler) {
        final String method = handler.getMethod().toUpperCase();

        if (!handlersByMethod.containsKey(method)) {
            throw new IllegalArgumentException("ContextMapper does not support HTTP method " + method);
        }
        handlersByMethod.get(method).add(handler);
        logger.info(String.format("Registering %s %s (%s)", method, handler.getPath(), handler.getName()));
    }

    protected String getExceptionMessage(final Exception e) {
        ByteArrayOutputStream outputStream = null;
        PrintStream ps = null;

        try {
            outputStream = new ByteArrayOutputStream();

            ps = new PrintStream(outputStream);

            e.printStackTrace(ps);

            return "Caught unhandled exception " + e.getMessage() + "\n" + outputStream.toString(StandardCharsets.UTF_8);
        } catch (Exception e2) {
            return "Caught unhandled exception " + e.getMessage();

        } finally {
            IOUtils.close(ps);
            IOUtils.close(outputStream);
        }
    }

    protected String getTimestamp(final HttpExchange exchange) {
        return Optional.ofNullable(exchange.getAttribute("timestamp"))
            .map(obj -> {
                final Instant instant = (Instant) obj;
                return String.format("%d:%d", instant.getEpochSecond(), instant.getNano());
            })
            .orElse("");
    }

    protected String getThreadId() {
        return Thread.currentThread().getName();
    }

    protected ContextResponse handleRequest(final HttpExchange exchange) throws Exception {
        // Get the method and see if there are any handlers.
        final List<EndpointHandler> list = handlersByMethod.get(exchange.getRequestMethod().toUpperCase());
        if (list != null) {
            // Iterate through the list until one handler does it.
            for (final EndpointHandler handler : list) {
                final ContextResponse response = handler.handle(rootPath, exchange);
                if (response != null) {
                    final String body = formatContextObject(exchange, "parsedBody");
                    final String resp = response.getResponseValue()
                        .map(obj -> IOUtils.formatObject(obj))
                        .map(s -> "\n  " + s)
                        .orElse("");

                    logger.info(String.format("%s %s %s %s %d %s%s",
                        getThreadId(),
                        getTimestamp(exchange),
                        exchange.getRequestMethod(), exchange.getRequestURI(), response.getResponseStatus(), body, resp));

                    return response;
                }
            }
        }
        return null;
    }

    protected HttpHandler determineResponseHandler(final HttpExchange exchange) {
        try {
            final ContextResponse response = handleRequest(exchange);
            if (response != null) {
                return new HandleResponse(response);
            } else {
                logger.severe(String.format("%s %s %s %s %d",
                    getThreadId(),
                    getTimestamp(exchange),
                    exchange.getRequestMethod(), exchange.getRequestURI(), 400));

                return new Handle404();
            }

        } catch (ForbiddenException e) {
            logger.severe(String.format("%s %s %s %s %d",
                getThreadId(),
                getTimestamp(exchange),
                exchange.getRequestMethod(), exchange.getRequestURI(), 403));

            return new HandleErrorResponse(403, e.getErrorResponse());

        } catch (BadRequestException e) {
            final String body = formatContextObject(exchange, "parsedBody");
            final String resp = IOUtils.formatObject(e.getErrorResponse());

            logger.severe(String.format("%s %s %s %s %d %s\n %s",
                getThreadId(),
                getTimestamp(exchange),
                exchange.getRequestMethod(), exchange.getRequestURI(), 400, body, resp));

            return new Handle400(e);

        } catch (Exception e) {
            logger.severe(() -> getExceptionMessage(e));
            return new Handle500(e);

        }
    }

    @Override
    public void handle(final HttpExchange exchange) {
        // Store arrival timestamp for later logging
        exchange.setAttribute("timestamp", Instant.now());

        final HttpHandler handler = determineResponseHandler(exchange);
        try {
            handler.handle(exchange);

        } catch (Exception e) {
            // Exception caught when sending the response. Do once more last-gasp effort before we give up.
            try {
                new Handle500(e).handle(exchange);
            } catch (Exception e2) {
                logger.severe(() -> getExceptionMessage(e));
            }

        } finally {
            exchange.close();
        }
    }

}
