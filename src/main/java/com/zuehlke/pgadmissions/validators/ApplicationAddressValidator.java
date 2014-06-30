package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.ApplicationAddress;

@Component
public class ApplicationAddressValidator extends AbstractValidator {

	@Override
	public boolean supports(Class<?> clazz) {
		return ApplicationAddress.class.equals(clazz);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentAddress.address1", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentAddress.address3", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentAddress.domicile", EMPTY_DROPDOWN_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactAddress.address1", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactAddress.address3", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactAddress.domicile", EMPTY_DROPDOWN_ERROR_MESSAGE);
	}
	
}
