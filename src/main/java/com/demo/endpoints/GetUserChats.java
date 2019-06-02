package com.demo.endpoints;

import com.demo.config.AppConfig;
import com.demo.exception.BadRequestException;
import com.demo.exception.ForbiddenException;
import com.demo.models.Chat;
import com.demo.models.ChatList;
import com.demo.models.ErrorResponse;
import com.demo.models.Message;
import com.demo.server.ContextResponse;
import com.demo.server.EndpointHandler;
import com.demo.service.ChatRecord;
import com.demo.service.UserRecord;
import com.demo.service.UserService;
import com.demo.util.IOUtils;
import com.sun.net.httpserver.HttpExchange;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GetUserChats extends HandlerBase implements EndpointHandler {
    private final UserService userService;

    public GetUserChats() {
        super(Logger.getLogger(GetUserChats.class.getName()));
        userService = AppConfig.getInstance().getUserService();
    }

    @Override
    public String getMethod() {
        return "get";
    }

    @Override
    public String getPath() {
        return "chats";
    }

    @Override
    public String getContextPath() {
        return "chats";
    }

    @Override
    public String getName() {
        return "GetUserChats";
    }

    private UserRecord validateUser(final Long userId)
        throws BadRequestException {

        // Make sure user does exist
        final UserRecord userRecord = userService.getUser(userId);
        if (userRecord == null) {
            throw new BadRequestException(new ErrorResponse.Error("Invalid userId"));
        }

        return userRecord;
    }

    @Override
    public ContextResponse handle(final String rootPath, final HttpExchange exchange) throws Exception {
        final URI uri = exchange.getRequestURI();

        final String[] parts = getPathParts(rootPath, exchange);
        final Map<String, String> queryParams = getQueryParams(exchange);

        final String userIdStr = queryParams.get("userid");

        if (parts.length == 1 &&
            userIdStr != null) {

            final UserRecord user = validateUser(getValidator().getUserId(userIdStr));

            return new ContextResponse() {
                @Override
                public Optional<Object> getResponseValue() {
                    return Optional.of(new ChatList(user.getChats().stream()
                        .map(ChatRecord::getId).collect(Collectors.toList())));
                }

                @Override
                public int getResponseStatus() {
                    return 200;
                }
            };
        }

        // did not process
        return null;
    }
}
