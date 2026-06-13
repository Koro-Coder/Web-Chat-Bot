package com.company.conversation.framework.engine;
import com.company.conversation.protocol.ActivityRequest;
import com.company.conversation.protocol.ActivityResponse;
/**
 * Extension point for event publishing.
 * 
 * EventPublisher allows applications to publish events for incoming requests
 * and outgoing responses. This enables reactive processing, audit logging,
 * real-time monitoring, and workflow integration.
 * 
 * Event publishers are optional: if no bean is registered, event publishing
 * is skipped.
 * 
 * @since 1.0.0
 */
public interface EventPublisher {
    /**
     * Publishes an event for an incoming conversation request.
     * 
     * @param request the incoming activity request
     */
    void publishIncoming(ActivityRequest request);
    /**
     * Publishes an event for an outgoing conversation response.
     * 
     * @param response the outgoing activity response
     */
    void publishOutgoing(ActivityResponse response);
}

