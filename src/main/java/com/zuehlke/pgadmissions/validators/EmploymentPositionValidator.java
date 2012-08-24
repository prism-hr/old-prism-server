package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.EmploymentPosition;

@Component
public class EmploymentPositionValidator extends FormSectionObjectValidator implements Validator {

    private static final int MAX_NUMBER_OF_POSITIONS = 5;
    
	@Override
	public boolean supports(Class<?> clazz) {
		return EmploymentPosition.class.equals(clazz);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
		super.addExtraValidation(target, errors);
		
		Date today = new Date();
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "employerName", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "employerAddress", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "employerCountry", "dropdown.radio.select.none");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "position", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "remit", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "startDate", "text.field.empty");
		EmploymentPosition position = (EmploymentPosition) target;
		
		String startDate = position.getStartDate() == null ? "" : position.getStartDate().toString();
		String endDate = position.getEndDate() == null ? "" : position.getEndDate().toString();
		if (StringUtils.isNotBlank(startDate) && position.getStartDate().after(today)) {
			errors.rejectValue("startDate", "date.field.notpast");
		}
		if (StringUtils.isNotBlank(endDate) && position.getEndDate().after(today)) {
			errors.rejectValue("endDate", "date.field.notpast");
		}
		if (StringUtils.isNotBlank(startDate) && position.getEndDate() != null && position.getStartDate().after(position.getEndDate())) {
			errors.rejectValue("startDate", "position.position_startDate.notvalid");
		}
		if (!position.isCurrent()  && StringUtils.isBlank(endDate)){
			errors.rejectValue("endDate", "text.field.empty");
		}
		if (position.getApplication().getEmploymentPositions().size() >= MAX_NUMBER_OF_POSITIONS + 1) {
		    errors.reject("");
		}
	}
}
