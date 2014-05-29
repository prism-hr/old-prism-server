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
@Constraint(validatedBy = ESAPIConstraintValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ESAPIConstraint {

    boolean allowNull() default true;
    
    int maxLength();
    
    int minLength() default 0;
    
    String rule() default "SafeString";
    
    String propertyPath() default StringUtils.EMPTY;
    
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String message() default "{text.field.nonextendedascii}";
}
