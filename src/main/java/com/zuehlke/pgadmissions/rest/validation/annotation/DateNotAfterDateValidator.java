package com.zuehlke.pgadmissions.rest.validation.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.DateTime;

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
        try {
            DateTime start = (DateTime) PropertyUtils.getSimpleProperty(value, startDate);
            DateTime end = (DateTime) PropertyUtils.getSimpleProperty(value, endDate);

            if (start == null || end == null) {
                return true;
            } else {
                return !start.isAfter(end);
            }
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}