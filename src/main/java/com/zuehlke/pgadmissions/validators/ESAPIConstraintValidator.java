package com.zuehlke.pgadmissions.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.IntrusionException;

public class ESAPIConstraintValidator implements ConstraintValidator<ESAPIConstraint, String> {
 
    private boolean allowNull;
    
    private int maxLength;
    
    private int minLength;
    
    private String esapiValidationRule;
    
    @SuppressWarnings("unused")
    private String message;
    
    private String maximumExceededMessage = "{text.field.maximumexceeded}";
    
    private String minimumExceededMessage = "{text.field.minimumexceeded}";
        
    public ESAPIConstraintValidator() {
    }

    @Override
    public void initialize(final ESAPIConstraint target) {
        allowNull = target.allowNull();
        maxLength = target.maxLength();
        minLength = target.minLength();
        esapiValidationRule = target.rule();
        message = target.message();
    }

    @Override
    public boolean isValid(final String input, final ConstraintValidatorContext context) {
        boolean returnValue = false;
        try {
            returnValue = ESAPI.validator().isValidInput("input", input, esapiValidationRule, maxLength, allowNull);
        } catch (IntrusionException e) {
            // do nothing
        } catch (Exception e) {
            // do nothing
        }
        
        if (!returnValue) {
            if (isInputTooLong(input)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(maximumExceededMessage).addConstraintViolation();
            } else if (isInputTooShort(input)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(minimumExceededMessage).addConstraintViolation();
            }
        }
        return returnValue;
    }
    
    private boolean isInputTooShort(String input) {
        try {
            String canonical = ESAPI.encoder().canonicalize(input);
            return canonical.length() < minLength;
        } catch (IntrusionException e) {
            // do nothing
        } catch (Exception e) {
            // do nothing
        }
        return false;
    }

    private boolean isInputTooLong(final String input) {
        try {
            String canonical = ESAPI.encoder().canonicalize(input);
            return canonical.length() > maxLength;
        } catch (IntrusionException e) {
            // do nothing
        } catch (Exception e) {
            // do nothing
        }
        return false;
    }
}
