package com.company.conversation.protocol;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a response sent from the server to the client containing conversation activities.
 * 
 * ActivityResponse encapsulates one or more activities that should be displayed or processed
 * by the client. A response can contain multiple activities, allowing the server to return
 * complex multi-part responses in a single roundtrip.
 * 
 * The response maintains conversation context and can optionally include an error if the
 * request was not fully successful.
 * 
 * @param responseId Unique identifier for this response
 * @param requestId The ID of the request this response corresponds to
 * @param conversationContext The updated conversation context
 * @param activities The list of activities to display/process. May be empty.
 * @param error Optional error information if the request was not fully successful
 * @param metadata Optional arbitrary metadata for extensibility
 * 
 * @since 1.0.0
 */
public record ActivityResponse(
        String responseId,
        String requestId,
        ConversationContext conversationContext,
        List<Activity> activities,
        Optional<ErrorResponse> error,
        Optional<Map<String, Object>> metadata
) {
    
    /**
     * Constructs an ActivityResponse with minimal required fields.
     * 
     * @param responseId Unique response identifier
     * @param requestId The originating request ID
     * @param conversationContext The conversation context
     * @param activities The list of activities
     */
    public ActivityResponse(
            String responseId,
            String requestId,
            ConversationContext conversationContext,
            List<Activity> activities
    ) {
        this(responseId, requestId, conversationContext, activities, Optional.empty(), Optional.empty());
    }
    
    /**
     * Constructs an ActivityResponse with activities and error information.
     * 
     * @param responseId Unique response identifier
     * @param requestId The originating request ID
     * @param conversationContext The conversation context
     * @param activities The list of activities
     * @param error Optional error information
     */
    public ActivityResponse(
            String responseId,
            String requestId,
            ConversationContext conversationContext,
            List<Activity> activities,
            Optional<ErrorResponse> error
    ) {
        this(responseId, requestId, conversationContext, activities, error, Optional.empty());
    }
    
    /**
     * Constructs an ActivityResponse with full field specification.
     * Records validation: ensures all required fields are valid.
     */
    public ActivityResponse {
        if (responseId == null || responseId.isBlank()) {
            throw new IllegalArgumentException("ActivityResponse responseId cannot be null or blank");
        }
        if (requestId == null || requestId.isBlank()) {
            throw new IllegalArgumentException("ActivityResponse requestId cannot be null or blank");
        }
        if (conversationContext == null) {
            throw new IllegalArgumentException("ActivityResponse conversationContext cannot be null");
        }
        if (activities == null) {
            throw new IllegalArgumentException("ActivityResponse activities cannot be null");
        }
    }
}


