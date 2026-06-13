package com.company.conversation.framework.pipeline.post;

import com.company.conversation.framework.context.ConversationExecutionContext;
import com.company.conversation.protocol.ActivityRequest;
import com.company.conversation.protocol.ActivityResponse;

/**
 * Post-processing extension point executed after the application {@code ConversationHandler}
 * has produced a response.
 *
 * Zero or more implementations may be registered. The framework will execute each
 * registered post-processor in arbitrary order after the handler returns.
 *
 * Typical uses include analytics, audit logging, and event publishing. Implementations
 * should be resilient and avoid throwing unchecked exceptions.
 *
 * @since 1.0.0
 */
@FunctionalInterface
public interface ConversationPostProcessor {

    /**
     * Process the request, response and context after the handler produced the response.
     *
     * @param request the original activity request
     * @param response the activity response produced by the handler
     * @param context the framework-owned execution context
     */
    void process(ActivityRequest request, ActivityResponse response, ConversationExecutionContext context);
}


