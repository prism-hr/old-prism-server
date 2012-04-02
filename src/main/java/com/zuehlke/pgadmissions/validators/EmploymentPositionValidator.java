package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.enums.CheckedStatus;
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
		EmploymentPosition position = (EmploymentPosition) target;
		String startDate = position.getPosition_startDate() == null ? "": position.getPosition_startDate().toString();
		if (StringUtils.isNotBlank(startDate) && position.getPosition_endDate() != null && position.getPosition_startDate().after(position.getPosition_endDate())) {
			errors.rejectValue("position_startDate", "position.position_startDate.notvalid");
		}
		String endDate = position.getPosition_endDate() == null ? "": position.getPosition_endDate().toString();
		if (position.getCompleted() == CheckedStatus.YES && StringUtils.isBlank(endDate)){
			errors.rejectValue("position_endDate", "position.position_endDate.notempty");
		}
		if (position.getCompleted()== CheckedStatus.NO && StringUtils.isNotBlank(endDate)){
			errors.rejectValue("position_endDate", "position.position_endDate.empty");
		}
	}
}
