package com.zuehlke.pgadmissions.validators;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.annotation.Annotation;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.constraints.AssertFalse;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ExtendedASCIIConstraintValidatorTest {

    private ExtendedASCIIConstraintValidator validator;
    private ConstraintValidatorContext contextMock;
    private static ExtendedASCIIConstraint target;
    private ConstraintViolationBuilder violationBuilderMock;

    @Before
    public void setUp() {
        validator = new ExtendedASCIIConstraintValidator();
        
        validator.initialize(target);
        contextMock = EasyMock.createMock(ConstraintValidatorContext.class);
        violationBuilderMock = EasyMock.createMock(ConstraintViolationBuilder.class);
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
        for (int i = 0; i < 50000; i++) {
            sb.append(a);
            sb.append(extendedASCIIChar);
        }
        
        contextMock.disableDefaultConstraintViolation();
        EasyMock.expectLastCall();
        EasyMock.expect(contextMock.buildConstraintViolationWithTemplate(ExtendedASCIIConstraintValidator.MAXIMUM_EXCEEDED_MESSAGE)).andReturn(violationBuilderMock);
        EasyMock.expect(violationBuilderMock.addConstraintViolation()).andReturn(null);
        EasyMock.replay(contextMock,violationBuilderMock);
        assertFalse(validator.isValid(sb.toString(), contextMock));
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
    
    @BeforeClass
    public static void classSetUp(){
        target = new ExtendedASCIIConstraint() {
            
            @Override
            public int maxLength() {
                return 50000;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String propertyPath() {
                return null;
            }

            @Override
            public Class<?>[] groups() {
                return null;
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return null;
            }

            @Override
            public String message() {
                return null;
            }
        };
    }
}
