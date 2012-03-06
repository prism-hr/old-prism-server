package com.zuehlke.pgadmissions.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dto.ApplicationFormDetails;

public class ApplicationFormValidator implements Validator{


	@Override
	public boolean supports(Class<?> clazz) {
		return ApplicationFormDetails.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ApplicationFormDetails applicationFormDetails = (ApplicationFormDetails) target;
		if (applicationFormDetails.getNumberOfAddresses() == 0) {
			errors.rejectValue("numberOfAddresses", "user.addresses.notempty");
		}
		
		if (applicationFormDetails.getNumberOfContactAddresses() == 0) {
			errors.rejectValue("numberOfContactAddresses", "user.contactAddresses.notempty");
		}
		
		if (applicationFormDetails.getNumberOfContactAddresses() > 1) {
			errors.rejectValue("numberOfContactAddresses", "user.contactAddresses.notvalid");
		}
	}
}
