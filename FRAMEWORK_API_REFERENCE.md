# Conversation Framework API Reference
## Core Interfaces
### ConversationHandler
**Package:** com.company.conversation.framework.handler
**Purpose:** Primary extension point for application business logic.
**Method:**
```java
ActivityResponse handle(ActivityRequest request)
```
**Parameters:**
- request: ActivityRequest containing client input and context
**Returns:** ActivityResponse with activities, context, and optional error
**Example:**
```java
@Component
public class MyHandler implements ConversationHandler {
    @Override
    public ActivityResponse handle(ActivityRequest request) {
        Activity response = new Activity(
            UUID.randomUUID().toString(),
            ActivityType.TEXT,
            Map.of("text", "Response")
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
## Extension Point Interfaces
### RequestValidator
**Package:** com.company.conversation.framework.engine
**Purpose:** Validate requests before handler invocation.
**Method:**
```java
void validate(ActivityRequest request) throws ValidationException
```
**Throws:** ValidationException for invalid requests
**Optional:** Yes - if no bean registered, validation skipped
**Registration:**
```java
@Component
public class MyValidator implements RequestValidator {
    @Override
    public void validate(ActivityRequest request) throws ValidationException {
        if (request.payload().isEmpty()) {
            throw new ValidationException("Payload empty");
        }
    }
}
```
### SessionManager
**Package:** com.company.conversation.framework.engine
**Purpose:** Load session information before handler invocation.
**Method:**
```java
void loadSession(ActivityRequest request)
```
**Optional:** Yes - if no bean registered, session loading skipped
**Registration:**
```java
@Component
public class MySessionManager implements SessionManager {
    @Override
    public void loadSession(ActivityRequest request) {
        String sessionId = request.conversationContext()
            .serverSessionReference()
            .sessionId();
        // Load session
    }
}
```
### AnalyticsCollector
**Package:** com.company.conversation.framework.engine
**Purpose:** Collect metrics after handler completes.
**Method:**
```java
void collect(ActivityRequest request, ActivityResponse response)
```
**Optional:** Yes - if no bean registered, analytics skipped
**Registration:**
```java
@Component
public class MyAnalytics implements AnalyticsCollector {
    @Override
    public void collect(ActivityRequest request, ActivityResponse response) {
        long activityCount = response.activities().size();
        // Log analytics
    }
}
```
### EventPublisher
**Package:** com.company.conversation.framework.engine
**Purpose:** Publish events for incoming and outgoing activities.
**Methods:**
```java
void publishIncoming(ActivityRequest request)
void publishOutgoing(ActivityResponse response)
```
**Optional:** Yes - if no bean registered, events not published
**Registration:**
```java
@Component
public class MyPublisher implements EventPublisher {
    private ApplicationEventPublisher eventPublisher;
    public MyPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    @Override
    public void publishIncoming(ActivityRequest request) {
        eventPublisher.publishEvent(new IncomingEvent(request));
    }
    @Override
    public void publishOutgoing(ActivityResponse response) {
        eventPublisher.publishEvent(new OutgoingEvent(response));
    }
}
```
## Configuration
### ConversationProperties
**Package:** com.company.conversation.framework.properties
**Properties:**
- conversation.enabled (boolean, default: false)
- conversation.path (string, default: /conversation)
**Usage:**
```yaml
conversation:
  enabled: true
  path: /api/conversation
```
## REST Endpoint
### POST /conversation
**Content-Type:** application/json
**Request Body:** ActivityRequest
**Response:** ActivityResponse with HTTP 200 or 500
**Example Request:**
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
**Example Response:**
```json
{
  "responseId": "resp-111",
  "requestId": "req-123",
  "conversationContext": {...},
  "activities": [
    {
      "id": "act-222",
      "type": "TEXT",
      "payload": {
        "text": "Hello!"
      }
    }
  ],
  "error": null
}
```
## Exception Classes
### ConversationException
**Package:** com.company.conversation.framework.exception
Base exception for framework errors.
```java
public ConversationException(String message)
public ConversationException(String message, Throwable cause)
```
### HandlerInvocationException
**Package:** com.company.conversation.framework.exception
Thrown when handler processing fails.
```java
throw new HandlerInvocationException("Processing failed", cause);
```
### ValidationException
**Package:** com.company.conversation.framework.exception
Thrown when request validation fails.
```java
throw new ValidationException("Invalid input");
```
## Classes and Beans
### ConversationEngine
**Package:** com.company.conversation.framework.engine
Core orchestration engine.
```java
ActivityResponse process(ActivityRequest request)
```
**Auto-registered:** Yes, when conversation.enabled=true
### ConversationController
**Package:** com.company.conversation.framework.controller
REST controller.
```java
ResponseEntity<ActivityResponse> handle(ActivityRequest request)
```
**Auto-registered:** Yes, when conversation.enabled=true
**Endpoint:** Mapped to ${conversation.path:/conversation}
### DefaultConversationHandler
**Package:** com.company.conversation.framework.handler
Default no-op handler implementation.
**Registration:** Automatically registered via @ConditionalOnMissingBean
**Response:** Returns message "No handler configured"
## Bean Registration Order
When conversation.enabled=true:
1. ConversationProperties loaded from configuration
2. DefaultConversationHandler registered (if no custom handler)
3. ConversationEngine created with handler
4. ConversationController created with engine
5. Optional extension points discovered
## Logging
The framework uses SLF4J for logging.
**Logger Names:**
- com.company.conversation.framework.controller
- com.company.conversation.framework.engine
- com.company.conversation.framework.autoconfigure
- com.company.conversation.framework.handler
**Typical Log Levels:**
- DEBUG: Request/response processing
- INFO: Component registration
- WARN: Validation failures
- ERROR: Exceptions and failures
**Example Configuration (logback.xml):**
```xml
<logger name="com.company.conversation.framework" level="DEBUG"/>
```
## Dependency Injection Examples
### Injecting Handler
```java
@RestController
public class CustomController {
    private final ConversationHandler handler;
    public CustomController(ConversationHandler handler) {
        this.handler = handler;
    }
}
```
### Injecting Engine
```java
@Component
public class CustomService {
    private final ConversationEngine engine;
    public CustomService(ConversationEngine engine) {
        this.engine = engine;
    }
}
```
### Injecting Properties
```java
@Component
public class ConfigConsumer {
    private final ConversationProperties properties;
    public ConfigConsumer(ConversationProperties properties) {
        this.properties = properties;
    }
}
```
## Error Handling Patterns
### Validation Error
```java
if (!isValid(request)) {
    throw new ValidationException("Invalid request format");
}
```
### Handler Error
```java
try {
    return handler.handle(request);
} catch (Exception e) {
    throw new HandlerInvocationException("Process failed", e);
}
```
### Extension Point Error
Automatically caught and logged by engine.
### Response Error
```java
return new ActivityResponse(
    responseId,
    requestId,
    context,
    activities,
    Optional.of(new ErrorResponse("ERROR_CODE", "Error message"))
);
```
## Testing Patterns
### Unit Test Handler
```java
@Test
void testHandler() {
    ConversationHandler handler = new MyHandler();
    ActivityRequest request = buildRequest();
    ActivityResponse response = handler.handle(request);
    assertNotNull(response);
}
```
### Integration Test with Framework
```java
@SpringBootTest
public class FrameworkIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Test
    void testConversationEndpoint() throws Exception {
        mockMvc.perform(post("/conversation")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isOk());
    }
}
```
## Configuration Examples
### Minimal Setup
```yaml
conversation:
  enabled: true
```
### Custom Path
```yaml
conversation:
  enabled: true
  path: /api/v1/conversation
```
### Disabled (No Framework)
```yaml
conversation:
  enabled: false
```
### Environment-Specific
Dev (application-dev.yml):
```yaml
conversation:
  enabled: true
  path: /dev/conversation
```
Prod (application-prod.yml):
```yaml
conversation:
  enabled: true
  path: /conversation
```
## Versioning
- Framework Version: 1.0.0
- Protocol Version: 1.0.0
- Java Requirement: 21+
- Spring Boot Requirement: 3.0+
## Related Documentation
- FRAMEWORK_GUIDE.md - Usage guide and examples
- FRAMEWORK_ARCHITECTURE.md - Architecture and design
- README.md - Project overview
