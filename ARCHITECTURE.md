# Enterprise Conversation Protocol - Architecture Guide

## Executive Summary

The Enterprise Conversation Protocol is a **generic, immutable, and extensible** framework for building conversational systems. It supports multiple bot implementations without encoding any business logic, following clean architecture principles.

## Design Goals

1. **Genericity** - No business-specific concepts (search, intent, LLM, forms, etc.)
2. **Immutability** - All models are Java 21 records for thread safety and value semantics
3. **Extensibility** - Dynamic payloads and metadata enable additions without protocol changes
4. **Opacity** - Session data is completely opaque to the SDK (important design principle)
5. **Traceability** - Built-in support for distributed tracing and correlation IDs
6. **Simplicity** - Minimal dependencies, focused API surface

## Architecture Layers

```
                    ┌─────────────────────────────────┐
                    │      Bot Implementation         │
                    │  (Search, Forms, AI, etc.)      │
                    └──────────────┬──────────────────┘
                                   │
                    ┌──────────────▼──────────────────┐
                    │   Protocol Exchange Layer       │
                    │  (ActivityRequest/Response)     │
                    └──────────────┬──────────────────┘
                                   │
                    ┌──────────────▼──────────────────┐
                    │    Protocol Models Layer        │
                    │  (Records with Validation)      │
                    │  - Immutable                    │
                    │  - Type-safe                    │
                    │  - Self-documenting             │
                    └──────────────┬──────────────────┘
                                   │
                    ┌──────────────▼──────────────────┐
                    │   Serialization Layer           │
                    │   (JSON, gRPC, etc.)            │
                    └──────────────┬──────────────────┘
                                   │
                    ┌──────────────▼──────────────────┐
                    │      Network Transport          │
                    │   (HTTP, WebSocket, etc.)       │
                    └─────────────────────────────────┘
```

## Core Concepts

### 1. ActivityType Hierarchy

**Purpose:** Defines the type of content being transported.

```
ActivityType (Enum)
├── TEXT              # Plain text
├── HTML              # HTML markup
├── MARKDOWN          # Markdown format
├── FORM              # Form structure
├── BUTTON_GROUP      # Interactive buttons
├── IMAGE             # Image content
├── TABLE             # Tabular data
├── FILE              # File reference
├── SYSTEM            # System messages
└── CUSTOM            # Extension point
```

**Design Principle:** Each type can have completely different payload structure, allowing flexibility without breaking the protocol.

### 2. Activity - The Core Message Unit

**Purpose:** Represents a single unit of communication.

```java
record Activity(
    String id,                           // Unique within conversation
    ActivityType type,                   // Semantic type
    Map<String, Object> payload,         // Opaque content
    Optional<Action> action,             // Optional interaction
    Optional<Map<String, Object>> metadata,
    Optional<String> correlationId       // Tracing
)
```

**Key Properties:**
- **Immutable** - Thread-safe by design
- **Opaque payloads** - No protocol interpretation
- **Composable** - Multiple activities in single response
- **Traceable** - Built-in correlation support

### 3. Session Management - Critical Design Pattern

#### Why Opaque Sessions?

The protocol must NOT interpret session state to enable:
- Infinite flexibility in application state structure
- Complete separation of concerns (protocol vs. business logic)
- Easy bot implementation swapping
- Stateless protocol layer

#### ClientSession - Application Perspective

```java
record ClientSession(
    String sessionId,
    Map<String, Object> state,    // Opaque - never interpreted
    long createdAt,
    Optional<Map<String, Object>> metadata
)
```

**Important:**
- The SDK stores `state` and exchanges it with the server
- The SDK NEVER reads, validates, or modifies `state`
- Each bot implementation defines state structure
- Application code accesses `state` directly (opaque to protocol)

#### ServerSessionReference - Stateless Design

```java
record ServerSessionReference(
    String reference,               // Opaque token/ID
    Optional<Long> expiresAt
)
```

**Purpose:**
- Allows stateless protocol layer
- Server handles session lookup using reference
- SDK never validates or interprets reference
- Enables scaling with distributed sessions

### 4. Request-Response Pattern

```
Client                          Server
  │                               │
  ├──── ActivityRequest ─────────►│
  │     ├─ requestId             │
  │     ├─ conversationContext    │
  │     └─ payload               │
  │                               │ (Process with bot)
  │                               │
  │◄──── ActivityResponse ────────┤
  │      ├─ responseId            │
  │      ├─ activities (0..N)     │
  │      ├─ error (optional)      │
  │      └─ conversationContext   │
  │                               │
```

**Key Design:**
- **Correlation:** requestId matches in response for tracing
- **Multiplex:** Single response can contain multiple activities
- **Stateless:** Context carries all session info
- **Fault tolerant:** Optional error response

### 5. Conversation Context - State Container

```java
record ConversationContext(
    String conversationId,
    ClientSession clientSession,           // Client state
    ServerSessionReference serverSessionReference,
    String protocolVersion,                // Versioning
    String correlationId,                  // Tracing
    Optional<Map<String, Object>> metadata
)
```

**Flow:**
1. Created by client with initial session state
2. Sent in every request
3. Updated by server with new session state
4. Returned in response
5. Used by client for next request

### 6. Error Handling

```java
record ErrorResponse(
    int statusCode,                    // HTTP-style (100-599)
    String errorCode,                  // Application code
    String message,                    // Message
    Optional<Map<String, Object>> details,  // Error details
    Optional<String> correlationId,
    Optional<Map<String, Object>> metadata
)
```

**Design:**
- HTTP-compatible status codes for interoperability
- Application-specific error codes for handling
- Optional field errors for structured validation failures
- Full tracing support with correlation IDs

### 7. Async Events

```java
record ConversationEvent(
    String eventId,
    String eventType,                  // Semantic type
    String conversationId,
    long timestamp,
    Map<String, Object> payload,       // Opaque
    Optional<String> correlationId,
    Optional<Map<String, Object>> metadata
)
```

**Use Cases:**
- Session expiration notifications
- Conversation lifecycle events
- Bot status changes
- Background process completions

## Extensibility Framework

### Level 1: Metadata Extensibility

Add application-specific fields to any model:

```java
activity.metadata().ifPresent(meta -> {
    String renderer = (String) meta.get("custom_renderer");
    // Use custom rendering logic
});
```

### Level 2: Payload Opaqueness

Define domain-specific structures in activity payloads:

```java
Activity customActivity = new Activity(
    "custom-1",
    ActivityType.CUSTOM,
    Map.of(
        "customType", "order_summary",
        "orderId", "ord-123",
        "items", itemsList,
        "total", 99.99
    )
);
```

### Level 3: Custom Activity Types

Extend supported types with custom types:

```
ActivityType.CUSTOM with type string: "custom:my_domain_type"
```

## Immutability Benefits

### Thread Safety
```java
// Safe to share between threads
ActivityResponse response = ...;
executor.submit(() -> processResponse(response));
executor.submit(() -> logResponse(response));
```

### Value Semantics
```java
ActivityResponse r1 = new ActivityResponse(...);
ActivityResponse r2 = new ActivityResponse(...);
if (r1.equals(r2)) { // Equality by value, not identity
    // Responses have same content
}
```

### Functional Composition
```java
// Create updated context without mutation
ConversationContext updated = new ConversationContext(
    context.conversationId(),
    newSession,  // Updated
    context.serverSessionReference(),
    context.protocolVersion(),
    context.correlationId(),
    context.metadata()
);
```

## Tracing Architecture

### Correlation Chain

```
User Request
  │
  └─► Conversation ID: "conv-123"
      │
      └─► Correlation ID: "trace-uuid-456"
          │
          ├─► Request ID: "req-789"
          │   └─► Logged with trace-uuid-456
          │
          ├─► Response ID: "resp-790"
          │   └─► Logged with trace-uuid-456
          │
          └─► Event ID: "event-791"
              └─► Logged with trace-uuid-456
```

### Distributed Tracing

Each component in the system logs with:
- Correlation ID (links all operations)
- Component ID (service/bot name)
- Request/Response/Event ID (unique within scope)
- Timestamp

### Example Usage

```java
String traceId = request.conversationContext().correlationId();
logger.info("Processing request", Map.of(
    "traceId", traceId,
    "requestId", request.requestId(),
    "conversationId", request.conversationContext().conversationId()
));
```

## Protocol Versioning Strategy

### Version Format
```
Major.Minor.Patch (e.g., "1.0.0")
```

### Version Evolution

**1.0.0 → 1.0.1 (Patch)**
- Bug fixes only
- Full backward compatibility

**1.0.0 → 1.1.0 (Minor)**
- New activity types (in ActivityType enum)
- New optional metadata fields
- Backward compatible

**1.0.0 → 2.0.0 (Major)**
- Breaking changes
- Different message structure
- Requires client update

### Implementation

```java
// Server supports multiple versions
if ("1.0.0".equals(request.conversationContext().protocolVersion())) {
    handleV1(request);
} else if ("1.1.0".equals(request.conversationContext().protocolVersion())) {
    handleV1_1(request);
}

// Response uses requested or current version
String responseVersion = determineResponseVersion(request);
```

## Session State Design Patterns

### 1. Minimal State Pattern
```java
Map<String, Object> state = Map.of(
    "userId", "user123",
    "sessionStart", System.currentTimeMillis()
);
```

### 2. Rich State Pattern
```java
Map<String, Object> state = Map.of(
    "user", userObject,
    "preferences", preferencesMap,
    "conversationState", conversationStateMap,
    "customData", applicationSpecificData
);
```

### 3. JSON-Based State Pattern
```java
Map<String, Object> state = Map.of(
    "raw_json", jsonAsString  // Parse client-side only
);
```

**Key Principle:** Application code validates and interprets state. Protocol never does.

## Clean Architecture Principles

### 1. Independence
- Protocol layer doesn't depend on business logic
- Bot implementations don't break protocol
- Easy to switch between bots

### 2. Testability
- Models can be tested in isolation
- No external dependencies needed
- Immutability simplifies testing

### 3. Maintainability
- Clear separation of concerns
- Self-documenting models (records)
- Comprehensive JavaDoc

### 4. Extensibility
- Three levels of extension mechanisms
- No protocol changes needed for new types
- Backward compatible evolution

## Message Sequence Diagrams

### Basic Conversation Flow

```
┌─────────┐                              ┌─────────┐
│ Client  │                              │ Server  │
└────┬────┘                              └────┬────┘
     │                                        │
     │  1. Create ActivityRequest             │
     │     - requestId: "req-1"               │
     │     - ConversationContext              │
     │     - payload: user input              │
     │                                        │
     ├───────────────────────────────────────►│
     │                                        │
     │                    2. Process request  │
     │                       (Bot logic)      │
     │                                        │
     │  3. Create ActivityResponse            │
     │     - responseId: "resp-1"             │
     │     - activities: [...]                │
     │     - updated ConversationContext      │
     │                                        │
     │◄───────────────────────────────────────┤
     │                                        │
     │  4. Update local state                 │
     │     (from response context)            │
     │                                        │
     │  5. Render activities                  │
     │                                        │
```

### Error Handling Flow

```
┌─────────┐                              ┌─────────┐
│ Client  │                              │ Server  │
└────┬────┘                              └────┬────┘
     │                                        │
     ├───────────── ActivityRequest ─────────►│
     │                                        │
     │              Validation fails         │
     │                                        │
     │  ◄─ ActivityResponse                  │
     │          (error set, no activities)    │
     │                                        │
     │  Handle error:                         │
     │  - Show error message to user          │
     │  - Optionally show validation errors   │
     │  - Allow retry                         │
     │                                        │
```

## Performance Considerations

### Memory Efficiency
- Records are optimized by the JVM
- No object allocation overhead compared to classes
- Immutability enables escape analysis optimizations

### Network Efficiency
- Compact JSON representation (no boilerplate)
- Optional fields not serialized
- Can be gzipped for transport

### CPU Efficiency
- No synchronization needed (immutability)
- No GC pressure from mutable state
- Records have minimal method overhead

## Security Considerations

### 1. Validation
- All required fields validated in constructors
- IllegalArgumentException prevents invalid states
- Immutability prevents post-construction tampering

### 2. Sensitive Data
- Never store sensitive data in default toString()
- Audit session state handling in application code
- Use correlation IDs for audit logging

### 3. Serialization
- Ensure custom serialization handles sensitive fields
- Validate incoming data before deserialization
- Use type-safe deserialization when possible

## Integration Patterns

### With JSON Serialization

```java
// Serialize response
ObjectMapper mapper = new ObjectMapper();
String json = mapper.writeValueAsString(response);

// Deserialize request
ActivityRequest request = mapper.readValue(
    json,
    ActivityRequest.class
);
```

### With gRPC

```protobuf
// Define proto for each record
message Activity {
    string id = 1;
    string type = 2;
    google.protobuf.Struct payload = 3;
    // ... other fields
}
```

### With Database

```java
// Store conversation
conversationRepository.save(
    new ConversationEntity(
        context.conversationId(),
        mapper.writeValueAsString(context),
        System.currentTimeMillis()
    )
);
```

## Testing Strategy

### Unit Testing
```java
@Test
void testActivityValidation() {
    assertThrows(IllegalArgumentException.class,
        () -> new Activity("", ActivityType.TEXT, Map.of()));
}
```

### Integration Testing
```java
@Test
void testRequestResponseCycle() {
    ActivityRequest request = createRequest();
    ActivityResponse response = processRequest(request);
    
    assertEquals(request.requestId(), response.requestId());
    assertFalse(response.activities().isEmpty());
}
```

### Property-Based Testing
```java
@Property
void activityImmutability(Activity a1, Activity a2) {
    // If content is same, equality holds
    assertTrue(a1.equals(a2) == contentEquals(a1, a2));
}
```

## Deployment Considerations

### Versioning Strategy
- Include protocol version in responses
- Maintain backward compatibility in minor versions
- Plan migration path for major updates

### Scaling
- Stateless protocol layer enables horizontal scaling
- Session tokens can be distributed across instances
- Events can be processed asynchronously

### Monitoring
- Log all requests with correlation IDs
- Track response times and error rates
- Monitor concurrent conversation count

## Future Extensions

### Possible Enhancements
1. **Streaming Activities** - For progressive rendering
2. **Activity Constraints** - Type system for payloads
3. **Built-in Pagination** - For large result sets
4. **Optimistic Concurrency** - Version fields for updates
5. **Activity Composition** - Template-based responses

### Backward Compatibility Strategy
- Add new models as new records
- Use inheritance (java interfaces) for extensions
- Maintain existing model interfaces
- Support multiple protocol versions

---

**Architecture Document v1.0.0**  
**Last Updated: 2024**

