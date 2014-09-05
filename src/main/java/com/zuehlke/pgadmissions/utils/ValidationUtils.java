package com.zuehlke.pgadmissions.utils;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.validation.Errors;

public class ValidationUtils {

    public static void rejectIfNotNull(Object object, Errors errors, String field, String errorCode) {
        try {
            Object value = PropertyUtils.getSimpleProperty(object, field);
            if (value != null) {
                errors.rejectValue(field, errorCode);
            }
        } catch (Exception e) {
            throw new Error(e);
        }
    }

}
