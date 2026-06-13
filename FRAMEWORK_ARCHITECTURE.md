# Conversation Framework Architecture
## Design Principles
The Conversation Framework follows these core architectural principles:
1. **Zero Business Logic**: Framework contains only infrastructure and orchestration
2. **Single Responsibility**: Each component has one clear purpose
3. **Open/Closed Principle**: Open for extension via interfaces, closed for modification
4. **Dependency Injection**: All dependencies injected via constructor
5. **Protocol Immutability**: All protocol objects are immutable Java records
6. **Optional Extensions**: All extension points are optional and gracefully skipped if not provided
## Component Architecture
### High-Level Flow
```
HTTP Request
    ↓
ConversationController
    ↓
ConversationEngine
    ├→ [Extension] RequestValidator
    ├→ [Extension] SessionManager
    ├→ [Extension] EventPublisher (incoming)
    ├→ ConversationHandler (business logic)
    ├→ [Extension] AnalyticsCollector
    └→ [Extension] EventPublisher (outgoing)
    ↓
HTTP Response
```
## Core Components
### 1. ConversationController
- REST endpoint exposure
- Maps to configurable path via conversation.path property
- Delegates all processing to ConversationEngine
### 2. ConversationEngine
- Pure orchestration and extension point coordination
- Calls extension points in order
- Invokes ConversationHandler for business logic
- Handles errors gracefully
### 3. ConversationHandler
- Application business logic implementation
- Framework-agnostic
- Can invoke AI, search, forms, workflows, etc.
### 4. Extension Point Interfaces
- RequestValidator: Request validation
- SessionManager: Session loading
- AnalyticsCollector: Metrics collection
- EventPublisher: Event notification
## Thread Safety
Stateless components throughout. Multiple concurrent requests supported. Horizontal scalability enabled.
## Configuration
ConversationProperties provides:
- conversation.enabled (default: false)
- conversation.path (default: /conversation)
Auto-configuration enables framework beans only when conversation.enabled=true.
See FRAMEWORK_ARCHITECTURE_DETAILED.md for full architecture documentation.
