package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.ReferenceComment;

@Component
public class ReferenceValidator extends AbstractValidator {

	@Override	
	public boolean supports(Class<?> clazz) {		
		return ReferenceComment.class.isAssignableFrom(clazz);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "documents", "file.upload.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "suitableForUCL", "dropdown.radio.select.none");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "suitableForProgramme", "dropdown.radio.select.none");
	}

}
