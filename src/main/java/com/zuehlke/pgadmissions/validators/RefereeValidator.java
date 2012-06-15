package com.zuehlke.pgadmissions.validators;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Referee;

@Service
public class RefereeValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Referee.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Referee referee = (Referee) target;
		if (!EmailValidator.getInstance().isValid(referee.getEmail())) {
			errors.rejectValue("email", "referee.email.invalid");
		}	
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressCountry", "referee.addressCountry.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressLocation", "referee.addressLocation.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobEmployer", "referee.jobEmployer.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobTitle", "referee.jobTitle.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber", "referee.phoneNumber.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstname", "referee.firstname.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastname", "referee.lastname.notempty");
		
		if (referee.getAddressLocation() != null) {
			if (referee.getAddressLocation().length() > 200) {
				errors.rejectValue("addressLocation", "user.refereeAddressLength.exceeded");
			}
		}
	}

}
