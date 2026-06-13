package com.company.conversation.framework.context;

import com.company.conversation.protocol.ClientSession;
import com.company.conversation.protocol.ServerSessionReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Default, simple implementation of {@link ConversationExecutionContext}.
 *
 * The class is intentionally minimal: it exposes the client and server session
 * references from the protocol and a mutable attributes map for request-scoped
 * data exchange between processors and the handler.
 *
 * @since 1.0.0
 */
public final class DefaultConversationExecutionContext implements ConversationExecutionContext {

    private final ClientSession clientSession;
    private final ServerSessionReference serverSession;
    private final Map<String, Object> attributes = new HashMap<>();

    public DefaultConversationExecutionContext(ClientSession clientSession, ServerSessionReference serverSession) {
        this.clientSession = Objects.requireNonNull(clientSession, "clientSession");
        this.serverSession = Objects.requireNonNull(serverSession, "serverSession");
    }

    @Override
    public ClientSession clientSession() {
        return clientSession;
    }

    @Override
    public ServerSessionReference serverSession() {
        return serverSession;
    }

    @Override
    public Map<String, Object> attributes() {
        return attributes;
    }
}


