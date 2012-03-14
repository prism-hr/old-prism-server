package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.ProgrammeDetail;

@Component
public class ProgrammeDetailsValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return ProgrammeDetail.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "programmeName", "user.programmeName.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "studyOption", "user.studyOption.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "startDate", "user.programmeStartDate.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "referrer", "user.programmeReferrer.notempty");
	}

}
