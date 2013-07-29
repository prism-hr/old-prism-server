package com.zuehlke.pgadmissions.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExtendedASCIIConstraintValidator implements ConstraintValidator<ExtendedASCIIConstraint, String> {

    @SuppressWarnings("unused")
    private String message;

    public final static String CONTAINS_NONE_ASCII_MESSAGE = "{text.field.nonextendedascii}";

    public final static String MAXIMUM_EXCEEDED_MESSAGE = "{text.field.maximumexceeded}";

    private int maxLength;

    public ExtendedASCIIConstraintValidator() {
    }

    @Override
    public void initialize(ExtendedASCIIConstraint target) {
        message = target.message();
        maxLength = target.maxLength();
    }

    @Override
    public boolean isValid(final String input, final ConstraintValidatorContext context) {
        
        boolean isExceedingMaxLength = isExceedingMaxLength(input);
        if(isExceedingMaxLength){
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(MAXIMUM_EXCEEDED_MESSAGE).addConstraintViolation();
        }
        return onlyContainExtendedASCIICharacters(input) && !isExceedingMaxLength;
    }

    private boolean isExceedingMaxLength(String input) {
        if (input != null) {
            return input.length() > maxLength;
        }
        return false;
    }

    private boolean onlyContainExtendedASCIICharacters(String input) {
        if (input != null) {
            for (int i = 0; i <= input.length() - 1; i++) {
                if (!isExtendedASCIICharacter(input.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isExtendedASCIICharacter(char input) {
        return input >= 0 && input <= 255;
    }

}
