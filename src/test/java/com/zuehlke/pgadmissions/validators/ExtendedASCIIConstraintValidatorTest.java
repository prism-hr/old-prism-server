package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.validation.ConstraintValidatorContext;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class ExtendedASCIIConstraintValidatorTest {

    private ExtendedASCIIConstraintValidator validator;
    private ConstraintValidatorContext contextMock;

    @Before
    public void setUp() {
        validator = new ExtendedASCIIConstraintValidator();
        contextMock = EasyMock.createMock(ConstraintValidatorContext.class);
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
        assertFalse(validator.isValid(roastedDuck, contextMock));
    }

    @Test
    public void shouldFailWithOneChineseCharacter() {
        String roastedDuck = "一";
        assertFalse(validator.isValid(roastedDuck, contextMock));
    }
}
