/**
 * Enterprise Conversation Protocol - Usage Guide and Examples
 * 
 * ============================================================================
 * OVERVIEW
 * ============================================================================
 * 
 * The Enterprise Conversation Protocol is a generic, lightweight framework for
 * building conversational systems. It supports multiple bot implementations
 * (search, workflow, forms, AI, approval, etc.) without encoding any business logic.
 * 
 * All models in this protocol are immutable Java 21 records with comprehensive
 * validation and extensible design.
 * 
 * ============================================================================
 * CORE MODELS
 * ============================================================================
 * 
 * 1. ActivityType - Enumeration of activity types:
 *    - TEXT, HTML, MARKDOWN: Text content in various formats
 *    - FORM: Form structure and fields
 *    - BUTTON_GROUP: Interactive buttons
 *    - IMAGE, TABLE, FILE: Specialized content types
 *    - SYSTEM: Internal system messages
 *    - CUSTOM: Extensible for custom application types
 * 
 * 2. Action - Represents user interactions:
 *    - type: Semantic identifier ("submit", "navigate", etc.)
 *    - payload: Optional action metadata
 * 
 * 3. Activity - Single unit of communication:
 *    - id: Unique activity identifier
 *    - type: ActivityType enum
 *    - payload: Type-specific content (Map&lt;String, Object&gt;)
 *    - action: Optional associated action
 *    - metadata: Optional custom metadata
 *    - correlationId: Optional tracing ID
 * 
 * 4. ClientSession - Client-side state storage:
 *    - sessionId: Unique client session identifier
 *    - state: Opaque client state map (SDK never interprets)
 *    - createdAt: Creation timestamp
 *    - metadata: Optional metadata
 * 
 * 5. ServerSessionReference - Server-side session handle:
 *    - reference: Opaque server identifier (can be token, ID, handle, etc.)
 *    - expiresAt: Optional expiration timestamp
 * 
 * 6. ConversationContext - Conversation state container:
 *    - conversationId: Unique conversation identifier
 *    - clientSession: Client session with state
 *    - serverSessionReference: Server session reference
 *    - protocolVersion: Protocol version string
 *    - correlationId: Tracing correlation ID
 *    - metadata: Optional metadata
 * 
 * 7. ActivityRequest - Client to server message:
 *    - requestId: Unique request identifier (supports idempotency)
 *    - conversationContext: Context including session info
 *    - payload: User input or action data (opaque)
 *    - metadata: Optional metadata
 * 
 * 8. ActivityResponse - Server to client message:
 *    - responseId: Unique response identifier
 *    - requestId: Corresponding request ID
 *    - conversationContext: Updated conversation context
 *    - activities: List of activities to display/process (can be empty)
 *    - error: Optional error information
 *    - metadata: Optional metadata
 * 
 * 9. ErrorResponse - Structured error reporting:
 *    - statusCode: HTTP-style status code
 *    - errorCode: Application-specific error code
 *    - message: Human-readable message
 *    - details: Optional error details/validation errors
 *    - correlationId: Optional tracing ID
 *    - metadata: Optional metadata
 * 
 * 10. ConversationEvent - Asynchronous events:
 *     - eventId: Unique event identifier
 *     - eventType: Event type string
 *     - conversationId: Associated conversation ID
 *     - timestamp: Event timestamp
 *     - payload: Event-specific data (opaque)
 *     - correlationId: Optional tracing ID
 *     - metadata: Optional metadata
 * 
 * ============================================================================
 * USAGE PATTERNS
 * ============================================================================
 * 
 * PATTERN 1: Basic Request-Response Cycle
 * ========================================
 * 
 *   // Create client session with initial state
 *   Map&lt;String, Object&gt; clientState = new HashMap&lt;&gt;();
 *   clientState.put("userId", "user123");
 *   clientState.put("context", "shopping");
 *   
 *   ClientSession session = new ClientSession(
 *       "session-abc123",
 *       clientState,
 *       System.currentTimeMillis()
 *   );
 *   
 *   // Create server session reference (opaque to client)
 *   ServerSessionReference serverRef = new ServerSessionReference("server-token-xyz");
 *   
 *   // Build conversation context
 *   ConversationContext context = new ConversationContext(
 *       "conv-001",
 *       session,
 *       serverRef,
 *       "1.0.0",
 *       "trace-id-001"
 *   );
 *   
 *   // Create user request
 *   Map&lt;String, Object&gt; requestPayload = new HashMap&lt;&gt;();
 *   requestPayload.put("text", "Show me blue shoes");
 *   requestPayload.put("source", "chat");
 *   
 *   ActivityRequest request = new ActivityRequest(
 *       "req-123",
 *       context,
 *       requestPayload
 *   );
 *   
 *   // Server processes and returns response with activities
 *   Map&lt;String, Object&gt; activityPayload = new HashMap&lt;&gt;();
 *   activityPayload.put("text", "Here are the blue shoes");
 *   activityPayload.put("itemCount", 5);
 *   
 *   Activity activity = new Activity(
 *       "activity-1",
 *       ActivityType.TEXT,
 *       activityPayload
 *   );
 *   
 *   ActivityResponse response = new ActivityResponse(
 *       "resp-123",
 *       "req-123",
 *       context,
 *       List.of(activity)
 *   );
 * 
 * 
 * PATTERN 2: Form Activity with Actions
 * ======================================
 * 
 *   // Create a form activity with a submit action
 *   Map&lt;String, Object&gt; formPayload = new HashMap&lt;&gt;();
 *   formPayload.put("form_id", "contact_form_v1");
 *   formPayload.put("fields", List.of(
 *       Map.of("name", "email", "type", "text", "required", true),
 *       Map.of("name", "message", "type", "textarea")
 *   ));
 *   
 *   Action submitAction = new Action("submit_form");
 *   
 *   Activity formActivity = new Activity(
 *       "activity-form-1",
 *       ActivityType.FORM,
 *       formPayload,
 *       Optional.of(submitAction)
 *   );
 * 
 * 
 * PATTERN 3: Multiple Activities in Single Response
 * ==================================================
 * 
 *   // Bot returns multiple activities for complex response
 *   Activity greeting = new Activity("act-1", ActivityType.TEXT,
 *       Map.of("text", "Welcome to our service"));
 *   
 *   Activity buttons = new Activity("act-2", ActivityType.BUTTON_GROUP,
 *       Map.of("buttons", List.of(
 *           Map.of("label", "Search", "id", "btn_search"),
 *           Map.of("label", "Browse", "id", "btn_browse")
 *       ))
 *   );
 *   
 *   Activity helpText = new Activity("act-3", ActivityType.MARKDOWN,
 *       Map.of("text", "Type your query or [browse results](#browse)"));
 *   
 *   ActivityResponse complexResponse = new ActivityResponse(
 *       "resp-multi",
 *       "req-multi",
 *       context,
 *       List.of(greeting, buttons, helpText)
 *   );
 * 
 * 
 * PATTERN 4: Client Session State Management
 * ===========================================
 * 
 *   // SDK maintains but never interprets client state
 *   Map&lt;String, Object&gt; clientState = new HashMap&lt;&gt;();
 *   clientState.put("preferences", Map.of(
 *       "language", "en",
 *       "theme", "dark",
 *       "timezone", "UTC"
 *   ));
 *   clientState.put("history", List.of("search1", "search2"));
 *   clientState.put("customData", "bot-specific-format");
 *   
 *   // SDK treats all of this as opaque
 *   ClientSession session = new ClientSession(
 *       "session-xyz",
 *       clientState,
 *       System.currentTimeMillis()
 *   );
 * 
 * 
 * PATTERN 5: Error Handling
 * ==========================
 * 
 *   // Server returns error response
 *   ErrorResponse error = new ErrorResponse(
 *       400,
 *       "INVALID_REQUEST",
 *       "The request format is invalid",
 *       Optional.of(Map.of(
 *           "field_errors", Map.of(
 *               "email", "Invalid email format",
 *               "age", "Age must be between 18 and 120"
 *           )
 *       )),
 *       Optional.of(traceId)
 *   );
 *   
 *   ActivityResponse errorResponse = new ActivityResponse(
 *       "resp-error",
 *       "req-123",
 *       context,
 *       List.of(), // No activities in error case
 *       Optional.of(error)
 *   );
 * 
 * 
 * PATTERN 6: Asynchronous Events
 * ===============================
 * 
 *   // Emit event when session expires
 *   ConversationEvent sessionExpiredEvent = new ConversationEvent(
 *       "event-1",
 *       "session.expired",
 *       "conv-001",
 *       System.currentTimeMillis(),
 *       Map.of("reason", "inactivity", "timeout_seconds", 1800),
 *       Optional.of("trace-001")
 *   );
 *   
 *   // Emit event when user takes action
 *   ConversationEvent userActionEvent = new ConversationEvent(
 *       "event-2",
 *       "user.action_completed",
 *       "conv-001",
 *       System.currentTimeMillis(),
 *       Map.of("action_type", "form_submission", "duration_ms", 5000)
 *   );
 * 
 * 
 * PATTERN 7: Distributed Tracing
 * ===============================
 * 
 *   // Use correlation IDs across services
 *   String traceId = UUID.randomUUID().toString();
 *   
 *   ConversationContext tracedContext = new ConversationContext(
 *       "conv-traced",
 *       session,
 *       serverRef,
 *       "1.0.0",
 *       traceId, // Shared across request/response/events
 *       Optional.of(Map.of(
 *           "service", "conversation-api",
 *           "environment", "production"
 *       ))
 *   );
 *   
 *   // Propagate trace ID through request/response cycle
 *   ActivityRequest tracedRequest = new ActivityRequest(
 *       "req-traced",
 *       tracedContext,
 *       payload
 *   );
 * 
 * 
 * ============================================================================
 * EXTENSIBILITY PATTERNS
 * ============================================================================
 * 
 * The protocol supports three levels of extensibility:
 * 
 * 1. METADATA MAPS: Most models include optional metadata Map&lt;String, Object&gt;
 *    for adding application-specific information without protocol changes.
 * 
 * 2. OPAQUE PAYLOADS: Activity, ActivityRequest, ActivityResponse, and
 *    ConversationEvent all use Map&lt;String, Object&gt; payloads that are never
 *    interpreted by the protocol layer, allowing any structure.
 * 
 * 3. CUSTOM ACTIVITY TYPES: The ActivityType.CUSTOM enum value and custom
 *    activity type strings (e.g., "custom:myformat") enable domain-specific
 *    activities without modifying the core protocol.
 * 
 * 
 * EXAMPLE: Custom Activity Type
 * ==============================
 * 
 *   // Use CUSTOM activity type for domain-specific content
 *   Activity customActivity = new Activity(
 *       "activity-custom-1",
 *       ActivityType.CUSTOM,
 *       Map.of(
 *           "customType", "chart",
 *           "chartType", "bar",
 *           "data", listOfDataPoints,
 *           "options", chartOptions
 *       ),
 *       Optional.empty(),
 *       Optional.of(Map.of(
 *           "renderer", "charts-library-v2",
 *           "theme", "dark"
 *       ))
 *   );
 * 
 * 
 * ============================================================================
 * IMMUTABILITY AND VALUE SEMANTICS
 * ============================================================================
 * 
 * All protocol models are immutable Java records. This ensures:
 * 
 * - Thread safety: No synchronization needed for concurrent access
 * - Value equality: Records are compared by their fields, not identity
 * - Copy-on-write: Update patterns create new instances without affecting originals
 * - Functional composition: Messages can be safely shared between threads
 * 
 * Example:
 * 
 *   ActivityResponse response1 = new ActivityResponse(...);
 *   ActivityResponse response2 = response1; // No copy - safe reference
 *   
 *   // Compare by value
 *   boolean equal = response1.equals(response2); // true (same values)
 * 
 * 
 * ============================================================================
 * VALIDATION AND ERROR HANDLING
 * ============================================================================
 * 
 * All records include validation in their compact constructors:
 * 
 * - Null checks for required fields
 * - Blank string checks for identifiers
 * - Range validation for numeric fields (e.g., HTTP status codes)
 * - Logical consistency checks
 * 
 * Invalid data throws IllegalArgumentException at construction time.
 * 
 * Example:
 * 
 *   // This will throw IllegalArgumentException
 *   new ErrorResponse(999, "CODE", "msg"); // 999 is invalid HTTP status
 *   
 *   // This will throw IllegalArgumentException
 *   new Activity("", ActivityType.TEXT, payload); // Empty id
 * 
 * 
 * ============================================================================
 * PROTOCOL VERSIONING
 * ============================================================================
 * 
 * The protocolVersion field in ConversationContext enables versioning:
 * 
 * - Include version in all requests and responses
 * - Servers can support multiple versions with version-specific logic
 * - Clients can adapt behavior based on server protocol version
 * 
 * Example version strings: "1.0.0", "1.0.1", "2.0.0"
 * 
 * Recommended approach:
 * - Define version as constant in application
 * - Always include version from last server response
 * - Implement server-side compatibility layer for old versions
 * 
 * 
 * ============================================================================
 * SESSION MANAGEMENT
 * ============================================================================
 * 
 * IMPORTANT: The SDK never interprets session data
 * 
 * ClientSession.state is a completely opaque Map&lt;String, Object&gt;:
 * - SDK stores it in requests/responses
 * - SDK never reads, validates, or modifies it
 * - Application defines structure and meaning
 * - Enables complete flexibility for state management
 * 
 * ServerSessionReference.reference is a completely opaque String:
 * - Could be a session ID, JWT token, handle, or any identifier
 * - SDK never validates or interprets it
 * - Server uses it for session lookup
 * - Client treats it as a black box
 * 
 * 
 * ============================================================================
 * BEST PRACTICES
 * ============================================================================
 * 
 * 1. Always include tracing/correlation IDs for debugging
 * 2. Keep request/response IDs unique within conversation (UUIDs recommended)
 * 3. Use metadata maps for application-specific extensions
 * 4. Document your payload structures (Activity.payload, etc.)
 * 5. Implement server-side version compatibility
 * 6. Validate received messages even though protocol is typed
 * 7. Use Optional for truly optional fields, not null
 * 8. Treat session data as opaque - never access ClientSession.state
 * 9. Support empty activity lists for silent responses
 * 10. Always handle errors gracefully with ErrorResponse
 * 
 * 
 * @since 1.0.0
 */
package com.company.conversation.protocol.examples;

