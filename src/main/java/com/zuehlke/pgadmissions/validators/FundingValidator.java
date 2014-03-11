package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Funding;

@Component
public class FundingValidator extends FormSectionObjectValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Funding.class.equals(clazz);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
		super.addExtraValidation(target, errors);	
		Funding funding = (Funding) target;
		
		Date today = new Date();
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", EMPTY_DROPDOWN_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", EMPTY_FIELD_ERROR_MESSAGE);
	    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "value", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "awardDate", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "document", "file.upload.empty");
		
		if (funding.getAwardDate() != null && funding.getAwardDate().after(today)) {
			errors.rejectValue("awardDate", "date.field.notpast");
		}
		
	}
}
