package com.zuehlke.pgadmissions.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dto.ProgrammeDetails;

public class ProgrammeDetailsValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return ProgrammeDetails.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "programmeDetailsProgrammeName", "user.programmeName.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "programmeDetailsStudyOption", "user.studyOption.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "programmeDetailsStartDate", "user.programmeStartDate.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "programmeDetailsReferrer", "user.programmeReferrer.notempty");
	}

}
