package com.company.conversation.protocol;

import java.util.Map;
import java.util.Optional;

/**
 * Represents an event that occurs within a conversation.
 * 
 * ConversationEvents allow for asynchronous or out-of-band communication about conversation
 * state changes, errors, or lifecycle events. Events are distinct from activities in that
 * they represent something that has happened, rather than content to be displayed.
 * 
 * Events support arbitrary payload for extensibility and include tracing information.
 * 
 * @param eventId Unique identifier for this event
 * @param eventType The type of event (e.g., "conversation.started", "session.expired", "error")
 * @param conversationId The ID of the conversation this event pertains to
 * @param timestamp Timestamp (milliseconds since epoch) when the event occurred
 * @param payload Event-specific data (structure depends on event type)
 * @param correlationId Optional correlation ID for tracing
 * @param metadata Optional arbitrary metadata for extensibility
 * 
 * @since 1.0.0
 */
public record ConversationEvent(
        String eventId,
        String eventType,
        String conversationId,
        long timestamp,
        Map<String, Object> payload,
        Optional<String> correlationId,
        Optional<Map<String, Object>> metadata
) {
    
    /**
     * Constructs a ConversationEvent with minimal required fields.
     * 
     * @param eventId Unique event identifier
     * @param eventType The type of event
     * @param conversationId The conversation ID
     * @param timestamp Event timestamp in milliseconds since epoch
     * @param payload Event payload
     */
    public ConversationEvent(
            String eventId,
            String eventType,
            String conversationId,
            long timestamp,
            Map<String, Object> payload
    ) {
        this(eventId, eventType, conversationId, timestamp, payload, Optional.empty(), Optional.empty());
    }
    
    /**
     * Constructs a ConversationEvent with correlation support.
     * 
     * @param eventId Unique event identifier
     * @param eventType The type of event
     * @param conversationId The conversation ID
     * @param timestamp Event timestamp in milliseconds since epoch
     * @param payload Event payload
     * @param correlationId Optional correlation ID
     */
    public ConversationEvent(
            String eventId,
            String eventType,
            String conversationId,
            long timestamp,
            Map<String, Object> payload,
            Optional<String> correlationId
    ) {
        this(eventId, eventType, conversationId, timestamp, payload, correlationId, Optional.empty());
    }
    
    /**
     * Constructs a ConversationEvent with full field specification.
     * Records validation: ensures all required fields are valid.
     */
    public ConversationEvent {
        if (eventId == null || eventId.isBlank()) {
            throw new IllegalArgumentException("ConversationEvent eventId cannot be null or blank");
        }
        if (eventType == null || eventType.isBlank()) {
            throw new IllegalArgumentException("ConversationEvent eventType cannot be null or blank");
        }
        if (conversationId == null || conversationId.isBlank()) {
            throw new IllegalArgumentException("ConversationEvent conversationId cannot be null or blank");
        }
        if (timestamp < 0) {
            throw new IllegalArgumentException("ConversationEvent timestamp must be a valid timestamp");
        }
        if (payload == null) {
            throw new IllegalArgumentException("ConversationEvent payload cannot be null");
        }
    }
}


