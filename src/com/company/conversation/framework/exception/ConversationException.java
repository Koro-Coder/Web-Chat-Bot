package com.company.conversation.framework.exception;

/**
 * Base exception for conversation framework errors.
 *
 * ConversationException is the parent exception for all checked exceptions thrown
 * by the conversation framework. It provides a foundation for framework-specific
 * error handling and recovery strategies.
 *
 * @since 1.0.0
 */
public class ConversationException extends Exception {

    /**
     * Constructs a ConversationException with the specified detail message.
     *
     * @param message the detail message
     */
    public ConversationException(String message) {
        super(message);
    }

    /**
     * Constructs a ConversationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of this exception
     */
    public ConversationException(String message, Throwable cause) {
        super(message, cause);
    }
}


