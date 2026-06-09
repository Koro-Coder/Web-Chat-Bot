package com.company.conversation.protocol;

import java.util.Optional;

/**
 * Represents a server-side session reference.
 * 
 * The ServerSessionReference is completely opaque to the SDK and client. The SDK does not
 * interpret, validate, or modify this reference. Its sole purpose is to allow the server
 * to track and manage conversation sessions on the server side.
 * 
 * The reference may be a session ID, token, handle, or any other identifier that the
 * server implementation deems appropriate. It should be treated as a black box by client code.
 * 
 * @param reference The server session identifier or token (opaque to the protocol)
 * @param expiresAt Optional timestamp (milliseconds since epoch) indicating when the session expires
 * 
 * @since 1.0.0
 */
public record ServerSessionReference(
        String reference,
        Optional<Long> expiresAt
) {
    
    /**
     * Constructs a ServerSessionReference with only the reference value.
     * 
     * @param reference The server session identifier
     */
    public ServerSessionReference(String reference) {
        this(reference, Optional.empty());
    }
    
    /**
     * Constructs a ServerSessionReference with reference and expiration.
     * Records validation: ensures the reference is valid.
     */
    public ServerSessionReference {
        if (reference == null || reference.isBlank()) {
            throw new IllegalArgumentException("ServerSessionReference cannot be null or blank");
        }
    }
}

