package com.demo.models;

/**
 * Public model of Message. This is used in the REST API.
 */
public class Message {
    protected String id;
    protected Long timestamp;
    protected String message;
    protected Long sourceUserId;
    protected Long destinationUserId;

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public Long getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(final Long sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public Long getDestinationUserId() {
        return destinationUserId;
    }

    public void setDestinationUserId(final Long destinationUserId) {
        this.destinationUserId = destinationUserId;
    }

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }
}
