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
		
		Date today = new Date();
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", "dropdown.radio.select.none");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "value", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "awardDate", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "document", "file.upload.empty");
		Funding fund = (Funding) target;
		
		if (fund.getAwardDate() != null && fund.getAwardDate().after(today)) {
			errors.rejectValue("awardDate", "date.field.notpast");
		}
	}
}
