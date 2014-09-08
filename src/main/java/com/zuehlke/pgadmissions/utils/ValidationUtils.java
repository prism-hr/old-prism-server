package com.zuehlke.pgadmissions.utils;

import org.springframework.validation.Errors;

import com.zuehlke.pgadmissions.services.helpers.IntrospectionHelper;

public class ValidationUtils {

    public static void rejectIfNotNull(Object object, Errors errors, String field, String errorCode) {
        Object value = IntrospectionHelper.getProperty(object, field);
        if (value != null) {
            errors.rejectValue(field, errorCode);
        }
    }

}
