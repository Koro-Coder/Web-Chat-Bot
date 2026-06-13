package com.company.conversation.framework.engine;
import com.company.conversation.protocol.ActivityRequest;
import com.company.conversation.protocol.ActivityResponse;
/**
 * Extension point for analytics collection.
 * 
 * AnalyticsCollector allows applications to collect metrics and telemetry
 * about conversation interactions. Implementations can track request/response
 * times, user behavior, error rates, etc.
 * 
 * Analytics collectors are optional: if no bean is registered, analytics
 * collection is skipped.
 * 
 * @since 1.0.0
 */
@FunctionalInterface
public interface AnalyticsCollector {
    /**
     * Collects analytics for completed request-response cycle.
     * 
     * @param request the incoming request
     * @param response the outgoing response
     */
    void collect(ActivityRequest request, ActivityResponse response);
}

