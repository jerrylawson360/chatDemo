package com.demo.models;

import java.util.Collections;
import java.util.List;

/**
 * Public model of list of messages. This is used in the REST API.
 */
public class MessageList {
    private List<Message> messages = Collections.emptyList();

    public MessageList() {

    }

    public MessageList(final List<Message> messages) {
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(final List<Message> messages) {
        this.messages = messages;
    }

}
