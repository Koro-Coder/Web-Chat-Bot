package com.company.conversation.framework.autoconfigure;

import com.company.conversation.framework.controller.ConversationController;
import com.company.conversation.framework.orchestrator.ConversationOrchestrator;
import com.company.conversation.framework.handler.ConversationHandler;
import com.company.conversation.framework.handler.DefaultConversationHandler;
import com.company.conversation.framework.properties.ConversationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Spring Boot auto-configuration for the conversation framework.
 * 
 * ConversationAutoConfiguration provides automatic setup of the conversation
 * framework when included in the application classpath. Configuration is
 * controlled by the conversation.enabled property.
 * 
 * This configuration:
 * - Loads ConversationProperties from application configuration
 * - Registers ConversationEngine and ConversationController when enabled
 * - Provides DefaultConversationHandler if no custom handler is present
 * - Only activates when conversation.enabled=true
 * 
 * @since 1.0.0
 */
@Configuration
@EnableConfigurationProperties(ConversationProperties.class)
@ConditionalOnProperty(
    prefix = "conversation",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = false
)
public class ConversationAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ConversationAutoConfiguration.class);
    /**
     * Registers the default conversation handler if no custom handler exists.
     * 
     * @return the default handler
     */
    @Bean
    @ConditionalOnMissingBean(ConversationHandler.class)
    public ConversationHandler defaultConversationHandler() {
        logger.info("Registering DefaultConversationHandler");
        return new DefaultConversationHandler();
    }
    /**
     * Registers the conversation engine.
     * 
     * @param handler the conversation handler
     * @param preProcessors the pre-processors
     * @param postProcessors the post-processors
     * @return the configured orchestrator
     */
    @Bean
    public ConversationOrchestrator conversationOrchestrator(
            ConversationHandler handler,
            java.util.List<com.company.conversation.framework.pipeline.pre.ConversationPreProcessor> preProcessors,
            java.util.List<com.company.conversation.framework.pipeline.post.ConversationPostProcessor> postProcessors
    ) {
        logger.info("Registering ConversationOrchestrator");
        return new ConversationOrchestrator(handler, preProcessors, postProcessors);
    }
    /**
     * Registers the REST controller.
     * 
     * @param engine the conversation engine
     * @param properties the conversation properties
     * @return the configured controller
     */
    @Bean
    public ConversationController conversationController(
            ConversationOrchestrator orchestrator,
            ConversationProperties properties
    ) {
        logger.info("Registering ConversationController at path: {}", properties.getPath());
        return new ConversationController(orchestrator, properties.getPath());
    }
    /**
     * Configures dynamic request mapping for the conversation controller.
     * 
     * This WebMvcConfigurer registers the controller''s endpoint at the
     * configured path.
     * 
     * @param properties the conversation properties
     * @return the web mvc configurer
     */
    @Bean
    public WebMvcConfigurer conversationWebMvcConfigurer(ConversationProperties properties) {
        return new ConversationWebMvcConfig(properties.getPath());
    }
}

