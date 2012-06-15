package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Reference;

@Component
public class ReferenceValidator implements Validator {

	@Override	
	
	public boolean supports(Class<?> clazz) {		
		return Reference.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "document", "reference.document.notempty");

	}

}
