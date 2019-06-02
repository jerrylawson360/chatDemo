package com.demo.validation;

import com.demo.exception.BadRequestException;
import com.demo.exception.ForbiddenException;
import com.demo.models.Chat;
import com.demo.models.ErrorResponse;
import com.demo.models.Message;
import com.demo.service.UserRecord;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public class Validator {
    final ObjectMapper objectMapper;

    public Validator(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Long getChatId(final String chatId) throws BadRequestException {
        // Try to parse a number.
        try {
            return Long.parseLong(chatId);
        } catch (Exception e) {
            throw new BadRequestException(
                new ErrorResponse.Error(String.format("Invalid chatId (%s)", chatId)));
        }
    }

    public Long getUserId(final String userId) throws BadRequestException {
        // Try to parse a number.
        try {
            return Long.parseLong(userId);
        } catch (Exception e) {
            throw new BadRequestException(
                new ErrorResponse.Error(String.format("Invalid userId (%s)", userId)));
        }
    }

    public Optional<ErrorResponse.Error> validateNotEmpty(final String name, final String value) {
        if (value == null || value.isEmpty()) {
            return Optional.of(new ErrorResponse.Error(name + " must be non-empty"));
        } else {
            return Optional.empty();
        }
    }

    public <T extends Number> Optional<ErrorResponse.Error> validateNonZero(final String name, final T value) {
        if (value == null || value.longValue() == 0) {
            return Optional.of(new ErrorResponse.Error(name + " must be non-zero"));
        } else {
            return Optional.empty();
        }
    }

    public <T> T parseBody(final InputStream stream, final Class<T> klass) throws BadRequestException {
        try {
            return objectMapper.readValue(stream, klass);

        } catch (IOException e) {
            final String message;
            if (e.getMessage().contains("No content")) {
                message = "No content";
            } else {
                message = e.getMessage();
            }
            throw new BadRequestException(
                new ErrorResponse.Error("Could not parse json: " + message));
        }
    }

    public Message validateMessage(final Message message) throws BadRequestException {
        final ErrorResponse errors = new ErrorResponse();

        validateNotEmpty("id", message.getId())
            .ifPresent(errors::addError);

        validateNotEmpty("message", message.getMessage())
            .ifPresent(errors::addError);

        validateNonZero("timestamp", message.getTimestamp())
            .ifPresent(errors::addError);

        validateNonZero("sourceUserId", message.getSourceUserId())
            .ifPresent(errors::addError);

        validateNonZero("destinationUserId", message.getDestinationUserId())
            .ifPresent(errors::addError);

        // throw exception if one or more errors detected above.
        if (!errors.getErrors().isEmpty()) {
            throw new BadRequestException(errors);
        }

        return message;
    }

    public Chat validateChat(final Chat chat) throws BadRequestException {
        final ErrorResponse errors = new ErrorResponse();

        validateNonZero("id", chat.getId())
            .ifPresent(errors::addError);

        if (chat.getParticipantIds() == null || chat.getParticipantIds().size() != 2) {
            errors.addError(new ErrorResponse.Error("participantIds" + " must be exactly 2 unique values"));
        }

        // throw exception if one or more errors detected above.
        if (!errors.getErrors().isEmpty()) {
            throw new BadRequestException(errors);
        }

        return chat;
    }

    public void validateContacts(final UserRecord userA, final UserRecord userB) throws ForbiddenException {
        if (userB.getId() != userA.getId()) {
            if (!userA.getContacts().contains(userB.getId())) {
                // A does not have B as a contact
                throw new ForbiddenException();
            }

            if (!userB.getContacts().contains(userA.getId())) {
                // B does not have A as a contact
                throw new ForbiddenException();
            }
        }
    }

    public void validateContacts(final Map<Long, UserRecord> users) throws ForbiddenException {

        // TODO: optimize this so we're not processing O(n^2)
        for (final Long userAId: users.keySet()) {
            final UserRecord userA = users.get(userAId);
            for (final UserRecord userB: users.values()) {
                validateContacts(userA, userB);
            }
        }

    }
}
