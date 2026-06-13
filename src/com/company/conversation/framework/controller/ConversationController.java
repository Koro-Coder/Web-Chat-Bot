package com.company.conversation.framework.controller;
import com.company.conversation.framework.orchestrator.ConversationOrchestrator;
import com.company.conversation.protocol.ActivityRequest;
import com.company.conversation.protocol.ActivityResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * REST controller for conversation requests.
 * 
 * ConversationController exposes the REST endpoint for processing conversation
 * activities. It accepts ActivityRequest payloads and returns ActivityResponse
 * results, delegating all business logic to the ConversationEngine.
 * 
 * The endpoint path is configurable via conversation.path property.
 * default path is /conversation.
 * 
 * @since 1.0.0
 */
@RestController
@RequestMapping("${conversation.path:/conversation}")
public class ConversationController {
    private static final Logger logger = LoggerFactory.getLogger(ConversationController.class);
    private final ConversationOrchestrator orchestrator;
    private final String path;
    /**
     * Constructs a ConversationController.
     * 
     * @param engine the conversation engine
     * @param path the REST endpoint path
     */
    public ConversationController(ConversationOrchestrator orchestrator, String path) {
        this.orchestrator = orchestrator;
        this.path = path;
    }
    /**
     * Handles incoming conversation requests.
     * 
     * Accepts an ActivityRequest and processes it through the conversation
     * engine, returning an ActivityResponse.
     * 
     * @param request the incoming activity request
     * @return response entity containing the activity response
     */
    @PostMapping
    public ResponseEntity<ActivityResponse> handle(@RequestBody ActivityRequest request) {
        logger.debug("Received conversation request: {}", request.requestId());
        try {
            ActivityResponse response = orchestrator.orchestrate(request);
            logger.debug("Sending conversation response: {}", response.responseId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error processing conversation request: {}", request.requestId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

