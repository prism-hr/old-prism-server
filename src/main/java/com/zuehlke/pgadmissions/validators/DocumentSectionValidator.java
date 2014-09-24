package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;

@Component
public class DocumentSectionValidator extends FormSectionObjectValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(ApplicationForm.class);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
		super.addExtraValidation(target, errors);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "personalStatement", "file.upload.empty");
	}
}