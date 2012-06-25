package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;

@Component
public class DocumentSectionValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(ApplicationForm.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "personalStatement", "file.upload.empty");

	}

}
