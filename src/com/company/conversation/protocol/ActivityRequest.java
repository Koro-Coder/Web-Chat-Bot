package com.company.conversation.protocol;

import java.util.Map;
import java.util.Optional;

/**
 * Represents a request sent from the client to the server for conversation activity.
 * 
 * ActivityRequest encapsulates a client's message or action within a conversation. It includes
 * the conversation context, the payload representing the user's input, and optional metadata
 * for extensibility.
 * 
 * Each request should include a unique requestId for tracing and idempotency purposes.
 * 
 * @param requestId Unique identifier for this request, used for tracing and idempotency
 * @param conversationContext The conversation context including session information
 * @param payload The request payload containing the user's input or action (opaque to protocol)
 * @param metadata Optional arbitrary metadata for extensibility
 * 
 * @since 1.0.0
 */
public record ActivityRequest(
        String requestId,
        ConversationContext conversationContext,
        Map<String, Object> payload,
        Optional<Map<String, Object>> metadata
) {
    
    /**
     * Constructs an ActivityRequest with minimal required fields.
     * 
     * @param requestId Unique request identifier
     * @param conversationContext The conversation context
     * @param payload The request payload
     */
    public ActivityRequest(
            String requestId,
            ConversationContext conversationContext,
            Map<String, Object> payload
    ) {
        this(requestId, conversationContext, payload, Optional.empty());
    }
    
    /**
     * Constructs an ActivityRequest with full field specification.
     * Records validation: ensures all required fields are valid.
     */
    public ActivityRequest {
        if (requestId == null || requestId.isBlank()) {
            throw new IllegalArgumentException("ActivityRequest requestId cannot be null or blank");
        }
        if (conversationContext == null) {
            throw new IllegalArgumentException("ActivityRequest conversationContext cannot be null");
        }
        if (payload == null) {
            throw new IllegalArgumentException("ActivityRequest payload cannot be null");
        }
    }
}


