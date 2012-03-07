package com.zuehlke.pgadmissions.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dto.EmploymentPosition;


public class EmploymentPositionValidator implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		return EmploymentPosition.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "position_employer", "position.position_employer.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "position_title", "position.position_title.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "position_remit", "position.position_remit.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "position_startDate", "position.position_startDate.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "position_language", "position.position_language.notempty");
	}
}
