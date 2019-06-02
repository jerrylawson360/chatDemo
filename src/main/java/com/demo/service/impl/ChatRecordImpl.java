package com.demo.service.impl;

import com.demo.models.Chat;
import com.demo.models.Message;
import com.demo.service.ChatRecord;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

public class ChatRecordImpl extends Chat implements ChatRecord {
    private static AtomicLong uniqueId = new AtomicLong(0);

    // Brute-force: keep a synchronized TreeMap, using
    // a special Key that is a combination of timestamp and a unique monotonically incrementing value,
    // to prevent timestamp collisions.
    // NOTE: this does not scale across multiple instances of this application.
    private Map<MessageKey, Message> messages = Collections.synchronizedSortedMap(new TreeMap<>());

    public ChatRecordImpl(final Long id, final Set<Long> partcipantIds) {
       super(id, partcipantIds);
    }

    @Override
    public void addMessage(final Message message) {
        messages.put(new MessageKey(message.getTimestamp()), message);
    }

    @Override
    public List<Message> getMessages() {
        return List.copyOf(messages.values());
    }

    private class MessageKey implements Comparable<MessageKey> {
        private Long timestamp;
        private Long unique;

        MessageKey(final Long timestamp) {
            this.timestamp = timestamp;
            this.unique = uniqueId.incrementAndGet();
        }

        @Override
        public int hashCode() {
            int hash = 17;
            hash = hash * 31 + timestamp.hashCode();
            hash = hash * 31 + unique.hashCode();
            return hash;
        }

        @Override
        public boolean equals(final Object object) {
            return object instanceof MessageKey &&
                ((MessageKey) object).timestamp == this.timestamp &&
                ((MessageKey) object).unique == this.unique;
        }

        @Override
        public int compareTo(final MessageKey other) {
            int result = this.timestamp.compareTo(other.timestamp);
            if (result != 0) {
                return result;
            }
            return this.unique.compareTo(other.unique);
        }
    }
}
