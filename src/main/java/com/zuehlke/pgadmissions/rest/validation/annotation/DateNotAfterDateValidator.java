package com.zuehlke.pgadmissions.rest.validation.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.joda.time.DateTime;

import com.zuehlke.pgadmissions.services.helpers.IntrospectionHelper;

public class DateNotAfterDateValidator implements ConstraintValidator<DateNotAfterDate, Object> {

    private String startDate;

    private String endDate;

    @Override
    public void initialize(DateNotAfterDate constraintAnnotation) {
        startDate = constraintAnnotation.startDate();
        endDate = constraintAnnotation.endDate();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        DateTime start = (DateTime) IntrospectionHelper.getProperty(value, startDate);
        DateTime end = (DateTime) IntrospectionHelper.getProperty(value, endDate);

        if (start == null || end == null) {
            return true;
        } else {
            return !start.isAfter(end);
        }
    }
}