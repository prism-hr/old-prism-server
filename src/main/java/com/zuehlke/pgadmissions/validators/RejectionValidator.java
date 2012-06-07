package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Rejection;

@Component
public class RejectionValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Rejection.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		System.err.println("VALIDATING");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rejectionReason", "rejection.rejectionReason.notempty");
	}

}
