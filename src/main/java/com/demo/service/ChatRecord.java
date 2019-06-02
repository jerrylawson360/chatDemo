package com.demo.service;

import com.demo.models.Message;

import java.util.List;
import java.util.Set;

public interface ChatRecord {
    Long getId();
    Set<Long> getParticipantIds();
    void addMessage(final Message message);
    List<Message> getMessages();
}
