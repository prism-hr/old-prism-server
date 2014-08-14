package com.zuehlke.pgadmissions.rest.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang.StringUtils;

public class ATASConstraintValidator implements ConstraintValidator<ATASConstraint, String> {

    private static final int MAX_ABSTRACT_WORD_COUNT = 200;

    @Override
    public void initialize(final ATASConstraint target) {
    }

    @Override
    public boolean isValid(final String input, final ConstraintValidatorContext context) {
        if (input != null) {
            int wordCount = countWords(input);
            if (wordCount > MAX_ABSTRACT_WORD_COUNT) {
                return false;
            }
        }
        return true;
    }

    private int countWords(String text) {
        return StringUtils.split(text, "\t\n\r ").length;
    }

}
