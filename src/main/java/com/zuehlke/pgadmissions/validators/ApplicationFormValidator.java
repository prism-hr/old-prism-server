package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.ApplicationForm;

@Component
public class ApplicationFormValidator implements Validator{


	@Override
	public boolean supports(Class<?> clazz) {
		return ApplicationForm.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ApplicationForm applicationForm = (ApplicationForm) target;
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "programmeDetails", "user.programmeDetails.incomplete");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "personalDetails", "user.personalDetails.incomplete");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentAddress", "user.addresses.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactAddress", "user.addresses.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "personalStatement", "documents.section.invalid");
		if(applicationForm.getReferees().size() < 3){
			errors.rejectValue("referees", "user.referees.notvalid");
		}
	
	}
}
