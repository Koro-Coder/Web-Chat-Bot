package com.company.conversation.protocol;

/**
 * Enumeration of supported activity types in the conversation protocol.
 * 
 * Activities represent units of communication that can be sent between client and server
 * in a conversation. Each type defines a different form of content presentation.
 * 
 * @since 1.0.0
 */
public enum ActivityType {
    /**
     * Plain text activity. Payload contains unformatted text content.
     */
    TEXT,
    
    /**
     * HTML-formatted activity. Payload contains HTML markup for rich text rendering.
     */
    HTML,
    
    /**
     * Markdown-formatted activity. Payload contains markdown syntax for structured text.
     */
    MARKDOWN,
    
    /**
     * Form activity. Payload contains form structure and field definitions.
     */
    FORM,
    
    /**
     * Button group activity. Payload contains buttons for user interaction and selection.
     */
    BUTTON_GROUP,
    
    /**
     * Image activity. Payload contains image reference or binary data.
     */
    IMAGE,
    
    /**
     * Table activity. Payload contains tabular data with rows and columns.
     */
    TABLE,
    
    /**
     * File activity. Payload contains file metadata or downloadable file reference.
     */
    FILE,
    
    /**
     * System activity. Used for internal system messages and control flow events.
     */
    SYSTEM,
    
    /**
     * Custom activity type. Used for extensibility when standard types are insufficient.
     * Custom types should be namespaced with a prefix, e.g., "custom:mytype".
     */
    CUSTOM
}


