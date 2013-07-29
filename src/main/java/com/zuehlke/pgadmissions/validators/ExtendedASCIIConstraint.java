package com.zuehlke.pgadmissions.validators;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import org.apache.commons.lang.StringUtils;

@Documented
@Constraint(validatedBy = ExtendedASCIIConstraintValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtendedASCIIConstraint {

    String propertyPath() default StringUtils.EMPTY;
    
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String message() default "{text.field.nonextendedascii}";
    
    int maxLength() default 50000;
}
