package com.demo.service;

public interface ChatService {
    ChatRecord getChat(final Long id);
    void addChat(final ChatRecord chat);
}
