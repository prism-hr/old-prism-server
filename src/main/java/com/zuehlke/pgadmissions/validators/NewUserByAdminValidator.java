package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Component
public class NewUserByAdminValidator extends AbstractValidator {

	public NewUserByAdminValidator() {
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return RegisteredUser.class.equals(clazz);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", EMPTY_FIELD_ERROR_MESSAGE);
	}
}
