package com.zuehlke.pgadmissions.rest.validation.annotation;

import org.joda.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DatePastValidator implements ConstraintValidator<DatePast, Object> {

    @Override
    public void initialize(DatePast constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        org.joda.time.LocalDate date = (LocalDate) value;
        org.joda.time.LocalDate today = new org.joda.time.LocalDate();
        return date.isBefore(today);
    }
}
