package com.zuehlke.pgadmissions.utils;

import org.springframework.validation.Errors;

public class ValidationUtils {

    public static void rejectIfNotNull(Object object, Errors errors, String field, String errorCode) {
        Object value = IntrospectionUtils.getProperty(object, field);
        if (value != null) {
            errors.rejectValue(field, errorCode);
        }
    }

}
