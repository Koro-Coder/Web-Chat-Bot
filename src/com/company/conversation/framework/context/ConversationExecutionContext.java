package com.company.conversation.framework.context;

import com.company.conversation.protocol.ClientSession;
import com.company.conversation.protocol.ServerSessionReference;

import java.util.Map;

/**
 * Framework-owned execution context that is provided to handlers and pipeline
 * processors during a single request lifecycle.
 *
 * The implementation is created by the framework and passed to pre-processors,
 * the {@code ConversationHandler}, and post-processors. Applications may store
 * arbitrary runtime data in {@link #attributes()}.
 *
 * Future properties may be added to this interface in a backward compatible
 * manner; handler signatures must remain stable.
 *
 * @since 1.0.0
 */
public interface ConversationExecutionContext {

    /**
     * Returns the client-side session information supplied in the protocol.
     *
     * @return the client session
     */
    ClientSession clientSession();

    /**
     * Returns the server-side session reference supplied in the protocol.
     *
     * @return the server session reference
     */
    ServerSessionReference serverSession();

    /**
     * Provides a mutable attributes map which handlers and processors may use to
     * exchange data for the duration of the request.
     *
     * @return a mutable attributes map
     */
    Map<String, Object> attributes();

}


