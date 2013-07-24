package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class ExtendedASCIIConstraintValidatorTest {

    private ExtendedASCIIConstraintValidator validator;
    private ConstraintValidatorContext contextMock;
    private ConstraintViolationBuilder constraintViolationBuilderMock;

    @Before
    public void setUp() {
        validator = new ExtendedASCIIConstraintValidator();
        contextMock = EasyMock.createMock(ConstraintValidatorContext.class);
        constraintViolationBuilderMock = EasyMock.createMock(ConstraintViolationBuilder.class);
    }

    @Test
    public void shouldPassForASCIIString() {
        String input = "aaa";
        assertTrue(validator.isValid(input, contextMock));
    }

    @Test
    public void shouldPassForExtendedASCIIString() {
        String input = "æææ";
        assertTrue(validator.isValid(input, contextMock));
    }

    @Test
    public void shouldPassForLongExtendedASCIIString() {
        String a = "a";
        String extendedASCIIChar = "æ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2000; i++) {
            sb.append(a);
            sb.append(extendedASCIIChar);
        }
        assertTrue(validator.isValid(sb.toString(), contextMock));
    }

    @Test
    public void shouldFailWithChineseCharacter() {
        String roastedDuck = "烤鸭";

        EasyMock.expect(contextMock.buildConstraintViolationWithTemplate(ExtendedASCIIConstraintValidator.CONTAINS_NONE_ASCII_MESSAGE)).andReturn(
                        constraintViolationBuilderMock);
        EasyMock.replay(contextMock);
        assertFalse(validator.isValid(roastedDuck, contextMock));
        EasyMock.verify(contextMock);
    }
}
