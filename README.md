# Enterprise Conversation Protocol - Java 21

A generic, enterprise-grade conversational protocol for building multi-bot systems with clean architecture principles.

## Overview

This protocol provides a lightweight, immutable foundation for building conversational systems that support multiple bot implementations (search, workflow, forms, AI, approval, etc.) without encoding any business logic.

**Key Features:**
- ✅ Generic design - no business-specific concepts
- ✅ Immutable models using Java 21 records
- ✅ Dynamic payloads for extensibility
- ✅ Multiple activities per response
- ✅ Opaque session management
- ✅ Distributed tracing support
- ✅ Comprehensive error handling
- ✅ Full JavaDoc documentation

## Package Structure

```
com.company.conversation.protocol/
├── ActivityType.java                 # Enum of activity types
├── Activity.java                     # Single unit of communication
├── Action.java                       # User action/interaction
├── ActivityRequest.java              # Client to server message
├── ActivityResponse.java             # Server to client message
├── ConversationContext.java          # Conversation state container
├── ClientSession.java                # Client-side session state
├── ServerSessionReference.java       # Server-side session handle
├── ConversationEvent.java            # Asynchronous events
├── ErrorResponse.java                # Structured error reporting
├── package-info.java                 # Package documentation
└── examples/
    └── ProtocolExamples.java         # Usage examples
```

## Core Models

### ActivityType Enum
Supports 10 predefined activity types:
- **TEXT** - Plain text content
- **HTML** - HTML-formatted content
- **MARKDOWN** - Markdown-formatted content
- **FORM** - Form with fields and validation
- **BUTTON_GROUP** - Interactive buttons
- **IMAGE** - Image reference or embedded image
- **TABLE** - Tabular data
- **FILE** - File reference/download
- **SYSTEM** - Internal system messages
- **CUSTOM** - Extension point for custom types

### Activity Record
Represents a single unit of communication with:
- Unique `id`
- `type` (ActivityType)
- `payload` (Map<String, Object> - opaque)
- Optional `action` (for interactive elements)
- Optional `metadata` (for extensibility)
- Optional `correlationId` (for tracing)

```java
Activity textActivity = new Activity(
    "activity-1",
    ActivityType.TEXT,
    Map.of("text", "Hello, world!")
);
```

### Action Record
Represents user interactions with:
- Semantic `type` (e.g., "submit", "navigate")
- Optional opaque `payload`

```java
Action submitAction = new Action(
    "submit_form",
    Optional.of(Map.of("endpoint", "/api/form/submit"))
);
```

### ActivityRequest Record
Client to server message containing:
- Unique `requestId` (for idempotency)
- `conversationContext`
- Opaque `payload`
- Optional `metadata`

```java
ActivityRequest request = new ActivityRequest(
    UUID.randomUUID().toString(),
    context,
    Map.of("userInput", "show me results")
);
```

### ActivityResponse Record
Server to client message containing:
- Unique `responseId`
- Corresponding `requestId`
- Updated `conversationContext`
- List of `activities` (can be multiple)
- Optional `error` (for error responses)
- Optional `metadata`

```java
ActivityResponse response = new ActivityResponse(
    "resp-123",
    "req-123",
    updatedContext,
    List.of(activity1, activity2, activity3)
);
```

### ConversationContext Record
Maintains conversation state across requests:
- Unique `conversationId`
- `clientSession` (opaque client state)
- `serverSessionReference` (opaque server handle)
- `protocolVersion` (for versioning)
- `correlationId` (for tracing)
- Optional `metadata`

```java
ConversationContext context = new ConversationContext(
    "conv-123",
    clientSession,
    serverSessionReference,
    "1.0.0",
    UUID.randomUUID().toString()
);
```

### ClientSession Record
Stores client-side state (opaque to SDK):
- `sessionId`
- `state` - Map<String, Object> (never interpreted by SDK)
- `createdAt` - Creation timestamp
- Optional `metadata`

**Important:** The SDK never interprets or validates the state Map. This enables complete flexibility.

```java
ClientSession session = new ClientSession(
    "session-123",
    Map.of("userId", "user456", "preferences", prefs),
    System.currentTimeMillis()
);
```

### ServerSessionReference Record
Opaque server session handle:
- `reference` - Can be token, ID, handle, or any identifier
- Optional `expiresAt` - Expiration timestamp

**Important:** The SDK never interprets this reference. It's a black box for the server.

```java
ServerSessionReference serverRef = new ServerSessionReference(
    "server-token-xyz",
    Optional.of(System.currentTimeMillis() + 3600000)
);
```

### ErrorResponse Record
Structured error reporting:
- HTTP-style `statusCode`
- Application `errorCode`
- Human-readable `message`
- Optional `details` (e.g., validation errors)
- Optional `correlationId` (for tracing)
- Optional `metadata`

```java
ErrorResponse error = new ErrorResponse(
    400,
    "VALIDATION_ERROR",
    "Form validation failed",
    Optional.of(Map.of("fields", fieldErrors)),
    Optional.of(traceId)
);
```

### ConversationEvent Record
Asynchronous event notification:
- Unique `eventId`
- `eventType` (e.g., "conversation.started")
- Associated `conversationId`
- Event `timestamp`
- Opaque `payload`
- Optional `correlationId`
- Optional `metadata`

```java
ConversationEvent event = new ConversationEvent(
    "event-123",
    "conversation.ended",
    "conv-456",
    System.currentTimeMillis(),
    Map.of("reason", "user_disconnect")
);
```

## Usage Patterns

### Pattern 1: Basic Request-Response

```java
// Create session and context
ClientSession session = new ClientSession("session-1", state, now);
ServerSessionReference serverRef = new ServerSessionReference("token");
ConversationContext context = new ConversationContext(
    "conv-1", session, serverRef, "1.0.0", traceId
);

// Create request
ActivityRequest request = new ActivityRequest(
    UUID.randomUUID().toString(),
    context,
    Map.of("text", "user input")
);

// Create response with activities
Activity activity = new Activity(
    "act-1",
    ActivityType.TEXT,
    Map.of("text", "bot response")
);
ActivityResponse response = new ActivityResponse(
    "resp-1",
    request.requestId(),
    context,
    List.of(activity)
);
```

### Pattern 2: Multiple Activities

```java
ActivityResponse response = new ActivityResponse(
    "resp-multi",
    "req-multi",
    context,
    List.of(
        new Activity("act-1", ActivityType.TEXT, greeting),
        new Activity("act-2", ActivityType.BUTTON_GROUP, buttons),
        new Activity("act-3", ActivityType.MARKDOWN, helpText)
    )
);
```

### Pattern 3: Form with Actions

```java
Activity form = new Activity(
    "form-1",
    ActivityType.FORM,
    Map.of("fields", formFields),
    Optional.of(new Action("submit_form"))
);
```

### Pattern 4: Error Response

```java
ErrorResponse error = new ErrorResponse(
    400,
    "INVALID_INPUT",
    "Invalid request format"
);
ActivityResponse errorResponse = new ActivityResponse(
    "resp-err",
    "req-123",
    context,
    List.of(),  // No activities in error case
    Optional.of(error)
);
```

### Pattern 5: Custom Activity Types

```java
Activity customChart = new Activity(
    "chart-1",
    ActivityType.CUSTOM,
    Map.of(
        "customType", "bar_chart",
        "data", chartData
    )
);
```

### Pattern 6: Distributed Tracing

```java
String traceId = UUID.randomUUID().toString();
ConversationContext context = new ConversationContext(
    "conv-1", session, serverRef, "1.0.0", traceId,
    Optional.of(Map.of("service", "bot", "environment", "prod"))
);
// Propagate traceId through all requests/responses
```

## Extensibility

The protocol supports three levels of extensibility:

### 1. Metadata Maps
Most models include optional `metadata` parameter for custom fields.

### 2. Opaque Payloads
Activities, requests, responses, and events use `Map<String, Object>` payloads that are never interpreted by the protocol.

### 3. Custom Activity Types
Use `ActivityType.CUSTOM` with custom type strings like `"custom:mychart"`.

## Design Principles

1. **Immutability** - All models are Java 21 records (no mutations)
2. **Opaque Sessions** - SDK never interprets client or server session data
3. **Generic Design** - No business-specific concepts in protocol
4. **Extensibility** - Metadata and payloads enable additions without changes
5. **Tracing** - Correlation IDs for distributed tracing support
6. **Validation** - Comprehensive checks in record constructors
7. **Value Semantics** - Records support equality by value
8. **Clean Architecture** - Protocol layer is independent of business logic

## Compilation and Execution

### Compile
```bash
javac -d bin src/module-info.java \
  src/com/company/conversation/protocol/*.java \
  src/com/company/conversation/protocol/examples/*.java
```

### Run Examples
```bash
java -cp bin com.company.conversation.protocol.examples.ProtocolExamples
```

### Requirements
- Java 21 or later (for records)
- No external dependencies

## Session Management Best Practices

### ClientSession.state
- **Never** interpret or validate the state by the protocol/SDK
- The application defines structure and meaning
- Enables complete flexibility for state management
- Can contain any serializable data

### ServerSessionReference.reference
- **Never** validate or interpret by the client
- Can be token, ID, handle, or custom identifier
- Treat as a black box on the client side
- Server uses it for session lookup

## Error Handling

All records validate inputs in their compact constructors:
- Required fields checked for null/blank
- Numeric ranges validated (e.g., HTTP status codes 100-599)
- Logical consistency checked

Throws `IllegalArgumentException` for invalid data.

## Protocol Versioning

The `protocolVersion` field enables versioning:
- Include version in all requests/responses
- Servers can support multiple versions
- Recommended format: "major.minor.patch" (e.g., "1.0.0")

## Correlation IDs and Tracing

For distributed tracing:
- Use `ConversationContext.correlationId()` to trace conversations
- Include correlation ID in events and errors
- Propagate through all layers for end-to-end tracing

## Examples

See `ProtocolExamples.java` for 10 comprehensive examples:

1. Basic conversation
2. Button interactions
3. Form submissions
4. Multi-activity responses
5. Error handling
6. Session state management
7. Distributed tracing
8. Conversation events
9. Custom activity types
10. Idempotent requests

Run with:
```bash
java -cp bin com.company.conversation.protocol.examples.ProtocolExamples
```

## Documentation

Every model includes comprehensive JavaDoc:
- Purpose and use cases
- Field descriptions
- Constructor documentation
- Usage examples in comments

Access full documentation in:
- `package-info.java` - Package overview
- `PROTOCOL_GUIDE.java` - Detailed protocol guide
- Model Java files - Individual model documentation

## Thread Safety

All records are immutable and thread-safe:
- No synchronization needed
- Safe to share between threads
- No copy-on-write needed for concurrent access

## Version History

- **1.0.0** - Initial protocol definition
  - Core models and records
  - 10 activity types
  - Full tracing support
  - Opaque session management
  - Extensibility framework

## License

[Your License Here]

## Support

For issues, questions, or contributions, please contact the development team.

---

**Built with Java 21 • Enterprise-grade • Clean Architecture • No Business Logic**

#   W e b - C h a t - B o t  
 