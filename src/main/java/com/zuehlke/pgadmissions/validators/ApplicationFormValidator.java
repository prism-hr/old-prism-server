package com.zuehlke.pgadmissions.validators;

import org.springframework.validation.DirectFieldBindingResult;
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
//		
//		if (applicationFormDetails.getNumberOfReferees() < 2) {
//			errors.rejectValue("numberOfReferees", "user.referees.notvalid");
//		}
//
//		DirectFieldBindingResult mappingResult = new DirectFieldBindingResult(applicationFormDetails.getPersonalDetails(), "personalDetails");
//
//		if (applicationFormDetails.getPersonalDetails() == null) {
//			errors.rejectValue("personalDetails", "user.personalDetails.incomplete");
//		} else {
//			PersonalDetailsValidator validator = new PersonalDetailsValidator();
//			validator.validate(applicationFormDetails.getPersonalDetails(), mappingResult);
//			if (mappingResult.hasErrors()) {
//				errors.rejectValue("personalDetails", "user.personalDetails.incomplete");
//			}
//		}
		
	}
}
