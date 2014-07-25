package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.ApplicationDocument;

@Component
public class ApplicationFormDocumentValidator extends AbstractValidator {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(ApplicationDocument.class);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "personalStatement", "file.upload.empty");
	}
	
}
