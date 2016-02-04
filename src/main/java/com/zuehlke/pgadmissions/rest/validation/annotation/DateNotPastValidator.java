package com.zuehlke.pgadmissions.rest.validation.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.joda.time.LocalDate;

public class DateNotPastValidator implements ConstraintValidator<DateNotPast, Object> {

    @Override
    public void initialize(DateNotPast constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        org.joda.time.LocalDate date = (LocalDate) value;
        org.joda.time.LocalDate today = new org.joda.time.LocalDate();
        return !date.isBefore(today);
    }
}
