package com.company.conversation.framework.engine;
import com.company.conversation.framework.handler.ConversationHandler;
import com.company.conversation.framework.exception.HandlerInvocationException;
import com.company.conversation.framework.exception.ValidationException;
import com.company.conversation.protocol.ActivityRequest;
import com.company.conversation.protocol.ActivityResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.company.conversation.framework.context.ConversationExecutionContext;
import com.company.conversation.framework.context.DefaultConversationExecutionContext;
import java.util.Optional;
/**
 * Core orchestration engine for conversation processing.
 * 
 * ConversationEngine provides the main orchestration logic for processing
 * conversation requests. It manages the lifecycle of request processing,
 * including validation, handler invocation, and response generation.
 * 
 * The engine defines extension points for validation, session management,
 * analytics, and event publishing without implementing these capabilities
 * itself. This maintains the framework''s zero-business-logic constraint.
 * 
 * @since 1.0.0
 */
public class ConversationEngine {
    private static final Logger logger = LoggerFactory.getLogger(ConversationEngine.class);
    private final ConversationHandler handler;
    private final Optional<RequestValidator> requestValidator;
    private final Optional<SessionManager> sessionManager;
    private final Optional<AnalyticsCollector> analyticsCollector;
    private final Optional<EventPublisher> eventPublisher;
    /**
     * Constructs a ConversationEngine with required dependencies.
     * 
     * @param handler the conversation handler
     */
    public ConversationEngine(ConversationHandler handler) {
        this(handler, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }
    /**
     * Constructs a ConversationEngine with all extension points.
     * 
     * @param handler the conversation handler
     * @param requestValidator optional request validator
     * @param sessionManager optional session manager
     * @param analyticsCollector optional analytics collector
     * @param eventPublisher optional event publisher
     */
    public ConversationEngine(
            ConversationHandler handler,
            Optional<RequestValidator> requestValidator,
            Optional<SessionManager> sessionManager,
            Optional<AnalyticsCollector> analyticsCollector,
            Optional<EventPublisher> eventPublisher
    ) {
        this.handler = handler;
        this.requestValidator = requestValidator;
        this.sessionManager = sessionManager;
        this.analyticsCollector = analyticsCollector;
        this.eventPublisher = eventPublisher;
    }
    /**
     * Processes a conversation request and returns a response.
     * 
     * This method orchestrates the complete request-response cycle:
     * 1. Validates the request
     * 2. Loads session information
     * 3. Publishes the incoming event
     * 4. Invokes the conversation handler
     * 5. Collects analytics
     * 6. Publishes the outgoing event
     * 7. Returns the response
     * 
     * All errors are handled gracefully and included in the response.
     * 
     * @param request the incoming activity request
     * @return the activity response
     */
    public ActivityResponse process(ActivityRequest request) throws com.company.conversation.framework.exception.HandlerInvocationException {
        logger.debug("Processing request: {}", request.requestId());
        try {
            // Extension point: validate request
            validateRequest(request);
            // Extension point: load session
            loadSession(request);
            // Extension point: publish incoming event
            publishIncomingEvent(request);
            // Create execution context and invoke the handler
            ConversationExecutionContext context = new DefaultConversationExecutionContext(
                    request.conversationContext().clientSession(),
                    request.conversationContext().serverSessionReference()
            );
            ActivityResponse response = handler.handle(request, context);
            // Extension point: collect analytics
            collectAnalytics(request, response);
            // Extension point: publish outgoing event
            publishOutgoingEvent(response);
            logger.debug("Request processed successfully: {}", request.requestId());
            return response;
        } catch (ValidationException ve) {
            logger.warn("Validation failed for request: {}", request.requestId(), ve);
            throw new HandlerInvocationException("Request validation failed", ve);
        } catch (Exception e) {
            logger.error("Error processing request: {}", request.requestId(), e);
            throw new HandlerInvocationException("Request processing failed", e);
        }
    }
    /**
     * Extension point for request validation.
     * 
     * Invokes the optional RequestValidator if registered.
     * 
     * @param request the request to validate
     * @throws ValidationException if validation fails
     */
    private void validateRequest(ActivityRequest request) throws ValidationException {
        if (requestValidator.isPresent()) {
            requestValidator.get().validate(request);
        }
    }
    /**
     * Extension point for session management.
     * 
     * Invokes the optional SessionManager if registered.
     * 
     * @param request the request containing session information
     */
    private void loadSession(ActivityRequest request) {
        if (sessionManager.isPresent()) {
            sessionManager.get().loadSession(request);
        }
    }
    /**
     * Extension point for analytics collection.
     * 
     * Invokes the optional AnalyticsCollector if registered.
     * 
     * @param request the incoming request
     * @param response the outgoing response
     */
    private void collectAnalytics(ActivityRequest request, ActivityResponse response) {
        if (analyticsCollector.isPresent()) {
            analyticsCollector.get().collect(request, response);
        }
    }
    /**
     * Extension point for incoming event publishing.
     * 
     * Invokes the optional EventPublisher if registered to publish
     * incoming request events.
     * 
     * @param request the incoming request
     */
    private void publishIncomingEvent(ActivityRequest request) {
        if (eventPublisher.isPresent()) {
            eventPublisher.get().publishIncoming(request);
        }
    }
    /**
     * Extension point for outgoing event publishing.
     * 
     * Invokes the optional EventPublisher if registered to publish
     * outgoing response events.
     * 
     * @param response the outgoing response
     */
    private void publishOutgoingEvent(ActivityResponse response) {
        if (eventPublisher.isPresent()) {
            eventPublisher.get().publishOutgoing(response);
        }
    }
}

