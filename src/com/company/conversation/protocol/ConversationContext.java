package com.company.conversation.protocol;

import java.util.Map;
import java.util.Optional;

/**
 * Represents the context of a conversation.
 * 
 * ConversationContext maintains information about a conversation that persists across
 * multiple request-response cycles. It includes session management, protocol versioning,
 * and extensible metadata.
 * 
 * This record is typically included in both requests and responses to maintain conversation state
 * and ensure protocol compatibility.
 * 
 * @param conversationId Unique identifier for this conversation
 * @param clientSession The client-side session state
 * @param serverSessionReference Server-side session reference (opaque to client)
 * @param protocolVersion The version of the conversation protocol (e.g., "1.0.0")
 * @param correlationId Correlation ID for tracing this conversation across systems
 * @param metadata Optional arbitrary metadata for extensibility
 * 
 * @since 1.0.0
 */
public record ConversationContext(
        String conversationId,
        ClientSession clientSession,
        ServerSessionReference serverSessionReference,
        String protocolVersion,
        String correlationId,
        Optional<Map<String, Object>> metadata
) {
    
    /**
     * Constructs a ConversationContext with minimal required fields.
     * 
     * @param conversationId Unique conversation identifier
     * @param clientSession Client session state
     * @param serverSessionReference Server session reference
     * @param protocolVersion Protocol version string
     * @param correlationId Correlation ID for tracing
     */
    public ConversationContext(
            String conversationId,
            ClientSession clientSession,
            ServerSessionReference serverSessionReference,
            String protocolVersion,
            String correlationId
    ) {
        this(conversationId, clientSession, serverSessionReference, protocolVersion, correlationId, Optional.empty());
    }
    
    /**
     * Constructs a ConversationContext with full field specification.
     * Records validation: ensures all required fields are valid.
     */
    public ConversationContext {
        if (conversationId == null || conversationId.isBlank()) {
            throw new IllegalArgumentException("ConversationContext conversationId cannot be null or blank");
        }
        if (clientSession == null) {
            throw new IllegalArgumentException("ConversationContext clientSession cannot be null");
        }
        if (serverSessionReference == null) {
            throw new IllegalArgumentException("ConversationContext serverSessionReference cannot be null");
        }
        if (protocolVersion == null || protocolVersion.isBlank()) {
            throw new IllegalArgumentException("ConversationContext protocolVersion cannot be null or blank");
        }
        if (correlationId == null || correlationId.isBlank()) {
            throw new IllegalArgumentException("ConversationContext correlationId cannot be null or blank");
        }
    }
}


