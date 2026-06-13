package com.company.conversation.framework.engine;
import com.company.conversation.protocol.ActivityRequest;
/**
 * Extension point for session management.
 * 
 * SessionManager allows applications to load and manage session state
 * before handler invocation. This is useful for recovering conversation
 * state from persistence, cache, or external systems.
 * 
 * Session managers are optional: if no bean is registered, session loading
 * is skipped.
 * 
 * @since 1.0.0
 */
@FunctionalInterface
public interface SessionManager {
    /**
     * Loads session information for the request.
     * 
     * @param request the request containing session information
     */
    void loadSession(ActivityRequest request);
}

