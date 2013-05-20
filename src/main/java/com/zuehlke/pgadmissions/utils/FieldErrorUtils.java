package com.zuehlke.pgadmissions.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class FieldErrorUtils {

    public static Map<String, Object> populateMapWithErrors(final BindingResult bindingResult, final MessageSource messageSource) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            String message = StringUtils.EMPTY;

            if (StringUtils.isNotBlank(error.getCode())) {
                message = resolveMessage(error.getCode(), messageSource);
            }

            if (message.equals(StringUtils.EMPTY)) {
                message = getDefaultMessage(error);
            }
            result.put(error.getField(), message);
        }
        return result;
    }

    public static String resolveMessage(final String code, final MessageSource messageSource) {
        try {
            return messageSource.getMessage(code, null, Locale.getDefault());
        } catch (NoSuchMessageException e) {
            return StringUtils.EMPTY;
        }
    }

    public static String getDefaultMessage(final FieldError error) {
        return StringUtils.trimToEmpty(error.getDefaultMessage());
    }
}
