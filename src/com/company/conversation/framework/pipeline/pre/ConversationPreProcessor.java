package com.company.conversation.framework.pipeline.pre;

import com.company.conversation.framework.context.ConversationExecutionContext;
import com.company.conversation.protocol.ActivityRequest;

/**
 * Pre-processing extension point executed before the application {@code ConversationHandler}.
 *
 * Zero or more implementations may be registered. The framework will execute each
 * registered pre-processor in arbitrary order prior to invoking the handler.
 *
 * Implementations must be side-effect free outside the provided context and request
 * lifetime; any cross-request state must be managed by the application.
 *
 * @since 1.0.0
 */
@FunctionalInterface
public interface ConversationPreProcessor {

    /**
     * Process the incoming request and context before handler invocation.
     *
     * @param request the incoming activity request
     * @param context the framework-owned execution context
     */
    void process(ActivityRequest request, ConversationExecutionContext context);
}


