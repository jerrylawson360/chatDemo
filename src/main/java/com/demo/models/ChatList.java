package com.demo.models;

import java.util.Collections;
import java.util.List;

/**
 * Public model of list of chats. This is used in the REST API.
 */
public class ChatList {
    private List<Long> ids = Collections.emptyList();

    public ChatList() {

    }

    public ChatList(final List<Long> ids) {
        this.ids = ids;

    }
    public List<Long> getIds() {
        return ids;
    }

    public void setIds(final List<Long> ids) {
        this.ids = ids;
    }

}
