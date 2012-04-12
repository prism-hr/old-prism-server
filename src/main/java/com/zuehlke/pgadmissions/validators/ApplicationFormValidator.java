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
		if(applicationForm.getProgrammeDetails() != null && applicationForm.getProgrammeDetails().getId() == null){
			errors.rejectValue("programmeDetails", "user.programmeDetails.incomplete");
		}else{
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "programmeDetails", "user.programmeDetails.incomplete");
		}
		if(applicationForm.getPersonalDetails() != null && applicationForm.getPersonalDetails().getId() == null){
			errors.rejectValue( "personalDetails", "user.personalDetails.incomplete");
		}else{
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "personalDetails", "user.personalDetails.incomplete");
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentAddress", "user.addresses.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactAddress", "user.addresses.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "personalStatement", "documents.section.invalid");
		if(applicationForm.getReferees().size() < 3){
			errors.rejectValue("referees", "user.referees.notvalid");
		}
	
	}
}
