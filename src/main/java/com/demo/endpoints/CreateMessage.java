package com.demo.endpoints;

import com.demo.config.AppConfig;
import com.demo.exception.BadRequestException;
import com.demo.exception.ForbiddenException;
import com.demo.models.ErrorResponse;
import com.demo.models.Message;
import com.demo.server.ContextResponse;
import com.demo.server.EndpointHandler;
import com.demo.service.ChatRecord;
import com.demo.service.ChatService;
import com.demo.service.UserService;
import com.sun.net.httpserver.HttpExchange;

import java.util.Optional;
import java.util.logging.Logger;

public class CreateMessage extends HandlerBase implements EndpointHandler {
    private final ChatService chatService;

    public CreateMessage() {
        super(Logger.getLogger(CreateMessage.class.getName()));
        chatService = AppConfig.getInstance().getChatService();
    }

    @Override
    public String getMethod() {
        return "post";
    }

    @Override
    public String getPath() {
        return "chats/{chatId}/messages";
    }

    @Override
    public String getContextPath() {
        return "chats";
    }

    @Override
    public String getName() {
        return "CreateMessage";
    }

    private Message parseMessage(final HttpExchange exchange) throws BadRequestException {
        return getValidator().parseBody(exchange.getRequestBody(), Message.class);
    }

    private ChatRecord validateChat(final Long chatId, final Message message)
            throws BadRequestException, ForbiddenException {

        // Make sure chat does exist
        final ChatRecord chat = chatService.getChat(chatId);
        if (chat == null) {
            throw new BadRequestException(new ErrorResponse.Error("Invalid chatId"));
        }

        // Make sure chat users match up to the message users.
        if (!chat.getParticipantIds().contains(message.getSourceUserId()) ||
            !chat.getParticipantIds().contains(message.getDestinationUserId())) {

            // Don't tell them why it's forbidden.
            throw new ForbiddenException();
        }

        return chat;
    }

    @Override
    public ContextResponse handle(final String rootPath, final HttpExchange exchange) throws Exception {

        final String[] parts = getPathParts(rootPath, exchange);
        if (parts.length == 3 &&
            parts[0].equals("chats") &&
            parts[2].equals("messages")) {

            // Parse and validate the message from the request body
            final Message message = getValidator().validateMessage(parseMessage(exchange));
            exchange.setAttribute("parsedBody", message);   // debugging

            // Try to parse a number and find the existing ChatRecord
            final ChatRecord chat = validateChat(getValidator().getChatId(parts[1]), message);

            // Add the new message to the ChatRecord
            chat.addMessage(message);

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
    }
}
