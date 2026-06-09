# Quick Start Guide - Enterprise Conversation Protocol

## 5-Minute Setup

### 1. Clone/Setup Project

```bash
git clone <repository>
cd Web-Chat-Bot
```

### 2. Compile

**Using command line:**
```bash
javac -d bin src/module-info.java \
  src/com/company/conversation/protocol/*.java \
  src/com/company/conversation/protocol/examples/*.java
```

**Using Maven:**
```bash
mvn clean compile
```

**Using Gradle:**
```bash
gradle build
```

### 3. Run Examples

**Command line:**
```bash
java -cp bin com.company.conversation.protocol.examples.ProtocolExamples
```

**Maven:**
```bash
mvn exec:java -Dexec.mainClass="com.company.conversation.protocol.examples.ProtocolExamples"
```

**Gradle:**
```bash
gradle runExamples
```

## Basic Usage - 5 Lines of Code

```java
// 1. Create a session
ClientSession session = new ClientSession("session-1", 
    Map.of("userId", "user123"), 
    System.currentTimeMillis());

// 2. Create context
ConversationContext context = new ConversationContext(
    "conversation-1",
    session,
    new ServerSessionReference("server-token"),
    "1.0.0",
    UUID.randomUUID().toString()
);

// 3. Create request
ActivityRequest request = new ActivityRequest(
    UUID.randomUUID().toString(),
    context,
    Map.of("text", "Hello bot!")
);

// 4. Create response with activity
Activity activity = new Activity(
    "activity-1",
    ActivityType.TEXT,
    Map.of("text", "Hello user!")
);

// 5. Create response
ActivityResponse response = new ActivityResponse(
    UUID.randomUUID().toString(),
    request.requestId(),
    context,
    List.of(activity)
);
```

## Common Patterns

### Pattern 1: Text Response

```java
Activity greeting = new Activity(
    "greeting-1",
    ActivityType.TEXT,
    Map.of("text", "What can I help you with?")
);

ActivityResponse response = new ActivityResponse(
    "resp-1",
    "req-1",
    context,
    List.of(greeting)
);
```

### Pattern 2: Button Menu

```java
Activity buttons = new Activity(
    "menu-1",
    ActivityType.BUTTON_GROUP,
    Map.of("buttons", List.of(
        Map.of("id", "opt1", "label", "Option 1"),
        Map.of("id", "opt2", "label", "Option 2")
    )),
    Optional.of(new Action("select_option"))
);
```

### Pattern 3: Form

```java
Activity form = new Activity(
    "form-1",
    ActivityType.FORM,
    Map.of(
        "fields", List.of(
            Map.of("name", "email", "type", "email", "required", true),
            Map.of("name", "message", "type", "textarea")
        )
    ),
    Optional.of(new Action("submit_form"))
);
```

### Pattern 4: Error Handling

```java
ErrorResponse error = new ErrorResponse(
    400,
    "INVALID_INPUT",
    "Please check your input",
    Optional.of(Map.of("fields", fieldErrors))
);

ActivityResponse errorResp = new ActivityResponse(
    "resp-err",
    "req-1",
    context,
    List.of(),
    Optional.of(error)
);
```

### Pattern 5: Multiple Activities

```java
List<Activity> activities = List.of(
    new Activity("a1", ActivityType.TEXT, 
        Map.of("text", "Welcome!")),
    new Activity("a2", ActivityType.BUTTON_GROUP,
        Map.of("buttons", buttons)),
    new Activity("a3", ActivityType.MARKDOWN,
        Map.of("text", "Choose an option above"))
);

ActivityResponse response = new ActivityResponse(
    "resp-multi",
    "req-1",
    context,
    activities
);
```

## File Structure

```
Web-Chat-Bot/
├── src/
│   ├── module-info.java                           # Module definition
│   ├── com/company/conversation/protocol/
│   │   ├── ActivityType.java                      # Enum
│   │   ├── Activity.java                          # Main record
│   │   ├── Action.java                            # Action record
│   │   ├── ActivityRequest.java                   # Request
│   │   ├── ActivityResponse.java                  # Response
│   │   ├── ConversationContext.java               # Context
│   │   ├── ClientSession.java                     # Client session
│   │   ├── ServerSessionReference.java            # Server session
│   │   ├── ConversationEvent.java                 # Events
│   │   ├── ErrorResponse.java                     # Error
│   │   ├── package-info.java                      # Package docs
│   │   ├── PROTOCOL_GUIDE.java                    # Usage guide
│   │   └── examples/
│   │       └── ProtocolExamples.java              # Examples
│   └── Main.java                                  # Starter template
├── bin/                                           # Compiled output
├── pom.xml                                        # Maven config
├── build.gradle                                   # Gradle config
├── README.md                                      # Main documentation
├── ARCHITECTURE.md                                # Architecture guide
└── QUICK_START.md                                 # This file
```

## Key Concepts

### 1. Records (Immutable)
All protocol models are Java 21 records:
```java
record Activity(
    String id,
    ActivityType type,
    Map<String, Object> payload,
    ...
) {
    // Validation in compact constructor
    public Activity { ... }
}
```

### 2. Opaque Session State
The SDK never interprets `ClientSession.state`:
```java
// Application defines structure
Map<String, Object> state = Map.of(
    "userId", "u123",
    "prefs", prefsMap
);

// SDK just stores and exchanges it
ClientSession session = new ClientSession("session-id", state, now);

// Application code reads directly (SDK never accesses)
String userId = (String) session.state().get("userId");
```

### 3. Optional Fields
Most optional fields use Java's `Optional`:
```java
Activity activity = new Activity(
    "id-1",
    ActivityType.TEXT,
    payload,
    Optional.of(action),    // Optional
    Optional.empty(),       // No metadata
    Optional.of(traceId)   // Optional
);
```

### 4. Tracing
Use correlation IDs for distributed tracing:
```java
String traceId = UUID.randomUUID().toString();

// Include in context
ConversationContext context = new ConversationContext(
    ..., traceId, ...
);

// Will be in all requests/responses/events
// for end-to-end tracing
```

## Common Mistakes to Avoid

### ❌ Don't mutate records
```java
// WRONG - records are immutable
activity.payload().put("key", "value");
```

### ✅ Do create new records
```java
// RIGHT - create new instance
Activity updated = new Activity(
    activity.id(),
    activity.type(),
    newPayload  // Updated payload
);
```

### ❌ Don't interpret session state
```java
// WRONG - SDK should never do this
String userId = (String) session.state().get("userId");  // In SDK code
```

### ✅ Do let application handle it
```java
// RIGHT - Application code interprets state
String userId = (String) session.state().get("userId");  // In app code
```

### ❌ Don't omit correlation IDs
```java
// WRONG - Difficult to trace
new Activity("id", type, payload);
```

### ✅ Do include tracing info
```java
// RIGHT - Traceable
new Activity(
    "id", type, payload,
    Optional.empty(),
    Optional.empty(),
    Optional.of(traceId)
);
```

## Validation Rules

All records validate inputs. Common validations:

| Field | Validation |
|-------|-----------|
| `id` | Must not be null or blank |
| `type` | Must not be null |
| `status` | Must be 100-599 (HTTP codes) |
| `timestamp` | Must be >= 0 |
| `payload` | Must not be null |

Invalid data throws `IllegalArgumentException`.

## Next Steps

1. **Review** Package documentation: `package-info.java`
2. **Read** Architecture guide: `ARCHITECTURE.md`
3. **Study** Examples: `ProtocolExamples.java`
4. **Explore** Full usage guide: `PROTOCOL_GUIDE.java`
5. **Implement** Your first bot using the protocol

## Testing Your Code

### Create a simple test:

```java
@Test
void testBasicConversation() {
    // Create session
    ClientSession session = new ClientSession(
        "test-session",
        Map.of(),
        System.currentTimeMillis()
    );
    
    // Create context
    ConversationContext context = new ConversationContext(
        "test-conv",
        session,
        new ServerSessionReference("test-token"),
        "1.0.0",
        "test-trace"
    );
    
    // Create request
    ActivityRequest request = new ActivityRequest(
        "test-req",
        context,
        Map.of("text", "test")
    );
    
    // Verify
    assertNotNull(request);
    assertEquals("test-req", request.requestId());
}
```

## Troubleshooting

### Compilation error: "records not supported"
- **Solution:** Ensure Java 21+
  ```bash
  javac -version  # Should show 21 or higher
  ```

### ClassNotFoundException
- **Solution:** Ensure classpath includes bin directory
  ```bash
  java -cp bin:. com.example.Main
  ```

### No activities displayed
- **Solution:** Check Activity type is correct
  ```java
  // Verify type is supported
  if (ActivityType.CUSTOM.equals(activity.type())) {
      // Handle custom rendering
  }
  ```

## Support & Resources

- **Examples:** Run `ProtocolExamples.java`
- **Documentation:** See `README.md` and `ARCHITECTURE.md`
- **Usage Guide:** See `PROTOCOL_GUIDE.java`
- **API Docs:** View JavaDoc in model files

## IDE Setup

### IntelliJ IDEA
1. Open project
2. Mark `src/` as Sources Root
3. Mark `bin/` as Output directory
4. File → Project Structure → SDK → Java 21
5. Build → Build Project

### Eclipse
1. Import as existing Java project
2. Configure Build Path → Source → Add src/
3. Configure Build Path → Libraries → Add JRE (Java 21)
4. Project → Build

### VS Code
1. Install Extension Pack for Java
2. Open in VS Code
3. Trust workspace
4. Should auto-configure Java 21

---

**Ready to build conversational systems? Start with a simple text activity and expand from there!**

