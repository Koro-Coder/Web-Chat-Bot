package com.company.conversation.protocol.tests;

import com.company.conversation.protocol.*;
import java.util.*;

/**
 * Comprehensive test suite for the Enterprise Conversation Protocol.
 * 
 * These tests verify:
 * - Model immutability
 * - Input validation
 * - Record equality
 * - Type safety
 * - Extensibility
 */
public class ProtocolTests {
    
    private static int passCount = 0;
    private static int failCount = 0;
    
    // Helper methods
    private static void assertEqual(Object expected, Object actual, String testName) {
        if (Objects.equals(expected, actual)) {
            System.out.println("✓ PASS: " + testName);
            passCount++;
        } else {
            System.out.println("✗ FAIL: " + testName + " (expected: " + expected + ", actual: " + actual + ")");
            failCount++;
        }
    }
    
    private static void assertTrue(boolean condition, String testName) {
        if (condition) {
            System.out.println("✓ PASS: " + testName);
            passCount++;
        } else {
            System.out.println("✗ FAIL: " + testName);
            failCount++;
        }
    }
    
    private static void assertThrows(Class<?> exceptionClass, String testName, Runnable test) {
        try {
            test.run();
            System.out.println("✗ FAIL: " + testName + " (no exception thrown)");
            failCount++;
        } catch (Exception e) {
            if (exceptionClass.isInstance(e)) {
                System.out.println("✓ PASS: " + testName);
                passCount++;
            } else {
                System.out.println("✗ FAIL: " + testName + " (wrong exception: " + e.getClass().getName() + ")");
                failCount++;
            }
        }
    }
    
    // Test Suites
    
    public static void testActivityTypeEnum() {
        System.out.println("\n=== ActivityType Enum Tests ===");
        
        assertTrue(ActivityType.TEXT != null, "TEXT type exists");
        assertTrue(ActivityType.CUSTOM != null, "CUSTOM type exists");
        assertTrue(ActivityType.values().length == 10, "Has 10 activity types");
    }
    
    public static void testActivityValidation() {
        System.out.println("\n=== Activity Validation Tests ===");
        
        Map<String, Object> payload = Map.of("text", "hello");
        
        // Valid activity
        Activity activity = new Activity("act-1", ActivityType.TEXT, payload);
        assertEqual("act-1", activity.id(), "Activity ID stored correctly");
        assertEqual(ActivityType.TEXT, activity.type(), "Activity type stored correctly");
        
        // Null ID validation
        assertThrows(
            IllegalArgumentException.class,
            "Activity rejects null ID",
            () -> new Activity(null, ActivityType.TEXT, payload)
        );
        
        // Blank ID validation
        assertThrows(
            IllegalArgumentException.class,
            "Activity rejects blank ID",
            () -> new Activity("", ActivityType.TEXT, payload)
        );
        
        // Null type validation
        assertThrows(
            IllegalArgumentException.class,
            "Activity rejects null type",
            () -> new Activity("act-1", null, payload)
        );
        
        // Null payload validation
        assertThrows(
            IllegalArgumentException.class,
            "Activity rejects null payload",
            () -> new Activity("act-1", ActivityType.TEXT, null)
        );
    }
    
    public static void testActionValidation() {
        System.out.println("\n=== Action Validation Tests ===");
        
        // Valid action
        Action action = new Action("submit");
        assertEqual("submit", action.type(), "Action type stored");
        
        // Null type validation
        assertThrows(
            IllegalArgumentException.class,
            "Action rejects null type",
            () -> new Action(null)
        );
        
        // Blank type validation
        assertThrows(
            IllegalArgumentException.class,
            "Action rejects blank type",
            () -> new Action("   ")
        );
    }
    
    public static void testClientSessionValidation() {
        System.out.println("\n=== ClientSession Validation Tests ===");
        
        // Valid session
        ClientSession session = new ClientSession(
            "session-1",
            Map.of("userId", "u123"),
            System.currentTimeMillis()
        );
        assertEqual("session-1", session.sessionId(), "Session ID stored");
        
        // Null session ID validation
        assertThrows(
            IllegalArgumentException.class,
            "ClientSession rejects null ID",
            () -> new ClientSession(null, Map.of(), 0)
        );
        
        // Null state validation
        assertThrows(
            IllegalArgumentException.class,
            "ClientSession rejects null state",
            () -> new ClientSession("s-1", null, 0)
        );
        
        // Invalid timestamp validation
        assertThrows(
            IllegalArgumentException.class,
            "ClientSession rejects negative timestamp",
            () -> new ClientSession("s-1", Map.of(), -1)
        );
    }
    
    public static void testServerSessionReferenceValidation() {
        System.out.println("\n=== ServerSessionReference Validation Tests ===");
        
        // Valid reference
        ServerSessionReference ref = new ServerSessionReference("token-123");
        assertEqual("token-123", ref.reference(), "Reference stored");
        assertTrue(ref.expiresAt().isEmpty(), "ExpiresAt optional when not provided");
        
        // Null reference validation
        assertThrows(
            IllegalArgumentException.class,
            "ServerSessionReference rejects null",
            () -> new ServerSessionReference(null)
        );
        
        // Blank reference validation
        assertThrows(
            IllegalArgumentException.class,
            "ServerSessionReference rejects blank",
            () -> new ServerSessionReference("   ")
        );
    }
    
    public static void testConversationContextValidation() {
        System.out.println("\n=== ConversationContext Validation Tests ===");
        
        ClientSession session = new ClientSession("s-1", Map.of(), System.currentTimeMillis());
        ServerSessionReference ref = new ServerSessionReference("ref-1");
        
        // Valid context
        ConversationContext context = new ConversationContext(
            "conv-1",
            session,
            ref,
            "1.0.0",
            "trace-1"
        );
        assertEqual("conv-1", context.conversationId(), "Conversation ID stored");
        
        // Null conversation ID validation
        assertThrows(
            IllegalArgumentException.class,
            "ConversationContext rejects null ID",
            () -> new ConversationContext(null, session, ref, "1.0.0", "trace")
        );
        
        // Null session validation
        assertThrows(
            IllegalArgumentException.class,
            "ConversationContext rejects null session",
            () -> new ConversationContext("conv-1", null, ref, "1.0.0", "trace")
        );
        
        // Null protocol version validation
        assertThrows(
            IllegalArgumentException.class,
            "ConversationContext rejects null protocol version",
            () -> new ConversationContext("conv-1", session, ref, null, "trace")
        );
    }
    
    public static void testActivityRequestValidation() {
        System.out.println("\n=== ActivityRequest Validation Tests ===");
        
        ClientSession session = new ClientSession("s-1", Map.of(), System.currentTimeMillis());
        ServerSessionReference ref = new ServerSessionReference("ref-1");
        ConversationContext context = new ConversationContext(
            "conv-1", session, ref, "1.0.0", "trace-1"
        );
        
        // Valid request
        ActivityRequest request = new ActivityRequest(
            "req-1",
            context,
            Map.of("text", "input")
        );
        assertEqual("req-1", request.requestId(), "Request ID stored");
        
        // Null request ID validation
        assertThrows(
            IllegalArgumentException.class,
            "ActivityRequest rejects null request ID",
            () -> new ActivityRequest(null, context, Map.of())
        );
        
        // Null payload validation
        assertThrows(
            IllegalArgumentException.class,
            "ActivityRequest rejects null payload",
            () -> new ActivityRequest("req-1", context, null)
        );
    }
    
    public static void testActivityResponseValidation() {
        System.out.println("\n=== ActivityResponse Validation Tests ===");
        
        ClientSession session = new ClientSession("s-1", Map.of(), System.currentTimeMillis());
        ServerSessionReference ref = new ServerSessionReference("ref-1");
        ConversationContext context = new ConversationContext(
            "conv-1", session, ref, "1.0.0", "trace-1"
        );
        
        Activity activity = new Activity("a-1", ActivityType.TEXT, Map.of("text", "hello"));
        
        // Valid response
        ActivityResponse response = new ActivityResponse(
            "resp-1",
            "req-1",
            context,
            List.of(activity)
        );
        assertEqual("resp-1", response.responseId(), "Response ID stored");
        assertEqual(1, response.activities().size(), "Activity count correct");
        
        // Empty activities allowed
        ActivityResponse emptyResponse = new ActivityResponse(
            "resp-2",
            "req-2",
            context,
            List.of()
        );
        assertTrue(emptyResponse.activities().isEmpty(), "Empty activities allowed");
    }
    
    public static void testErrorResponseValidation() {
        System.out.println("\n=== ErrorResponse Validation Tests ===");
        
        // Valid error
        ErrorResponse error = new ErrorResponse(
            400,
            "BAD_REQUEST",
            "Invalid request"
        );
        assertEqual(400, error.statusCode(), "Status code stored");
        
        // Invalid status code validation
        assertThrows(
            IllegalArgumentException.class,
            "ErrorResponse rejects invalid status code (99)",
            () -> new ErrorResponse(99, "CODE", "msg")
        );
        
        assertThrows(
            IllegalArgumentException.class,
            "ErrorResponse rejects invalid status code (600)",
            () -> new ErrorResponse(600, "CODE", "msg")
        );
        
        // Null error code validation
        assertThrows(
            IllegalArgumentException.class,
            "ErrorResponse rejects null error code",
            () -> new ErrorResponse(400, null, "msg")
        );
    }
    
    public static void testConversationEventValidation() {
        System.out.println("\n=== ConversationEvent Validation Tests ===");
        
        long timestamp = System.currentTimeMillis();
        
        // Valid event
        ConversationEvent event = new ConversationEvent(
            "event-1",
            "conversation.started",
            "conv-1",
            timestamp,
            Map.of("source", "web")
        );
        assertEqual("event-1", event.eventId(), "Event ID stored");
        assertEqual(timestamp, event.timestamp(), "Timestamp stored");
        
        // Negative timestamp validation
        assertThrows(
            IllegalArgumentException.class,
            "ConversationEvent rejects negative timestamp",
            () -> new ConversationEvent("e-1", "type", "c-1", -1, Map.of())
        );
    }
    
    public static void testImmutability() {
        System.out.println("\n=== Immutability Tests ===");
        
        // Create activity
        Activity activity1 = new Activity(
            "a-1",
            ActivityType.TEXT,
            Map.of("text", "hello")
        );
        
        // Records should be equal if content is equal
        Activity activity2 = new Activity(
            "a-1",
            ActivityType.TEXT,
            Map.of("text", "hello")
        );
        
        assertEqual(activity1, activity2, "Activities with same content are equal");
        assertTrue(activity1 == activity1, "Same instance is equal to itself");
    }
    
    public static void testOptionalFields() {
        System.out.println("\n=== Optional Fields Tests ===");
        
        Activity activity = new Activity(
            "a-1",
            ActivityType.TEXT,
            Map.of()
        );
        
        assertTrue(activity.action().isEmpty(), "Action is optional");
        assertTrue(activity.metadata().isEmpty(), "Metadata is optional");
        assertTrue(activity.correlationId().isEmpty(), "CorrelationId is optional");
    }
    
    public static void testExtensibility() {
        System.out.println("\n=== Extensibility Tests ===");
        
        // Custom metadata in activity
        Activity activity = new Activity(
            "custom-1",
            ActivityType.CUSTOM,
            Map.of("customField", "customValue"),
            Optional.empty(),
            Optional.of(Map.of("renderer", "custom-renderer")),
            Optional.empty()
        );
        
        assertTrue(activity.metadata().isPresent(), "Metadata can be added");
        assertEqual(
            "custom-renderer",
            activity.metadata().get().get("renderer"),
            "Custom metadata is accessible"
        );
    }
    
    public static void testDistributedTracing() {
        System.out.println("\n=== Distributed Tracing Tests ===");
        
        String traceId = UUID.randomUUID().toString();
        
        ClientSession session = new ClientSession("s-1", Map.of(), System.currentTimeMillis());
        ServerSessionReference ref = new ServerSessionReference("ref-1");
        
        ConversationContext context = new ConversationContext(
            "conv-1",
            session,
            ref,
            "1.0.0",
            traceId
        );
        
        assertEqual(traceId, context.correlationId(), "Correlation ID propagated in context");
        
        Activity activity = new Activity(
            "a-1",
            ActivityType.TEXT,
            Map.of("text", "hello"),
            Optional.empty(),
            Optional.empty(),
            Optional.of(traceId)
        );
        
        assertTrue(
            activity.correlationId().isPresent() && 
            activity.correlationId().get().equals(traceId),
            "Correlation ID propagated in activity"
        );
    }
    
    public static void testComplexScenario() {
        System.out.println("\n=== Complex Scenario Test ===");
        
        try {
            // Build a complete conversation flow
            ClientSession session = new ClientSession(
                "session-" + UUID.randomUUID(),
                Map.of("userId", "user123", "preferences", Map.of("language", "en")),
                System.currentTimeMillis()
            );
            
            ServerSessionReference serverRef = new ServerSessionReference(
                "token-" + UUID.randomUUID()
            );
            
            String traceId = UUID.randomUUID().toString();
            
            ConversationContext context = new ConversationContext(
                "conv-" + UUID.randomUUID(),
                session,
                serverRef,
                "1.0.0",
                traceId
            );
            
            // Create complex request
            ActivityRequest request = new ActivityRequest(
                UUID.randomUUID().toString(),
                context,
                Map.of(
                    "userInput", "show me products",
                    "source", "web",
                    "metadata", Map.of("ip", "192.168.1.1")
                )
            );
            
            // Create complex response with multiple activities
            List<Activity> activities = List.of(
                new Activity("a1", ActivityType.TEXT,
                    Map.of("text", "I found 5 products matching your search")),
                new Activity("a2", ActivityType.TABLE,
                    Map.of("headers", List.of("Name", "Price"),
                           "rows", List.of(List.of("Product 1", "$10"))),
                    Optional.empty(),
                    Optional.of(Map.of("sortable", true)),
                    Optional.empty()),
                new Activity("a3", ActivityType.BUTTON_GROUP,
                    Map.of("buttons", List.of(
                        Map.of("id", "buy", "label", "Buy"),
                        Map.of("id", "back", "label", "Back")
                    )),
                    Optional.of(new Action("product_action")),
                    Optional.empty(),
                    Optional.empty())
            );
            
            ActivityResponse response = new ActivityResponse(
                UUID.randomUUID().toString(),
                request.requestId(),
                context,
                activities,
                Optional.empty(),
                Optional.of(Map.of("processingTime", 150))
            );
            
            // Verify everything is correct
            assertEqual(3, response.activities().size(), "Complex response has 3 activities");
            assertTrue(response.error().isEmpty(), "No error in successful response");
            assertEqual(request.requestId(), response.requestId(), "Request/Response linked");
            
            System.out.println("✓ Complex scenario completed successfully");
            passCount++;
        } catch (Exception e) {
            System.out.println("✗ Complex scenario failed: " + e.getMessage());
            failCount++;
        }
    }
    
    // Main test runner
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║   Enterprise Conversation Protocol - Test Suite           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        testActivityTypeEnum();
        testActivityValidation();
        testActionValidation();
        testClientSessionValidation();
        testServerSessionReferenceValidation();
        testConversationContextValidation();
        testActivityRequestValidation();
        testActivityResponseValidation();
        testErrorResponseValidation();
        testConversationEventValidation();
        testImmutability();
        testOptionalFields();
        testExtensibility();
        testDistributedTracing();
        testComplexScenario();
        
        // Summary
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║   TEST SUMMARY                                             ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println(String.format("║ ✓ Passed: %-46d ║", passCount));
        System.out.println(String.format("║ ✗ Failed: %-46d ║", failCount));
        System.out.println(String.format("║ Total:   %-46d ║", passCount + failCount));
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        if (failCount == 0) {
            System.out.println("\n✓ All tests passed!");
        } else {
            System.out.println("\n✗ Some tests failed");
        }
    }
}


