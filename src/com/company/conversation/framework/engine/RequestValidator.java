package com.company.conversation.framework.engine;
import com.company.conversation.framework.exception.ValidationException;
import com.company.conversation.protocol.ActivityRequest;
/**
 * Extension point for request validation.
 * 
 * RequestValidator allows applications to define custom validation logic
 * that runs before the handler is invoked. Implementations can validate
 * request format, business rules, authentication, authorization, etc.
 * 
 * Validators are optional: if no bean is registered, validation is skipped.
 * 
 * @since 1.0.0
 */
@FunctionalInterface
public interface RequestValidator {
    /**
     * Validates the incoming request.
     * 
     * @param request the request to validate
     * @throws ValidationException if validation fails
     */
    void validate(ActivityRequest request) throws ValidationException;
}

