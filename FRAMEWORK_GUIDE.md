# Conversation Framework Guide

## Overview

The Conversation Framework is a production-grade Spring Boot 3 framework for hosting conversational applications. It provides all infrastructure required for conversation handling while maintaining complete separation from business logic.

**Key Principle:** Zero business logic. Application developers implement only the `ConversationHandler` interface.

## Quick Start

### 1. Enable the Framework

Add to your `application.yml`:

```yaml
conversation:
  enabled: true
  path: /conversation
```

Or `application.properties`:

```properties
conversation.enabled=true
conversation.path=/conversation
```

### 2. Implement ConversationHandler

```java
import com.company.conversation.protocol.ActivityRequest;
import com.company.conversation.protocol.ActivityResponse;
import com.company.conversation.protocol.Activity;
import com.company.conversation.protocol.ActivityType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Optional;

@Component
public class MyConversationHandler implements ConversationHandler {
    
    @Override
    public ActivityResponse handle(ActivityRequest request) {
        // Your business logic here
        // Could invoke AI, search, forms, workflows, etc.
        
        Activity response = new Activity(
            UUID.randomUUID().toString(),
            ActivityType.TEXT,
            Map.of("text", "Hello!")
        );
        
        return new ActivityResponse(
            UUID.randomUUID().toString(),
            request.requestId(),
            request.conversationContext(),
            List.of(response)
        );
    }
}
```

### 3. Send Requests

POST to `http://localhost:8080/conversation`:

```json
{
  "requestId": "req-123",
  "conversationContext": {
    "conversationId": "conv-456",
    "clientSession": {...},
    "serverSessionReference": {...},
    "protocolVersion": "1.0.0",
    "correlationId": "corr-789"
  },
  "payload": {
    "message": "Hello"
  }
}
```

## Configuration

### conversation.enabled
- **Type:** boolean
- **Default:** false
- **Description:** Enable or disable the framework. When false, no framework beans are registered.

### conversation.path
- **Type:** string
- **Default:** /conversation
- **Description:** REST endpoint path for conversation requests.

**Important:** When `conversation.enabled=false`, the framework startup is skipped entirely. This allows applications to include the framework without activating it immediately, enabling progressive rollout and A/B testing.

## Extension Points

The framework provides extension points for custom logic without framework modifications.

### RequestValidator

Validate incoming requests before handler invocation:

```java
import com.company.conversation.framework.engine.RequestValidator;
import com.company.conversation.framework.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class MyRequestValidator implements RequestValidator {
    
    @Override
    public void validate(ActivityRequest request) throws ValidationException {
        if (request.payload().isEmpty()) {
            throw new ValidationException("Payload cannot be empty");
        }
    }
}
```

**When it runs:** After request arrives, before handler invocation.
**Optional:** If no bean is registered, validation is skipped.

### SessionManager

Load and manage session state:

```java
import com.company.conversation.framework.engine.SessionManager;
import org.springframework.stereotype.Component;

@Component
public class MySessionManager implements SessionManager {
    
    @Override
    public void loadSession(ActivityRequest request) {
        String sessionId = request.conversationContext()
            .serverSessionReference()
            .sessionId();
        // Load session from database, cache, etc.
    }
}
```

**When it runs:** After validation, before handler invocation.
**Optional:** If no bean is registered, session loading is skipped.

### AnalyticsCollector

Collect metrics and telemetry:

```java
import com.company.conversation.framework.engine.AnalyticsCollector;
import org.springframework.stereotype.Component;

@Component
public class MyAnalyticsCollector implements AnalyticsCollector {
    
    @Override
    public void collect(ActivityRequest request, ActivityResponse response) {
        long responseSize = response.activities().stream()
            .map(a -> a.payload().toString().length())
            .reduce(0L, Long::sum);
        
        // Log analytics, send to monitoring system, etc.
    }
}
```

**When it runs:** After handler completes successfully.
**Optional:** If no bean is registered, analytics collection is skipped.

### EventPublisher

Publish events for incoming requests and outgoing responses:

```java
import com.company.conversation.framework.engine.EventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class MyEventPublisher implements EventPublisher {
    
    private final ApplicationEventPublisher eventPublisher;
    
    public MyEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    @Override
    public void publishIncoming(ActivityRequest request) {
        eventPublisher.publishEvent(new IncomingConversationEvent(request));
    }
    
    @Override
    public void publishOutgoing(ActivityResponse response) {
        eventPublisher.publishEvent(new OutgoingConversationEvent(response));
    }
}
```

**When it runs:**
- publishIncoming: After validation and session loading, before handler invocation
- publishOutgoing: After handler completes successfully

**Optional:** If no bean is registered, event publishing is skipped.

## Request Processing Order

The framework follows this processing order:

```
1. Receive HTTP POST request with ActivityRequest payload
2. Deserialize to ActivityRequest object
3. [Extension] Validate request (RequestValidator)
4. [Extension] Load session (SessionManager)
5. [Extension] Publish incoming event (EventPublisher.publishIncoming)
6. Invoke ConversationHandler.handle(request)
7. [Extension] Collect analytics (AnalyticsCollector)
8. [Extension] Publish outgoing event (EventPublisher.publishOutgoing)
9. Return HTTP response with ActivityResponse payload
10. Serialize ActivityResponse to JSON and send
```

## Dependency Injection

The framework uses **constructor injection only**. No field injection or static state.

```java
// Correct - constructor injection
@Component
public class MyConversationHandler implements ConversationHandler {
    private final SomeService service;
    
    public MyConversationHandler(SomeService service) {
        this.service = service;
    }
}

// Incorrect - field injection
@Component
public class MyConversationHandler implements ConversationHandler {
    @Autowired
    private SomeService service;  // DON'T DO THIS
}
```

## Error Handling

The framework handles errors gracefully:

- **Validation errors:** Return 500 status with error details
- **Handler exceptions:** Return 500 status with error message
- **Extension point errors:** Logged and returned as 500 status

**Best Practice:** Handlers should return errors via the `error` field in `ActivityResponse`, not throw exceptions.

```java
@Override
public ActivityResponse handle(ActivityRequest request) {
    try {
        // Process request
        return new ActivityResponse(...);
    } catch (Exception e) {
        ErrorResponse error = new ErrorResponse(
            "HANDLER_ERROR",
            e.getMessage()
        );
        
        return new ActivityResponse(
            UUID.randomUUID().toString(),
            request.requestId(),
            request.conversationContext(),
            List.of(),
            Optional.of(error)
        );
    }
}
```

## Testing

Example unit test for a custom handler:

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MyConversationHandlerTest {
    
    private MyConversationHandler handler;
    
    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        handler = new MyConversationHandler(mockService);
    }
    
    @Test
    void testHandleRequest() {
        ActivityRequest request = new ActivityRequest(...);
        ActivityResponse response = handler.handle(request);
        
        assertNotNull(response);
        assertFalse(response.activities().isEmpty());
    }
}
```

## Package Structure

The framework is organized for clarity and modularity:

```
com.company.conversation.framework/
├── handler/              # Handler interface and default impl
├── engine/              # Core orchestration engine and extension points
├── controller/          # REST controller
├── autoconfigure/       # Spring Boot auto-configuration
├── properties/          # Configuration properties
└── exception/           # Exception hierarchy
```

## Performance Considerations

- **Minimal overhead:** Framework adds only orchestration, no business logic
- **Async support:** Handlers can return responses asynchronously
- **Extension point efficiency:** Optional beans are checked at startup, not at runtime
- **Serialization:** Uses Spring's built-in Jackson JSON serialization

## Versioning

- Framework version: 1.0.0
- Protocol version: 1.0.0
- Requires Java 21+
- Requires Spring Boot 3.0+

## FAQ

**Q: Can I use this framework with my existing conversation system?**
A: Yes! The framework is agnostic to your conversation implementation. Implement `ConversationHandler` and delegate to your existing system.

**Q: How do I customize the endpoint path?**
A: Set `conversation.path` in your configuration. Default is `/conversation`.

**Q: What if I don't want to use certain extension points?**
A: They're all optional. If no bean is registered, the framework skips that step.

**Q: Can I deploy an app with the framework but have it disabled initially?**
A: Yes! Set `conversation.enabled=false` and the framework won't initialize. Change it to `true` when ready.

**Q: How do I handle long-running operations?**
A: Return an immediate response with status information, then publish updates via EventPublisher.

**Q: Can the framework handle real-time bidirectional communication?**
A: The current version is request-response only. WebSocket support is planned for future versions.

## Support

For issues, feature requests, or documentation clarifications, contact the framework team.

