package com.company.conversation.framework.orchestrator;

import com.company.conversation.framework.context.ConversationExecutionContext;
import com.company.conversation.framework.context.DefaultConversationExecutionContext;
import com.company.conversation.framework.handler.ConversationHandler;
import com.company.conversation.framework.pipeline.post.ConversationPostProcessor;
import com.company.conversation.framework.pipeline.pre.ConversationPreProcessor;
import com.company.conversation.protocol.ActivityRequest;
import com.company.conversation.protocol.ActivityResponse;

import java.util.List;
import java.util.Objects;

/**
 * Orchestrates the execution pipeline for a single conversation request.
 *
 * Responsibilities:
 * - Create the {@link ConversationExecutionContext}
 * - Execute all registered {@link ConversationPreProcessor} instances
 * - Invoke the application {@link ConversationHandler}
 * - Execute all registered {@link ConversationPostProcessor} instances
 * - Return the {@link ActivityResponse}
 *
 * The orchestrator contains no business logic and simply executes the pipeline
 * in the defined order. All pipeline extension points are optional and the
 * orchestrator functions correctly with zero pre- or post-processors.
 *
 * @since 1.0.0
 */
public final class ConversationOrchestrator {

    private final ConversationHandler handler;
    private final List<ConversationPreProcessor> preProcessors;
    private final List<ConversationPostProcessor> postProcessors;

    public ConversationOrchestrator(
            ConversationHandler handler,
            List<ConversationPreProcessor> preProcessors,
            List<ConversationPostProcessor> postProcessors
    ) {
        this.handler = Objects.requireNonNull(handler, "handler");
        this.preProcessors = Objects.requireNonNull(preProcessors, "preProcessors");
        this.postProcessors = Objects.requireNonNull(postProcessors, "postProcessors");
    }

    /**
     * Execute the conversation pipeline for the provided request.
     *
     * @param request the incoming activity request
     * @return the activity response produced by the handler
     */
    public ActivityResponse orchestrate(ActivityRequest request) {
        Objects.requireNonNull(request, "request");

        ConversationExecutionContext context = new DefaultConversationExecutionContext(
                request.conversationContext().clientSession(),
                request.conversationContext().serverSessionReference()
        );

        // Execute pre-processors
        for (ConversationPreProcessor pre : preProcessors) {
            pre.process(request, context);
        }

        // Invoke application handler
        ActivityResponse response = handler.handle(request, context);

        // Execute post-processors
        for (ConversationPostProcessor post : postProcessors) {
            post.process(request, response, context);
        }

        return response;
    }
}


