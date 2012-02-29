package com.zuehlke.pgadmissions.validators;

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
		ValidationUtils.rejectIfEmpty(errors, "firstName", "First name cannot be empty.");
		ValidationUtils.rejectIfEmpty(errors, "lastName", "user.lastname.notempty");
	}

}
