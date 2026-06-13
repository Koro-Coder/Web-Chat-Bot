/**
 * Enterprise-grade conversational protocol package.
 * 
 * This package contains immutable model classes that define a generic conversation protocol
 * supporting multiple bot implementations. The protocol is agnostic to business logic and
 * bot-specific concepts; it provides a foundation for building conversational systems.
 * 
 * <h2>Core Concepts</h2>
 * 
 * <strong>Activities:</strong> Units of communication sent between client and server.
 * Activities represent content to be displayed (TEXT, FORM, BUTTON_GROUP, etc.) or
 * processed (SYSTEM events). Each activity is typed and contains an opaque payload.
 * 
 * <strong>Requests and Responses:</strong> ActivityRequest encapsulates a client message
 * or action. ActivityResponse contains one or more activities to display and updated
 * conversation context.
 * 
 * <strong>Session Management:</strong> ClientSession maintains opaque client-side state
 * that the SDK stores but does not interpret. ServerSessionReference is an opaque handle
 * for server-side session tracking.
 * 
 * <strong>Context and Tracing:</strong> ConversationContext maintains conversation state
 * including protocol versioning and correlation IDs for distributed tracing.
 * 
 * <strong>Events:</strong> ConversationEvent represents asynchronous or out-of-band
 * notifications about conversation state changes.
 * 
 *  <strong>Errors:</strong> ErrorResponse provides structured error reporting with
 *  HTTP-style status codes and application-specific error codes.
 * 
 *  <strong>Execution Context:</strong> ConversationExecutionContext is a framework-managed
 *  interface supplied to handlers containing sessions, attributes, and operational context.
 *  This enables the framework to inject configuration, feature flags, and helper data without
 *  requiring future changes to the handler interface.
 * 
 *  <h2>Design Principles</h2>
 * 
 * <ul>
 *   <li><strong>Immutability:</strong> All models are implemented as Java records (Java 21+),
 *       ensuring immutability and value semantics.</li>
 *   <li><strong>Opaque Payloads:</strong> Payloads are modeled as Map&lt;String, Object&gt;
 *       to support arbitrary extensibility without protocol changes.</li>
 *   <li><strong>Generic Design:</strong> No business-specific concepts (search, intent, LLM, etc.)
 *       are included in the protocol layer.</li>
 *   <li><strong>Extensibility:</strong> Most models include optional metadata maps for
 *       adding new information without breaking existing implementations.</li>
 *   <li><strong>Tracing Support:</strong> Correlation IDs and request IDs enable
 *       distributed tracing and debugging.</li>
 *   <li><strong>Session Opacity:</strong> Session data is treated as opaque - the SDK
 *       never interprets client or server session information.</li>
 * </ul>
 * 
 * <h2>Typical Message Flow</h2>
 * 
 * <ol>
 *   <li>Client creates an ActivityRequest with payload containing user input</li>
 *   <li>Request includes ConversationContext with current session information</li>
 *   <li>Server processes the request (bot-specific logic)</li>
 *   <li>Server creates an ActivityResponse containing one or more Activity objects</li>
 *   <li>Response updates the ConversationContext with any session state changes</li>
 *   <li>Client displays activities and updates local session state</li>
 * </ol>
 * 
 * <h2>Version History</h2>
 * <ul>
 *   <li>1.0.0 - Initial protocol definition</li>
 * </ul>
 * 
 * @since 1.0.0
 */
package com.company.conversation.protocol;

