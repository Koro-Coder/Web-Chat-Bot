package com.company.conversation.framework.properties;
import org.springframework.boot.context.properties.ConfigurationProperties;
/**
 * Configuration properties for the conversation framework.
 * 
 * ConversationProperties defines the configuration settings for the conversation
 * framework, allowing users to enable/disable the framework and customize the
 * REST endpoint path through application properties or YAML configuration.
 * 
 * Configuration keys:
 * - conversation.enabled: Enable or disable the framework (default: false)
 * - conversation.path: REST endpoint path (default: /conversation)
 * 
 * @since 1.0.0
 */
@ConfigurationProperties(prefix = "conversation")
public class ConversationProperties {
    private boolean enabled = false;
    private String path = "/conversation";
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
}

