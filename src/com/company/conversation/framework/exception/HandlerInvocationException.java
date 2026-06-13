package com.company.conversation.framework.exception;

/**
 * Exception thrown when a ConversationHandler invocation fails.
 * 
 * HandlerInvocationException wraps errors that occur during handler processing,
 * providing diagnostic information about what went wrong during conversation handling.
 * 
 * @since 1.0.0
 */
public class HandlerInvocationException extends ConversationException {
    
    /**
     * Constructs a HandlerInvocationException with the specified detail message.
     * 
     * @param message the detail message
     */
    public HandlerInvocationException(String message) {
        super(message);
    }
    
    /**
     * Constructs a HandlerInvocationException with the specified detail message and cause.
     * 
     * @param message the detail message
     * @param cause the underlying cause of the exception
     */
    public HandlerInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}


