package com.zuehlke.pgadmissions.utils;

import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.google.common.collect.Maps;

public class FieldErrorUtils {

    public static Map<String, Object> populateMapWithErrors(final BindingResult bindingResult, final MessageSource messageSource) {
        Map<String, Object> result = Maps.newHashMap();
        for (FieldError error : bindingResult.getFieldErrors()) {
            result.put(error.getField(), messageSource.getMessage(error, Locale.getDefault()));
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

}
