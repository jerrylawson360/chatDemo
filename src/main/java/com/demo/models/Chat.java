package com.demo.models;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Public model defining a given Chat. This is used in the REST API.
 */
public class Chat {
    protected Long id;
    protected Set<Long> participantIds;

    public Chat() {
        // Used by objectmapper
    }

    public Chat(final Long id, final Set<Long> participantIds) {
        this.id = id;
        this.setParticipantIds(participantIds);
    }

    public Long getId() {
        return this.id;
    }

    public Set<Long> getParticipantIds() {
        return this.participantIds;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setParticipantIds(final Set<Long> participantIds) {
        // Ensure that objectMapper didn't give us Integer values
        this.participantIds = Set.copyOf(participantIds.stream()
            .map(value -> value.longValue())
            .collect(Collectors.toSet()));
    }
}
