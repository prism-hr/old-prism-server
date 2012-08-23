package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.Rejection;

@Component
public class RejectionValidator extends AbstractValidator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Rejection.class.equals(clazz);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
	
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rejectionReason", "dropdown.radio.select.none");
	}

}
