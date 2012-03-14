package com.zuehlke.pgadmissions.validators;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dto.PersonalDetailsDTO;

@Service
public class PersonalDetailsValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return PersonalDetailsDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "user.firstname.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "user.lastname.notempty");
		PersonalDetailsDTO personalDetails = (PersonalDetailsDTO) target;
		if (!EmailValidator.getInstance().isValid(personalDetails.getEmail())) {
			errors.rejectValue("email", "user.email.notvalid");
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gender", "user.gender.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateOfBirth", "user.dateOfBirth.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", "user.country.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "residenceCountry", "user.residenceCountry.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "residenceStatus", "user.residenceStatus.notempty");
	}

}
