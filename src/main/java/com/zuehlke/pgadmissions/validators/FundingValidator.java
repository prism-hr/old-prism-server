package com.zuehlke.pgadmissions.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dto.Funding;

public class FundingValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Funding.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fundingType", "user.fundingType.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fundingDescription", "user.fundingDescription.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fundingValue", "user.fundingValue.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fundingAwardDate", "user.fundingAwardDate.notempty");
	}

}
