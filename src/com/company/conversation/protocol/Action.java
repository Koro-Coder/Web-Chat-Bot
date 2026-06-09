package com.company.conversation.protocol;

import java.util.Map;
import java.util.Optional;

/**
 * Represents an action that can be performed within a conversation.
 * 
 * Actions are used primarily in interactive activities (e.g., buttons, form submissions)
 * to specify what should happen when the user interacts with the activity. An action
 * is immutable and contains a type identifier along with optional metadata.
 * 
 * The action type should be a semantic identifier that the bot implementation understands.
 * The payload map allows arbitrary extensibility without protocol changes.
 * 
 * @param type The semantic identifier of the action (e.g., "submit", "navigate", "trigger_bot")
 * @param payload Optional metadata associated with the action. Opaque to the protocol.
 * 
 * @since 1.0.0
 */
public record Action(
        String type,
        Optional<Map<String, Object>> payload
) {
    
    /**
     * Constructs an Action with only a type identifier.
     * 
     * @param type The semantic identifier of the action
     */
    public Action(String type) {
        this(type, Optional.empty());
    }
    
    /**
     * Constructs an Action with a type and payload.
     * 
     * @param type The semantic identifier of the action
     * @param payload The action metadata, wrapped in Optional
     */
    public Action {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Action type cannot be null or blank");
        }
    }
}

