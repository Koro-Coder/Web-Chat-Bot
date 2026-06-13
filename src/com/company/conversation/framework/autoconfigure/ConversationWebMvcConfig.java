package com.company.conversation.framework.autoconfigure;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Web MVC configuration for the conversation framework.
 * 
 * ConversationWebMvcConfig registers dynamic request mappings for the
 * conversation controller, allowing the endpoint path to be configured
 * via application properties.
 * 
 * @since 1.0.0
 */
public class ConversationWebMvcConfig implements WebMvcConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(ConversationWebMvcConfig.class);
    private final String path;
    /**
     * Constructs a ConversationWebMvcConfig.
     * 
     * @param path the conversation endpoint path
     */
    public ConversationWebMvcConfig(String path) {
        this.path = path;
        logger.debug("ConversationWebMvcConfig initialized with path: {}", path);
    }
}

