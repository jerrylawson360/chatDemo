package com.demo.service.impl;

import com.demo.service.ChatRecord;
import com.demo.service.ChatService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServiceMemoryImpl implements ChatService {
    private final Map<Long, ChatRecord> chats = new ConcurrentHashMap<>();    // brute force for now

    @Override
    public ChatRecord getChat(final Long id) {
        return chats.get(id);
    }

    @Override
    public void addChat(final ChatRecord chat) {
        // If we replace a chat, that means someone else already created it, so report an error.
        final ChatRecord previous = chats.put(chat.getId(), chat);
        if (previous != null) {
            chats.put(previous.getId(), previous);
            throw new IllegalStateException("ChatId already exists");
        }
    }
}
