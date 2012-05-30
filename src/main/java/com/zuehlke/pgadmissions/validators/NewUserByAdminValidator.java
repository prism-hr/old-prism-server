package com.zuehlke.pgadmissions.validators;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.RegisteredUser;

@Service
public class NewUserByAdminValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return RegisteredUser.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		RegisteredUser user = (RegisteredUser) target;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "user.firstName.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "user.lastName.notempty");

		if (!EmailValidator.getInstance().isValid(user.getEmail())) {
			errors.rejectValue("email", "user.email.invalid");
		}
	}
	
	
}
