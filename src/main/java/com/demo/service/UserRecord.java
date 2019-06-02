package com.demo.service;

import com.demo.models.Chat;

import java.util.List;
import java.util.Set;

/**
 * UserRecord object interface
 */
public interface UserRecord {
    Long getId();
    List<ChatRecord> getChats();
    ChatRecord getChat(final Long chatId);
    void addChat(final ChatRecord chat);
    Set<Long> getContacts();
}
