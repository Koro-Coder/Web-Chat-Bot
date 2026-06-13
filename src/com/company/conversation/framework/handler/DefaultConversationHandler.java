package com.company.conversation.framework.handler;

import com.company.conversation.protocol.Activity;
import com.company.conversation.protocol.ActivityRequest;
import com.company.conversation.protocol.ActivityResponse;
import com.company.conversation.protocol.ActivityType;
import com.company.conversation.framework.context.ConversationExecutionContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
/**
 * Default conversation handler implementation.
 * 
 * DefaultConversationHandler is a no-op implementation used when no custom
 * ConversationHandler bean has been registered. It returns a simple message
 * indicating that no handler is configured, allowing applications to include
 * the framework without immediately activating it.
 * 
 * This handler is registered only when no other ConversationHandler bean
 * exists in the application context.
 * 
 * @since 1.0.0
 */
@Component
@ConditionalOnMissingBean(ConversationHandler.class)
public class DefaultConversationHandler implements ConversationHandler {
    /**
     * Handles a conversation request by returning a default response.
     * 
     * @param request the incoming activity request
     * @return a response with a default message
     */
    @Override
    public ActivityResponse handle(ActivityRequest request, ConversationExecutionContext context) {
        Activity defaultActivity = new Activity(
            UUID.randomUUID().toString(),
            ActivityType.TEXT,
            Map.of("text", "No conversation handler has been configured. Please implement ConversationHandler.")
        );

        return new ActivityResponse(
            UUID.randomUUID().toString(),
            request.requestId(),
            request.conversationContext(),
            List.of(defaultActivity)
        );
    }
}

