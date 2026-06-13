package com.company.conversation.framework.orchestrator;

import com.company.conversation.framework.context.ConversationExecutionContext;
import com.company.conversation.framework.context.DefaultConversationExecutionContext;
import com.company.conversation.framework.handler.ConversationHandler;
import com.company.conversation.framework.pipeline.pre.ConversationPreProcessor;
import com.company.conversation.framework.pipeline.post.ConversationPostProcessor;
import com.company.conversation.protocol.Activity;
import com.company.conversation.protocol.ActivityRequest;
import com.company.conversation.protocol.ActivityResponse;
import com.company.conversation.protocol.ActivityType;
import com.company.conversation.protocol.ClientSession;
import com.company.conversation.protocol.ConversationContext;
import com.company.conversation.protocol.ServerSessionReference;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConversationOrchestratorTest {

    @Test
    void orchestratorExecutesPreHandlerPostProcessorsInOrder() {
        // Prepare protocol objects
        ClientSession clientSession = new ClientSession("client-1", Map.of(), System.currentTimeMillis());
        ServerSessionReference serverRef = new ServerSessionReference("server-1");
        ConversationContext convContext = new ConversationContext("conv-1", clientSession, serverRef, "1.0.0", "corr-1");

        ActivityRequest request = new ActivityRequest("req-1", convContext, Map.of("message", "hello"));

        // Collector to record execution order
        List<String> order = new ArrayList<>();

        // Pre-processor: mark executed and put attribute
        ConversationPreProcessor pre = (req, ctx) -> {
            order.add("pre");
            ctx.attributes().put("preExecuted", true);
        };

        // Handler: assert pre executed, add handled marker, and return response
        ConversationHandler handler = (req, ctx) -> {
            assertTrue(ctx.attributes().containsKey("preExecuted"));
            order.add("handler");
            ctx.attributes().put("handled", true);
            Activity activity = new Activity("a1", ActivityType.TEXT, Map.of("text", "ok"));
            return new ActivityResponse("resp-1", req.requestId(), req.conversationContext(), List.of(activity));
        };

        // Post-processor: assert handler ran and record order
        ConversationPostProcessor post = (req, resp, ctx) -> {
            assertTrue(ctx.attributes().containsKey("handled"));
            order.add("post");
        };

        ConversationOrchestrator orchestrator = new ConversationOrchestrator(handler, List.of(pre), List.of(post));

        ActivityResponse response = orchestrator.orchestrate(request);

        assertNotNull(response);
        assertEquals("resp-1", response.responseId());
        assertEquals(List.of("pre", "handler", "post"), order);
    }
}


