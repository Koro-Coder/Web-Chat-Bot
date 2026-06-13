package com.company.conversation.framework.session;

import com.company.conversation.protocol.ClientSession;
import com.company.conversation.protocol.ServerSessionReference;

/**
 * Abstraction for session storage used by the framework. The framework does not
 * assume any particular persistence technology; implementations may use Redis,
 * databases, browser storage, or any other storage mechanism.
 *
 * This interface is intentionally minimal and asynchronous or blocking
 * implementations are permitted depending on application needs.
 *
 * No implementation is provided by the framework; this is a pure abstraction.
 *
 * @since 1.0.0
 */
public interface ConversationSessionStore {

    /**
     * Load the client session for the provided identifier/context.
     *
     * @param conversationId the conversation identifier
     * @return the loaded ClientSession or null if none exists
     */
    ClientSession loadClientSession(String conversationId);

    /**
     * Load the server session reference for the provided identifier/context.
     *
     * @param conversationId the conversation identifier
     * @return the loaded ServerSessionReference or null if none exists
     */
    ServerSessionReference loadServerSession(String conversationId);

    /**
     * Persist session state. The exact semantics are implementation-defined.
     *
     * @param conversationId the conversation identifier
     * @param clientSession the client session to persist
     * @param serverSession the server session reference to persist
     */
    void save(String conversationId, ClientSession clientSession, ServerSessionReference serverSession);

}


