package com.zuehlke.pgadmissions.rest.validation.annotation;

import org.joda.time.DateTime;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DateNotFutureValidator implements ConstraintValidator<DateNotFuture, Object> {

    @Override
    public void initialize(DateNotFuture constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if(value == null) {
            return true;
        }
        org.joda.time.LocalDate date = ((DateTime) value).toLocalDate();
        org.joda.time.LocalDate today = new org.joda.time.LocalDate();
        return !date.isAfter(today);
    }
}