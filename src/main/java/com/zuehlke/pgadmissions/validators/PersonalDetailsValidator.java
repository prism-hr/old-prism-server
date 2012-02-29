package com.zuehlke.pgadmissions.validators;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dto.PersonalDetails;

@Service
public class PersonalDetailsValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return PersonalDetails.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "First name cannot be empty.");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "Last name cannot be empty.");
		PersonalDetails personalDetails = (PersonalDetails) target;
		if (!EmailValidator.getInstance().isValid(personalDetails.getEmail())) {
			errors.rejectValue("email", "Email is not a valid email");
		}
	}

}
