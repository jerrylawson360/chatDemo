package com.demo.validation;

import com.demo.exception.BadRequestException;
import com.demo.exception.ForbiddenException;
import com.demo.models.Chat;
import com.demo.models.ErrorResponse;
import com.demo.models.Message;
import com.demo.service.UserRecord;
import com.demo.service.impl.UserRecordImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class ValidatorTest {
    final Validator unit = new Validator(new ObjectMapper());

    @Test
    public void getChatId() throws Exception {
        final Long result = unit.getChatId("1234567890");
        assertEquals(1234567890, result.longValue());
    }

    @Test
    public void getChatIdInvalid() {
        try {
            final Long result = unit.getChatId("abcdefghi");
            fail("Expected BadRequestException");
        } catch (BadRequestException e) {
            assertEquals(1, e.getErrorResponse().getErrors().size());
        }
    }

    @Test
    public void getUserIdTest() throws Exception {
        final Long result = unit.getUserId("1234567890");
        assertEquals(1234567890, result.longValue());
    }

    @Test
    public void getUserIdInvalid() {
        try {
            final Long result = unit.getUserId("abcdefghi");
            fail("Expected BadRequestException");
        } catch (BadRequestException e) {
            assertEquals(1, e.getErrorResponse().getErrors().size());
        }
    }

    @Test
    public void validateNotEmpty() {
        final Optional<ErrorResponse.Error> result = unit.validateNotEmpty("abcdef", "value");
        assertTrue(result.isEmpty());
    }

    @Test
    public void validateNotEmptyWithNull() {
        final Optional<ErrorResponse.Error> result = unit.validateNotEmpty("abcdef", null);
        assertTrue(result.isPresent());
        assertEquals("abcdef must be non-empty", result.get().getMessage());
    }

    @Test
    public void validateNotEmptyWithEmpty() {
        final Optional<ErrorResponse.Error> result = unit.validateNotEmpty("abcdef", "");
        assertTrue(result.isPresent());
        assertEquals("abcdef must be non-empty", result.get().getMessage());
    }

    @Test
    public void validateNonZero() {
        final Optional<ErrorResponse.Error> result = unit.validateNonZero("abcdef", new Integer(12345));
        assertTrue(result.isEmpty());
    }

    @Test
    public void validateNonZeroWithNull() {
        final Optional<ErrorResponse.Error> result = unit.validateNonZero("abcdef", null);
        assertTrue(result.isPresent());
        assertEquals("abcdef must be non-zero", result.get().getMessage());
    }

    @Test
    public void validateNonZeroWithZero() {
        final Optional<ErrorResponse.Error> result = unit.validateNonZero("abcdef", 0L);
        assertTrue(result.isPresent());
        assertEquals("abcdef must be non-zero", result.get().getMessage());
    }

    @Test
    public void validateMessage() throws Exception {
        final Message message = new Message();
        message.setId("123456");
        message.setDestinationUserId(111222L);
        message.setSourceUserId(333444L);
        message.setMessage("this is a test message");
        message.setTimestamp(Instant.now().getEpochSecond());

        unit.validateMessage(message);

    }

    @Test
    public void validateMessageMissingId() throws Exception {
        final Message message = new Message();
//        message.setId("123456");
        message.setDestinationUserId(111222L);
        message.setSourceUserId(333444L);
        message.setMessage("this is a test message");
        message.setTimestamp(Instant.now().getEpochSecond());

        try {
            unit.validateMessage(message);
            fail("Expected BadRequestException");
        } catch (BadRequestException e) {
            assertEquals(1, e.getErrorResponse().getErrors().size());
            assertEquals("id must be non-empty", e.getErrorResponse().getErrors().get(0).getMessage());

        }
    }

    @Test
    public void validateMessageMissingDestUserId() throws Exception {
        final Message message = new Message();
        message.setId("123456");
//        message.setDestinationUserId(111222L);
        message.setSourceUserId(333444L);
        message.setMessage("this is a test message");
        message.setTimestamp(Instant.now().getEpochSecond());

        try {
            unit.validateMessage(message);
            fail("Expected BadRequestException");
        } catch (BadRequestException e) {
            assertEquals(1, e.getErrorResponse().getErrors().size());
            assertEquals("destinationUserId must be non-zero", e.getErrorResponse().getErrors().get(0).getMessage());

        }
    }

    @Test
    public void validateMessageMissingSourceUserId() throws Exception {
        final Message message = new Message();
        message.setId("123456");
        message.setDestinationUserId(111222L);
//        message.setSourceUserId(333444L);
        message.setMessage("this is a test message");
        message.setTimestamp(Instant.now().getEpochSecond());

        try {
            unit.validateMessage(message);
            fail("Expected BadRequestException");
        } catch (BadRequestException e) {
            assertEquals(1, e.getErrorResponse().getErrors().size());
            assertEquals("sourceUserId must be non-zero", e.getErrorResponse().getErrors().get(0).getMessage());

        }
    }

    @Test
    public void validateMessageMissingMessage() throws Exception {
        final Message message = new Message();
        message.setId("123456");
        message.setDestinationUserId(111222L);
        message.setSourceUserId(333444L);
//        message.setMessage("this is a test message");
        message.setTimestamp(Instant.now().getEpochSecond());

        try {
            unit.validateMessage(message);
            fail("Expected BadRequestException");
        } catch (BadRequestException e) {
            assertEquals(1, e.getErrorResponse().getErrors().size());
            assertEquals("message must be non-empty", e.getErrorResponse().getErrors().get(0).getMessage());

        }
    }

    @Test
    public void validateMessageMissingTimestamp() throws Exception {
        final Message message = new Message();
        message.setId("123456");
        message.setDestinationUserId(111222L);
        message.setSourceUserId(333444L);
        message.setMessage("this is a test message");
//        message.setTimestamp(Instant.now().getEpochSecond());

        try {
            unit.validateMessage(message);
            fail("Expected BadRequestException");
        } catch (BadRequestException e) {
            assertEquals(1, e.getErrorResponse().getErrors().size());
            assertEquals("timestamp must be non-zero", e.getErrorResponse().getErrors().get(0).getMessage());

        }
    }

    @Test
    public void validateChat() throws Exception {
        final Chat chat = new Chat();
        chat.setId(123456L);
        chat.setParticipantIds(Set.of(111222L, 333444L));

        unit.validateChat(chat);
    }

    @Test
    public void validateChatWithMissingId() throws Exception {
        final Chat chat = new Chat();
//        chat.setId(123456L);
        chat.setParticipantIds(Set.of(111222L, 333444L));

        try {
            unit.validateChat(chat);
            fail("Expected BadRequestException");
        } catch (BadRequestException e) {
            assertEquals(1, e.getErrorResponse().getErrors().size());
            assertEquals("id must be non-zero", e.getErrorResponse().getErrors().get(0).getMessage());

        }
    }

    @Test
    public void validateChatWithMissingParticipantIds() throws Exception {
        final Chat chat = new Chat();
        chat.setId(123456L);
//        chat.setParticipantIds(Set.of(111222L, 333444L));

        try {
            unit.validateChat(chat);
            fail("Expected BadRequestException");
        } catch (BadRequestException e) {
            assertEquals(1, e.getErrorResponse().getErrors().size());
            assertEquals("participantIds" + " must be exactly 2 unique values", e.getErrorResponse().getErrors().get(0).getMessage());

        }
    }

    @Test
    public void validateChatWithTooFewParticipantIds() throws Exception {
        final Chat chat = new Chat();
        chat.setId(123456L);
        chat.setParticipantIds(Set.of(111222L));

        try {
            unit.validateChat(chat);
            fail("Expected BadRequestException");
        } catch (BadRequestException e) {
            assertEquals(1, e.getErrorResponse().getErrors().size());
            assertEquals("participantIds" + " must be exactly 2 unique values", e.getErrorResponse().getErrors().get(0).getMessage());

        }
    }

    @Test
    public void validateChatWithTooManyParticipantIds() throws Exception {
        final Chat chat = new Chat();
        chat.setId(123456L);
        chat.setParticipantIds(Set.of(111222L, 333444L, 555666L));

        try {
            unit.validateChat(chat);
            fail("Expected BadRequestException");
        } catch (BadRequestException e) {
            assertEquals(1, e.getErrorResponse().getErrors().size());
            assertEquals("participantIds" + " must be exactly 2 unique values", e.getErrorResponse().getErrors().get(0).getMessage());

        }
    }

    @Test
    public void validateContactsMap() throws Exception {
        final UserRecord userA = new UserRecordImpl(12345L, Set.of(111L, 222L, 333L));
        final UserRecord userB = new UserRecordImpl(111L, Set.of(444L, 12345L, 333L));

        final Map<Long, UserRecord> users = Map.of(userA.getId(), userA, userB.getId(), userB);
        unit.validateContacts(users);
    }

    @Test
    public void validateContactsMapANotInB() throws Exception {
        final UserRecord userA = new UserRecordImpl(12345L, Set.of(111L, 222L, 333L));
        final UserRecord userB = new UserRecordImpl(111L, Set.of(444L, 333L));

        try {
            final Map<Long, UserRecord> users = Map.of(userA.getId(), userA, userB.getId(), userB);
            unit.validateContacts(users);
            fail("Expected ForbiddenException");
        } catch (ForbiddenException e) {

        }
    }

    @Test
    public void validateContactsMapBNotInA() throws Exception {
        final UserRecord userA = new UserRecordImpl(12345L, Set.of(444L, 222L, 333L));
        final UserRecord userB = new UserRecordImpl(111L, Set.of(444L, 333L));

        try {
            final Map<Long, UserRecord> users = Map.of(userA.getId(), userA, userB.getId(), userB);
            unit.validateContacts(users);
            fail("Expected ForbiddenException");
        } catch (ForbiddenException e) {

        }
    }

    @Test
    public void validateContacts() throws Exception {
        final UserRecord userA = new UserRecordImpl(12345L, Set.of(111L, 222L, 333L));
        final UserRecord userB = new UserRecordImpl(111L, Set.of(444L, 12345L, 333L));

        unit.validateContacts(userA, userB);
    }

    @Test
    public void validateContactsANotInB() throws Exception {
        final UserRecord userA = new UserRecordImpl(12345L, Set.of(111L, 222L, 333L));
        final UserRecord userB = new UserRecordImpl(111L, Set.of(444L, 333L));

        try {
            unit.validateContacts(userA, userB);
            fail("Expected ForbiddenException");
        } catch (ForbiddenException e) {

        }
    }

    @Test
    public void validateContactsBNotInA() throws Exception {
        final UserRecord userA = new UserRecordImpl(12345L, Set.of(444L, 222L, 333L));
        final UserRecord userB = new UserRecordImpl(111L, Set.of(444L, 333L));

        try {
            unit.validateContacts(userA, userB);
            fail("Expected ForbiddenException");
        } catch (ForbiddenException e) {

        }
    }

    @Test
    public void testParseMessage() throws Exception {
        final String s = "{\"id\":\"00d84d16-c2e2-4a5b-b99c-eaa485347b7e\",\"timestamp\":1559433608896,\"message\":\"See the cat glaring at the scared mouse.\",\"sourceUserId\":11212,\"destinationUserId\":109530}";

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(s.getBytes());
        final Message message = unit.parseBody(inputStream, Message.class);
        assertEquals("00d84d16-c2e2-4a5b-b99c-eaa485347b7e", message.getId());
        assertEquals(1559433608896L, message.getTimestamp().longValue());
        assertEquals("See the cat glaring at the scared mouse.", message.getMessage());
        assertEquals(11212L, message.getSourceUserId().longValue());
        assertEquals(109530L, message.getDestinationUserId().longValue());

    }

    @Test
    public void testParseMessageParseFails() throws Exception {
        final String s = "{\"id\":\"00d84d16-c2e2-4a5b-b99c-eaa485347b7e\",\"timestamp\":\"abcdef\",\"message\":\"See the cat glaring at the scared mouse.\",\"sourceUserId\":11212,\"destinationUserId\":109530}";

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(s.getBytes());
        try {
            final Message message = unit.parseBody(inputStream, Message.class);
            fail("Expected BadRequestException");
        } catch (BadRequestException e) {
            assertEquals(1, e.getErrorResponse().getErrors().size());
        }
    }
}
