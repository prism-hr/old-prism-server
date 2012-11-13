package com.zuehlke.pgadmissions.validators;

import org.apache.commons.validator.routines.EmailValidator;
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
		RegisteredUser user = (RegisteredUser) target;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "text.field.empty");

		if (!EmailValidator.getInstance().isValid(user.getEmail())) {
			errors.rejectValue("email", "text.email.notvalid");
		}
	}
}
