package com.zuehlke.pgadmissions.rest.validation.annotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.joda.time.LocalDate;

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
            LocalDate start = (LocalDate) PropertyUtils.getSimpleProperty(value, startDate);
            LocalDate end = (LocalDate) PropertyUtils.getSimpleProperty(value, endDate);

            return start == null || end == null || !start.isAfter(end);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
