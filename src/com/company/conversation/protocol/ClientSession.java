package com.company.conversation.protocol;

import java.util.Map;
import java.util.Optional;

/**
 * Represents client-side session state.
 * 
 * The ClientSession contains state that the SDK maintains on behalf of the client.
 * While the SDK exchanges this data with the server, the SDK does not interpret the
 * contents of the state map. The structure and meaning of the state are entirely
 * determined by the client application and bot implementation.
 * 
 * This design allows for complete flexibility in how client state is managed without
 * requiring protocol changes for new state formats or structure.
 * 
 * @param sessionId Unique client session identifier
 * @param state Arbitrary client state stored as key-value pairs. Completely opaque to the SDK.
 * @param createdAt Timestamp (milliseconds since epoch) when the session was created
 * @param metadata Optional arbitrary metadata for extensibility
 * 
 * @since 1.0.0
 */
public record ClientSession(
        String sessionId,
        Map<String, Object> state,
        long createdAt,
        Optional<Map<String, Object>> metadata
) {
    
    /**
     * Constructs a ClientSession with minimal required fields.
     * 
     * @param sessionId Unique client session identifier
     * @param state Client state map
     * @param createdAt Creation timestamp in milliseconds since epoch
     */
    public ClientSession(String sessionId, Map<String, Object> state, long createdAt) {
        this(sessionId, state, createdAt, Optional.empty());
    }
    
    /**
     * Constructs a ClientSession with full field specification.
     * Records validation: ensures required fields are valid.
     */
    public ClientSession {
        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("ClientSession sessionId cannot be null or blank");
        }
        if (state == null) {
            throw new IllegalArgumentException("ClientSession state cannot be null");
        }
        if (createdAt < 0) {
            throw new IllegalArgumentException("ClientSession createdAt must be a valid timestamp");
        }
    }
}


