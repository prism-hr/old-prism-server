package com.zuehlke.pgadmissions.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.IntrusionException;

public class ESAPIConstraintValidator implements ConstraintValidator<ESAPIConstraint, String> {
 
    private boolean allowNull;
    
    private int maxLength;
    
    private String esapiValidationRule;
    
    private String message;
        
    public ESAPIConstraintValidator() {
    }

    @Override
    public void initialize(final ESAPIConstraint target) {
        allowNull = target.allowNull();
        maxLength = target.maxLength();
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
                message = "{text.field.maximumexceeded}";
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            }
        }
        
        return returnValue;
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
