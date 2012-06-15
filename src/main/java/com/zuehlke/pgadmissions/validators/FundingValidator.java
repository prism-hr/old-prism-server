package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Funding;

@Component
public class FundingValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Funding.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Date today = new Date();
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", "user.fundingType.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "user.fundingDescription.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "value", "user.fundingValue.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "awardDate", "user.fundingAwardDate.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "document", "user.fundingDocument.notempty");
		Funding fund = (Funding) target;
		if (fund.getAwardDate() != null && fund.getAwardDate().after(today)) {
			errors.rejectValue("awardDate", "funding.fundingAwardDate.future");
		}
		
		if (fund.getDescription() != null) {
			if (fund.getDescription().length() > 2000) {
				errors.rejectValue("description", "user.fundingDescriptLength.exceeded");
			}
		}

	}


}
