package com.company.conversation.framework.handler;

import com.company.conversation.protocol.ActivityRequest;
import com.company.conversation.protocol.ActivityResponse;
import com.company.conversation.framework.context.ConversationExecutionContext;

import java.util.Objects;

/**
 * Primary application extension point for conversation business logic.
 *
 * Implementations of this interface are provided by application code and executed
 * by the framework's orchestration runtime. The framework does not impose any
 * business rules or processing semantics; it simply invokes the handler with
 * the request and a runtime execution context.
 *
 * The handler receives a {@link ConversationExecutionContext} that may be used to
 * store or retrieve arbitrary runtime attributes for the current request. The
 * framework owns the context implementation.
 *
 * @since 1.0.0
 */
@FunctionalInterface
public interface ConversationHandler {

    /**
     * Handle the given {@link ActivityRequest} and return an {@link ActivityResponse}.
     *
     * Implementations should avoid throwing unchecked exceptions; failures should
     * be represented in the returned {@code ActivityResponse} where appropriate.
     *
     * @param request the incoming activity request (never null)
     * @param context the execution context owned by the framework (never null)
     * @return the activity response to send back to the client (never null)
     */
    ActivityResponse handle(ActivityRequest request, ConversationExecutionContext context);

}

