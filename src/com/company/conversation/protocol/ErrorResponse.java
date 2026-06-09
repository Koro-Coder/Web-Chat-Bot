package com.company.conversation.protocol;

import java.util.Map;
import java.util.Optional;

/**
 * Represents an error response from the server.
 * 
 * ErrorResponse is used when a request cannot be processed successfully. It provides
 * error details including a status code, error code, message, and optional additional
 * information such as stack traces (for debugging) or field-level validation errors.
 * 
 * This immutable record ensures consistent error reporting across the protocol.
 * 
 * @param statusCode HTTP-style status code (e.g., 400, 500)
 * @param errorCode Application-specific error code for programmatic error handling
 * @param message Human-readable error message
 * @param details Optional additional error details or field-level validation errors
 * @param correlationId Optional correlation ID for tracing this error
 * @param metadata Optional arbitrary metadata for extensibility
 * 
 * @since 1.0.0
 */
public record ErrorResponse(
        int statusCode,
        String errorCode,
        String message,
        Optional<Map<String, Object>> details,
        Optional<String> correlationId,
        Optional<Map<String, Object>> metadata
) {
    
    /**
     * Constructs an ErrorResponse with minimal required fields.
     * 
     * @param statusCode HTTP-style status code
     * @param errorCode Application error code
     * @param message Error message
     */
    public ErrorResponse(int statusCode, String errorCode, String message) {
        this(statusCode, errorCode, message, Optional.empty(), Optional.empty(), Optional.empty());
    }
    
    /**
     * Constructs an ErrorResponse with status, error code, message, and details.
     * 
     * @param statusCode HTTP-style status code
     * @param errorCode Application error code
     * @param message Error message
     * @param details Additional error details
     */
    public ErrorResponse(int statusCode, String errorCode, String message, Optional<Map<String, Object>> details) {
        this(statusCode, errorCode, message, details, Optional.empty(), Optional.empty());
    }
    
    /**
     * Constructs an ErrorResponse with full field specification.
     * Records validation: ensures required fields are valid.
     */
    public ErrorResponse {
        if (statusCode < 100 || statusCode > 599) {
            throw new IllegalArgumentException("ErrorResponse statusCode must be a valid HTTP status code");
        }
        if (errorCode == null || errorCode.isBlank()) {
            throw new IllegalArgumentException("ErrorResponse errorCode cannot be null or blank");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("ErrorResponse message cannot be null or blank");
        }
    }
}

