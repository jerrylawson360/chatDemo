package com.demo.endpoints;

import com.demo.config.AppConfig;
import com.demo.exception.BadRequestException;
import com.demo.exception.ForbiddenException;
import com.demo.models.ErrorResponse;
import com.demo.models.Message;
import com.demo.models.MessageList;
import com.demo.server.ContextResponse;
import com.demo.server.EndpointHandler;
import com.demo.service.ChatRecord;
import com.demo.service.ChatService;
import com.demo.service.UserService;
import com.sun.net.httpserver.HttpExchange;

import java.net.URI;
import java.util.Optional;
import java.util.logging.Logger;

public class GetChatMessages extends HandlerBase implements EndpointHandler {
    private final ChatService chatService;

    public GetChatMessages() {
        super(Logger.getLogger(GetChatMessages.class.getName()));
        chatService = AppConfig.getInstance().getChatService();
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
        return "GetChatMessages";
    }

    private ChatRecord validateChat(final Long chatId)
        throws BadRequestException, ForbiddenException {

        // Make sure chat does exist
        final ChatRecord chat = chatService.getChat(chatId);
        if (chat == null) {
            throw new BadRequestException(new ErrorResponse.Error("Invalid chatId"));
        }

        return chat;
    }

    @Override
    public ContextResponse handle(final String rootPath, final HttpExchange exchange) throws Exception {

        final String[] parts = getPathParts(rootPath, exchange);
        if (parts.length == 3 &&
            parts[0].equals("chats") &&
            parts[2].equals("messages")) {

            // Try to parse a number and find the existing ChatRecord
            final ChatRecord chat = validateChat(getValidator().getChatId(parts[1]));

            return new ContextResponse() {
                @Override
                public Optional<Object> getResponseValue() {
                    return Optional.of(new MessageList(chat.getMessages()));
                }

                @Override
                public int getResponseStatus() {
                    return 202;
                }
            };
        }

        // did not process
        return null;
    }
}
