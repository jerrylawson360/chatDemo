package com.demo.endpoints;

import com.demo.config.AppConfig;
import com.demo.exception.BadRequestException;
import com.demo.exception.ForbiddenException;
import com.demo.models.Chat;
import com.demo.models.ErrorResponse;
import com.demo.server.ContextResponse;
import com.demo.server.EndpointHandler;
import com.demo.service.ChatService;
import com.demo.service.UserRecord;
import com.demo.service.UserService;
import com.demo.service.impl.ChatRecordImpl;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CreateChat extends HandlerBase implements EndpointHandler {
    private final UserService userService;
    private final ChatService chatService;

    public CreateChat() {
        super(Logger.getLogger(CreateMessage.class.getName()));
        userService = AppConfig.getInstance().getUserService();
        chatService = AppConfig.getInstance().getChatService();
    }

    @Override
    public String getMethod() {
        return "post";
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
        return "CreateChat";
    }

    private Chat parseChat(final HttpExchange exchange) throws BadRequestException {
        return getValidator().parseBody(exchange.getRequestBody(), Chat.class);
    }

    private void validateChatParticipants(final Chat chat) throws BadRequestException, ForbiddenException {
        // Try to get the participants by id.
        final Map<Long, UserRecord> users = chat.getParticipantIds().stream()
            .map(id -> userService.getUser(id))
            .collect(Collectors.toMap(UserRecord::getId, Function.identity()));

        // Make sure we got one User for each userId above
        if (users.size() != chat.getParticipantIds().size()) {
            throw new BadRequestException(new ErrorResponse.Error("Unknown userId(s)"));
        }

        // Make sure that all users in the chat have each other as contacts.
        getValidator().validateContacts(users);
    }

    @Override
    public ContextResponse handle(final String rootPath, final HttpExchange exchange) throws Exception {

        final String[] parts = getPathParts(rootPath, exchange);
        if (parts.length == 1) {

            // Parse and validate the chat from the request body
            final Chat chat = getValidator().validateChat(parseChat(exchange));
            exchange.setAttribute("parsedBody", chat);   // debugging

            validateChatParticipants(chat);

            try {
                chatService.addChat(new ChatRecordImpl(chat.getId(), chat.getParticipantIds()));
                getLogger().info("Created chat " + getObjectMapper().writeValueAsString(chat));
            } catch (IllegalStateException e) {
                getLogger().severe("Chat already exists: Caught " + e);
                throw new ForbiddenException();
            }

            return new ContextResponse() {
                @Override
                public Optional<Object> getResponseValue() {
                    return Optional.empty();
                }

                @Override
                public int getResponseStatus() {
                    return 202;
                }
            };
        }

        // did not process
        return null;
    }}
