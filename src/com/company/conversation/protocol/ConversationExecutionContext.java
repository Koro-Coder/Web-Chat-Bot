package com.company.conversation.protocol;

import java.util.Map;

/**
 * Framework-managed context supplied to conversation handlers during execution.
 * 
 * This interface provides handlers access to framework-managed state without encoding
 * business logic into the protocol layer. It enables the framework to supply sessions,
 * attributes, feature flags, and other operational context without requiring future
 * changes to the handler signature.
 * 
 * The protocol layer treats this context as opaque — the framework defines what
 * sessions and attributes it provides and manages their lifecycle.
 * 
 * @since 1.0.0
 */
public interface ConversationExecutionContext {
    
    /**
     * Returns the client-side session associated with this conversation.
     * 
     * The session contains client state maintained across requests. The protocol
     * never interprets or validates this state — it is entirely handler-managed.
     * 
     * @return the client session for this conversation
     */
    ClientSession clientSession();
    
    /**
     * Returns the server-side session reference for this conversation.
     * 
     * The reference is an opaque handle managed by the server/framework and is
     * never interpreted by handlers. It can be a token, ID, or any identifier
     * used by the framework for session lookup.
     * 
     * @return the server session reference for this conversation
     */
    ServerSessionReference serverSession();
    
    /**
     * Returns framework-supplied attributes and operational context.
     * 
     * This map contains framework-managed data such as feature flags, helper objects,
     * configuration, or other context the framework wants to make available to
     * handlers without modifying the handler interface.
     * 
     * Attributes are optional and framework-specific. Handlers should defensively
     * check for key existence and handle missing keys gracefully.
     * 
     * @return mutable map of framework attributes; returns empty map if none supplied
     */
    Map<String, Object> attributes();
}

