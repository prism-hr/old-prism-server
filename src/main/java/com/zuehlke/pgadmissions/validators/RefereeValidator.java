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
			errors.rejectValue("email", "text.email.notvalid");
		}	
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressCountry", "dropdown.radio.select.none");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressLocation", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobEmployer", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "jobTitle", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstname", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastname", "text.field.empty");
		
		if (referee.getAddressLocation() != null) {
			if (referee.getAddressLocation().length() > 200) {
				errors.rejectValue("addressLocation", "user.refereeAddressLength.exceeded");
			}
		}
	}

}
