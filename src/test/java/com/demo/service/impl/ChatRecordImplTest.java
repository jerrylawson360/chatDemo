package com.demo.service.impl;

import com.demo.models.Message;
import com.demo.service.ChatRecord;
import org.junit.Test;
import util.TestUtil;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ChatRecordImplTest {
    @Test
    public void addMessageTest() {
        final ChatRecord chat = new ChatRecordImpl(TestUtil.randomLong(),
            Set.of(TestUtil.randomLong(), TestUtil.randomLong(), TestUtil.randomLong(), TestUtil.randomLong()));

        final Message message = new Message();
        message.setId(TestUtil.randomString());
        message.setDestinationUserId(TestUtil.randomLong());
        message.setSourceUserId(TestUtil.randomLong());
        message.setMessage(TestUtil.randomString());
        message.setTimestamp(Instant.now().getEpochSecond());

        chat.addMessage(message);

        final List<Message> messages = chat.getMessages();
        assertEquals(1, messages.size());
    }

    @Test
    public void addMessageDuplicateTimeTest() {
        final ChatRecord chat = new ChatRecordImpl(TestUtil.randomLong(),
            Set.of(TestUtil.randomLong(), TestUtil.randomLong(), TestUtil.randomLong(), TestUtil.randomLong()));

        final Message message = new Message();
        message.setId(TestUtil.randomString());
        message.setDestinationUserId(TestUtil.randomLong());
        message.setSourceUserId(TestUtil.randomLong());
        message.setMessage(TestUtil.randomString());
        message.setTimestamp(Instant.now().getEpochSecond());

        chat.addMessage(message);

        final Message message2 = new Message();
        message2.setId(TestUtil.randomString());
        message2.setDestinationUserId(TestUtil.randomLong());
        message2.setSourceUserId(TestUtil.randomLong());
        message2.setMessage(TestUtil.randomString());
        message2.setTimestamp(message.getTimestamp());
        chat.addMessage(message2);

        final List<Message> messages = chat.getMessages();
        assertEquals(2, messages.size());

        final List<String> expectedIds = List.of(message.getId(), message2.getId());
        assertEquals(expectedIds, messages.stream().map(Message::getId).collect(Collectors.toList()));
    }

    @Test
    public void addMessageRandomTimeTest() {
        final ChatRecord chat = new ChatRecordImpl(TestUtil.randomLong(),
            Set.of(TestUtil.randomLong(), TestUtil.randomLong(), TestUtil.randomLong(), TestUtil.randomLong()));

        final Message message = new Message();
        message.setId(TestUtil.randomString());
        message.setTimestamp(5000L);

        chat.addMessage(message);

        final Message message2 = new Message();
        message2.setId(TestUtil.randomString());
        message2.setTimestamp(4000L);
        chat.addMessage(message2);

        final Message message3 = new Message();
        message3.setId(TestUtil.randomString());
        message3.setTimestamp(2000L);
        chat.addMessage(message3);

        final List<Message> messages = chat.getMessages();
        assertEquals(3, messages.size());

        final List<String> expectedIds = List.of(message3.getId(), message2.getId(), message.getId());
        assertEquals(expectedIds, messages.stream().map(Message::getId).collect(Collectors.toList()));
    }
}
