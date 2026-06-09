package com.company.conversation.protocol.examples;

import com.company.conversation.protocol.*;
import java.util.*;

/**
 * Comprehensive examples demonstrating usage of the Enterprise Conversation Protocol.
 * 
 * This class shows real-world patterns for:
 * - Creating requests and responses
 * - Managing session state
 * - Handling errors
 * - Working with different activity types
 * - Implementing complex bot interactions
 * 
 * @since 1.0.0
 */
public class ProtocolExamples {
    
    /**
     * Example 1: Basic conversation setup and text response
     * 
     * Demonstrates the simplest request-response cycle with text activity.
     */
    public static ActivityResponse example1_BasicConversation() {
        // Create initial client session
        Map<String, Object> clientState = new HashMap<>();
        clientState.put("userId", "user123");
        clientState.put("sessionStartTime", System.currentTimeMillis());
        
        ClientSession session = new ClientSession(
            "session-" + UUID.randomUUID(),
            clientState,
            System.currentTimeMillis()
        );
        
        // Server session reference (opaque token)
        ServerSessionReference serverRef = new ServerSessionReference(
            "server-token-" + UUID.randomUUID()
        );
        
        // Build conversation context
        String traceId = UUID.randomUUID().toString();
        ConversationContext context = new ConversationContext(
            "conversation-" + UUID.randomUUID(),
            session,
            serverRef,
            "1.0.0",
            traceId
        );
        
        // Create simple text response
        Activity textActivity = new Activity(
            "activity-1",
            ActivityType.TEXT,
            Map.of("text", "Hello! How can I help you today?")
        );
        
        return new ActivityResponse(
            "response-" + UUID.randomUUID(),
            "request-1",
            context,
            List.of(textActivity)
        );
    }
    
    
    /**
     * Example 2: Interactive buttons with actions
     * 
     * Demonstrates button group activity with associated actions.
     */
    public static ActivityResponse example2_ButtonInteraction(ConversationContext context) {
        // Create greeting activity
        Activity greeting = new Activity(
            "greeting",
            ActivityType.TEXT,
            Map.of("text", "What would you like to do?")
        );
        
        // Create button group activity
        Activity buttons = new Activity(
            "buttons",
            ActivityType.BUTTON_GROUP,
            Map.of(
                "buttons", List.of(
                    Map.of("id", "btn_search", "label", "Search", "style", "primary"),
                    Map.of("id", "btn_browse", "label", "Browse", "style", "secondary"),
                    Map.of("id", "btn_help", "label", "Help", "style", "default")
                )
            ),
            Optional.of(new Action("button_selection"))
        );
        
        return new ActivityResponse(
            "response-" + UUID.randomUUID(),
            "request-2",
            context,
            List.of(greeting, buttons)
        );
    }
    
    
    /**
     * Example 3: Form submission
     * 
     * Demonstrates form activity with structured field definitions.
     */
    public static ActivityResponse example3_FormActivity(ConversationContext context) {
        Map<String, Object> formPayload = new HashMap<>();
        formPayload.put("form_id", "contact-form-v1");
        formPayload.put("title", "Contact Us");
        
        // Define form fields
        List<Map<String, Object>> fields = List.of(
            Map.of("name", "email", "label", "Email", "type", "email", "required", true),
            Map.of("name", "subject", "label", "Subject", "type", "text", "required", true),
            Map.of("name", "message", "label", "Message", "type", "textarea", "required", true),
            Map.of("name", "priority", "label", "Priority", "type", "select", 
                   "options", List.of("Low", "Medium", "High"), "default", "Medium")
        );
        formPayload.put("fields", fields);
        
        Activity form = new Activity(
            "contact-form",
            ActivityType.FORM,
            formPayload,
            Optional.of(new Action("submit_form", Optional.of(
                Map.of("endpoint", "/api/form/submit", "method", "POST")
            )))
        );
        
        return new ActivityResponse(
            "response-" + UUID.randomUUID(),
            "request-3",
            context,
            List.of(form)
        );
    }
    
    
    /**
     * Example 4: Complex multi-activity response
     * 
     * Demonstrates server returning multiple activities to create a rich experience.
     */
    public static ActivityResponse example4_MultiActivityResponse(ConversationContext context) {
        List<Activity> activities = new ArrayList<>();
        
        // Greeting
        activities.add(new Activity(
            "greeting",
            ActivityType.TEXT,
            Map.of("text", "Welcome! Here's your dashboard")
        ));
        
        // Stats as HTML
        activities.add(new Activity(
            "stats",
            ActivityType.HTML,
            Map.of(
                "html", "<div class='stats'><p>Emails: 5</p><p>Tasks: 3</p></div>"
            )
        ));
        
        // Action buttons
        activities.add(new Activity(
            "actions",
            ActivityType.BUTTON_GROUP,
            Map.of("buttons", List.of(
                Map.of("id", "check-email", "label", "Check Email"),
                Map.of("id", "view-tasks", "label", "View Tasks")
            ))
        ));
        
        return new ActivityResponse(
            "response-" + UUID.randomUUID(),
            "request-4",
            context,
            activities
        );
    }
    
    
    /**
     * Example 5: Error response with validation details
     * 
     * Demonstrates structured error handling for form validation.
     */
    public static ActivityResponse example5_ErrorResponse(ConversationContext context) {
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("fieldErrors", Map.of(
            "email", "Invalid email format",
            "age", "Must be between 18 and 120",
            "password", "Must contain at least one uppercase letter"
        ));
        errorDetails.put("attemptCount", 3);
        
        ErrorResponse error = new ErrorResponse(
            400,
            "VALIDATION_ERROR",
            "Form validation failed",
            Optional.of(errorDetails),
            Optional.of(context.correlationId()),
            Optional.of(Map.of("validationRules", "strict"))
        );
        
        return new ActivityResponse(
            "response-" + UUID.randomUUID(),
            "request-5",
            context,
            List.of(), // No activities in error case
            Optional.of(error)
        );
    }
    
    
    /**
     * Example 6: Client session state updates
     * 
     * Demonstrates how client session state is maintained across requests.
     */
    public static void example6_SessionStateManagement() {
        // Initial session with empty state
        Map<String, Object> initialState = new HashMap<>();
        ClientSession session1 = new ClientSession(
            "session-abc",
            initialState,
            System.currentTimeMillis()
        );
        
        // After first interaction, state is updated by bot
        Map<String, Object> updatedState = new HashMap<>();
        updatedState.put("userId", "user123");
        updatedState.put("userProfile", Map.of(
            "name", "John Doe",
            "preferences", Map.of("language", "en", "timezone", "UTC")
        ));
        updatedState.put("conversationHistory", List.of(
            "greeting",
            "form_submitted",
            "confirmation_shown"
        ));
        
        ClientSession session2 = new ClientSession(
            "session-abc", // Same session ID
            updatedState,  // Updated state
            System.currentTimeMillis()
        );
        
        System.out.println("Session 1 state: " + session1.state());
        System.out.println("Session 2 state: " + session2.state());
    }
    
    
    /**
     * Example 7: Distributed tracing with correlation IDs
     * 
     * Demonstrates how to use correlation IDs for tracing across systems.
     */
    public static void example7_DistributedTracing() {
        String traceId = UUID.randomUUID().toString();
        String spanId = UUID.randomUUID().toString();
        
        ClientSession session = new ClientSession(
            "session-trace",
            Map.of("userId", "user456"),
            System.currentTimeMillis()
        );
        
        ConversationContext context = new ConversationContext(
            "conv-trace",
            session,
            new ServerSessionReference("server-trace-token"),
            "1.0.0",
            traceId,
            Optional.of(Map.of(
                "spanId", spanId,
                "service", "conversation-bot",
                "environment", "production",
                "version", "1.0.0"
            ))
        );
        
        Activity activity = new Activity(
            "activity-trace",
            ActivityType.TEXT,
            Map.of("text", "Processing order"),
            Optional.empty(),
            Optional.of(Map.of(
                "traceId", traceId,
                "spanId", spanId
            )),
            Optional.of(traceId) // Correlation ID
        );
        
        System.out.println("Trace ID: " + traceId);
        System.out.println("This activity belongs to trace: " + activity.correlationId());
    }
    
    
    /**
     * Example 8: Asynchronous events
     * 
     * Demonstrates conversation events for out-of-band notifications.
     */
    public static List<ConversationEvent> example8_ConversationEvents() {
        List<ConversationEvent> events = new ArrayList<>();
        
        // Session started event
        events.add(new ConversationEvent(
            "event-1",
            "conversation.started",
            "conv-xyz",
            System.currentTimeMillis(),
            Map.of(
                "initiator", "user",
                "platform", "web",
                "userId", "user123"
            )
        ));
        
        // User action event
        events.add(new ConversationEvent(
            "event-2",
            "user.action_taken",
            "conv-xyz",
            System.currentTimeMillis(),
            Map.of(
                "action", "button_clicked",
                "buttonId", "btn_search",
                "timestamp", System.currentTimeMillis()
            )
        ));
        
        // Session ended event
        events.add(new ConversationEvent(
            "event-3",
            "conversation.ended",
            "conv-xyz",
            System.currentTimeMillis(),
            Map.of(
                "reason", "user_disconnect",
                "duration_seconds", 300
            )
        ));
        
        return events;
    }
    
    
    /**
     * Example 9: Custom activity types
     * 
     * Demonstrates extensibility using custom activity types.
     */
    public static ActivityResponse example9_CustomActivityTypes(ConversationContext context) {
        // Custom chart activity
        Activity chartActivity = new Activity(
            "chart-1",
            ActivityType.CUSTOM,
            Map.of(
                "customType", "line_chart",
                "title", "Revenue Trend",
                "data", Map.of(
                    "labels", List.of("Jan", "Feb", "Mar"),
                    "values", List.of(100, 150, 200)
                ),
                "options", Map.of(
                    "showLegend", true,
                    "animationDuration", 500
                )
            ),
            Optional.empty(), // No action
            Optional.of(Map.of(
                "renderer", "charts-library-v2",
                "theme", "dark"
            )),
            Optional.empty() // No correlation ID
        );
        
        // Custom data table
        Activity tableActivity = new Activity(
            "table-1",
            ActivityType.CUSTOM,
            Map.of(
                "customType", "interactive_table",
                "headers", List.of("Name", "Score", "Status"),
                "rows", List.of(
                    List.of("Alice", 95, "Complete"),
                    List.of("Bob", 87, "In Progress"),
                    List.of("Charlie", 92, "Complete")
                ),
                "sortable", true,
                "filterable", true
            ),
            Optional.empty(), // No action
            Optional.empty(), // No metadata
            Optional.empty()  // No correlation ID
        );
        
        return new ActivityResponse(
            "response-" + UUID.randomUUID(),
            "request-9",
            context,
            List.of(chartActivity, tableActivity)
        );
    }
    
    
    /**
     * Example 10: Request with idempotency key
     * 
     * Demonstrates how request IDs enable idempotent operations.
     */
    public static ActivityRequest example10_IdempotentRequest(ConversationContext context) {
        // Same request ID can be reused for idempotent retries
        String idempotencyKey = UUID.randomUUID().toString();
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("action", "transfer_funds");
        payload.put("amount", 1000.00);
        payload.put("recipient", "account-xyz");
        
        // First request attempt
        ActivityRequest request1 = new ActivityRequest(
            idempotencyKey, // Use as idempotency key
            context,
            payload
        );
        
        // Retry with same request ID - server can detect it's a duplicate
        ActivityRequest request2 = new ActivityRequest(
            idempotencyKey, // Same ID - server recognizes retry
            context,
            payload
        );
        
        System.out.println("Request 1 ID: " + request1.requestId());
        System.out.println("Request 2 ID: " + request2.requestId());
        System.out.println("Same request? " + request1.requestId().equals(request2.requestId()));
        
        return request1;
    }
    
    
    public static void main(String[] args) {
        System.out.println("Enterprise Conversation Protocol Examples");
        System.out.println("==========================================\n");
        
        // Run examples
        ActivityResponse ex1 = example1_BasicConversation();
        System.out.println("Example 1 - Basic Conversation:");
        System.out.println("Activities: " + ex1.activities().size());
        System.out.println();
        
        example6_SessionStateManagement();
        System.out.println();
        
        example7_DistributedTracing();
        System.out.println();
        
        List<ConversationEvent> events = example8_ConversationEvents();
        System.out.println("Example 8 - Conversation Events: " + events.size() + " events");
        events.forEach(e -> System.out.println("  - " + e.eventType()));
        System.out.println();
        
        example10_IdempotentRequest(ex1.conversationContext());
        System.out.println();
        
        System.out.println("All examples completed successfully!");
    }
}



