package com.zuehlke.pgadmissions.validators;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.PersonalDetail;

@Component
public class PersonalDetailValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(PersonalDetail.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "personalDetails.firstName.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "personalDetails.lastName.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gender", "personalDetails.gender.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "personalDetails.email.notempty");
		
		if (!errors.hasFieldErrors("email") && !EmailValidator.getInstance().isValid(((PersonalDetail)target).getEmail())) {
			errors.rejectValue("email", "personalDetails.email.invalid");
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", "personalDetails.country.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "residenceCountry", "personalDetails.residenceCountry.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "residenceStatus", "personalDetails.residenceStatus.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateOfBirth", "personalDetails.dateOfBirth.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "application", "personalDetails.application.notempty");

	}

}
