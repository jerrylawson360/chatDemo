package com.demo.service.impl;

import com.demo.service.ChatRecord;
import com.demo.service.UserRecord;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UserRecordImpl implements UserRecord {
    private Long id;
    private Set<Long> contacts;
    private Map<Long, ChatRecord> chats = new ConcurrentHashMap<>();  // brute-force for now

    public UserRecordImpl(final Long id, final Collection<Number> contacts) {
        this.id = id;

        // Ensure that we're always dealing with Longs, in case ObjectMapper parsed as Integer
        this.contacts = Set.copyOf(contacts.stream()
            .map(value -> value.longValue())
            .collect(Collectors.toSet()));
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public ChatRecord getChat(final Long chatId) {
        return this.chats.get(chatId);
    }

    @Override
    public void addChat(final ChatRecord chat) {
        this.chats.put(chat.getId(), chat);
    }

    @Override
    public Set<Long> getContacts() {
        return this.contacts;
    }

    @Override
    public List<ChatRecord> getChats() {
        return List.copyOf(this.chats.values());
    }
}
