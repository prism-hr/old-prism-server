package com.zuehlke.pgadmissions.validators;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ATASConstraintValidatorTest {

    private ATASConstraintValidator validator;

    @Test
    public void shouldValidateIfProjectAbstractHas200Words() {
        boolean valid = validator.isValid(createdSampleText(200), null);
        Assert.assertTrue(valid);
    }

    @Test
    public void shouldRejectIfProjectAbstractHas201Words() {
        boolean valid = validator.isValid(createdSampleText(201), null);
        Assert.assertFalse(valid);
    }

    private static String createdSampleText(int numberOfWords) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numberOfWords; i++) {
            sb.append("word ");
        }
        return sb.toString();
    }

    @Before
    public void setup() {
        validator = new ATASConstraintValidator();
    }

}
