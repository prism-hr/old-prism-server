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
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "comment", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "suitableForUCL", EMPTY_DROPDOWN_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "suitableForProgramme", EMPTY_DROPDOWN_ERROR_MESSAGE);
	}

}
