package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
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
		
		LocalDate today = new LocalDate();
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "employerName", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "employerAddress", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "position", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "remit", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "startDate", EMPTY_FIELD_ERROR_MESSAGE);
		EmploymentPosition position = (EmploymentPosition) target;
		
		String startDate = position.getStartDate() == null ? "" : position.getStartDate().toString();
		String endDate = position.getEndDate() == null ? "" : position.getEndDate().toString();
		if (StringUtils.isNotBlank(startDate) && position.getStartDate().isAfter(today)) {
			errors.rejectValue("startDate", "date.field.notpast");
		}
		if (StringUtils.isNotBlank(endDate) && position.getEndDate().isAfter(today)) {
			errors.rejectValue("endDate", "date.field.notpast");
		}
		if (StringUtils.isNotBlank(startDate) && position.getEndDate() != null && position.getStartDate().isAfter(position.getEndDate())) {
			errors.rejectValue("startDate", "position.position_startDate.notvalid");
		}
		if (!position.isCurrent()  && StringUtils.isBlank(endDate)){
			errors.rejectValue("endDate", EMPTY_FIELD_ERROR_MESSAGE);
		}
		if (position.getApplication().getEmploymentPositions().size() >= MAX_NUMBER_OF_POSITIONS + 1) {
		    errors.reject("");
		}
		if (position.getEmployerAddress() != null && StringUtils.isBlank(position.getEmployerAddress().getAddressLine1())) {
			errors.rejectValue("employerAddress.address1", EMPTY_FIELD_ERROR_MESSAGE);
		}
		if (position.getEmployerAddress() != null && StringUtils.isBlank(position.getEmployerAddress().getAddressTown())) {
			errors.rejectValue("employerAddress.address3", EMPTY_FIELD_ERROR_MESSAGE);
		}
		if (position.getEmployerAddress() != null && position.getEmployerAddress().getDomicile()==null) {
			errors.rejectValue("employerAddress.domicile", EMPTY_FIELD_ERROR_MESSAGE);
		}
	}
}
