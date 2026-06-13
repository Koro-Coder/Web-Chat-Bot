package com.company.conversation.protocol;

import java.util.Map;
import java.util.Optional;

/**
 * Represents a single unit of activity in a conversation.
 * 
 * An activity encapsulates content that should be displayed to or processed by the client.
 * Activities are immutable and contain type information, payload, optional actions, and
 * extensible metadata. The payload structure depends on the activity type and is opaque
 * to the protocol layer.
 * 
 * Activities support optional correlation tracking and sequencing through correlation and
 * activity IDs, which are useful for tracing and debugging purposes.
 * 
 * @param id Unique identifier for this activity within the conversation
 * @param type The type of activity (TEXT, FORM, BUTTON_GROUP, etc.)
 * @param payload The activity content. Structure varies by type. Opaque to the protocol.
 * @param action Optional action associated with this activity (e.g., for buttons)
 * @param metadata Optional arbitrary metadata for extensibility
 * @param correlationId Optional correlation ID for tracing purposes
 * 
 * @since 1.0.0
 */
public record Activity(
        String id,
        ActivityType type,
        Map<String, Object> payload,
        Optional<Action> action,
        Optional<Map<String, Object>> metadata,
        Optional<String> correlationId
) {
    
    /**
     * Constructs an Activity with minimal required fields.
     * 
     * @param id Unique activity identifier
     * @param type The activity type
     * @param payload The activity content
     */
    public Activity(String id, ActivityType type, Map<String, Object> payload) {
        this(id, type, payload, Optional.empty(), Optional.empty(), Optional.empty());
    }
    
    /**
     * Constructs an Activity with id, type, payload, and action.
     * 
     * @param id Unique activity identifier
     * @param type The activity type
     * @param payload The activity content
     * @param action Optional associated action
     */
    public Activity(String id, ActivityType type, Map<String, Object> payload, Optional<Action> action) {
        this(id, type, payload, action, Optional.empty(), Optional.empty());
    }
    
    /**
     * Constructs an Activity with full field specification.
     * Records validation: ensures required fields are valid.
     */
    public Activity {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Activity id cannot be null or blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("Activity type cannot be null");
        }
        if (payload == null) {
            throw new IllegalArgumentException("Activity payload cannot be null");
        }
    }
}


