package com.zuehlke.pgadmissions.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dto.ApplicationFormDetails;

public class ApplicationFormValidator implements Validator{


	@Override
	public boolean supports(Class<?> clazz) {
		return ApplicationFormDetails.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ApplicationFormDetails applicationForm = (ApplicationFormDetails) target;

		try {
			errors.pushNestedPath("personalDetails");
		ValidationUtils.invokeValidator(new PersonalDetailsValidator(), applicationForm.getPersonalDetails(), errors);
		} finally {
			errors.popNestedPath();
		}

		try {
			errors.pushNestedPath("address");
			ValidationUtils.invokeValidator(new AddressValidator(), applicationForm.getAddress(), errors);
		} finally {
			errors.popNestedPath();
		}

		try {
			errors.pushNestedPath("funding");
			ValidationUtils.invokeValidator(new FundingValidator(), applicationForm.getFunding(), errors);
		} finally {
			errors.popNestedPath();
		}
	}

}
