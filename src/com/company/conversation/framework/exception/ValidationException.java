package com.company.conversation.framework.exception;

/**
 * Exception thrown when conversation request validation fails.
 * 
 * ValidationException indicates that an ActivityRequest did not pass framework
 * or extension point validation before being delivered to the handler.
 * 
 * @since 1.0.0
 */
public class ValidationException extends ConversationException {
    
    /**
     * Constructs a ValidationException with the specified detail message.
     * 
     * @param message the detail message
     */
    public ValidationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a ValidationException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the underlying cause of the exception
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}


