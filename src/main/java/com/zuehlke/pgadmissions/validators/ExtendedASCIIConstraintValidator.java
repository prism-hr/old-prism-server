package com.zuehlke.pgadmissions.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ExtendedASCIIConstraintValidator implements ConstraintValidator<ExtendedASCIIConstraint, String> {

    @SuppressWarnings("unused")
    private String message;

    public final static String CONTAINS_NONE_ASCII_MESSAGE = "{text.field.nonextendedascii}";

    public ExtendedASCIIConstraintValidator() {
    }

    @Override
    public void initialize(ExtendedASCIIConstraint target) {
        message = target.message();
    }

    @Override
    public boolean isValid(final String input, final ConstraintValidatorContext context) {
        boolean returnValue = isOnlyContainExtendedASCIICharacters(input);
        if (!returnValue) {
            context.buildConstraintViolationWithTemplate(CONTAINS_NONE_ASCII_MESSAGE).addConstraintViolation();
        }
        return returnValue;
    }

    private boolean isOnlyContainExtendedASCIICharacters(String input) {
        if (input != null) {
            for (int i = 0; i < input.length() - 1; i++) {
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
