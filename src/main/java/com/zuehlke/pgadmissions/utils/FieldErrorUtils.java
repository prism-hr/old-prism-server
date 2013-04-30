package com.zuehlke.pgadmissions.utils;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class FieldErrorUtils {

	public static Map<String, String> populateMapWithErrors(BindingResult bindingResult, MessageSource messageSource) {
		Map<String, String> result = new HashMap<String, String>();
		for (FieldError error : bindingResult.getFieldErrors()) {
		    String message;
		    if (error.getCode()!=null && !isBlank(error.getCode())) {
		        message = messageSource.getMessage(error.getCode(), null, Locale.getDefault());
		    } else {
		        message = error.getDefaultMessage();
		    }
		    result.put(error.getField(), message);
		}
		return result;
	}
}
