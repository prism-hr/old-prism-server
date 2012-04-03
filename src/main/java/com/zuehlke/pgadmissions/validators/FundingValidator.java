package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dto.Address;
import com.zuehlke.pgadmissions.dto.Funding;

public class FundingValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Funding.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Date today = new Date();
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fundingType", "user.fundingType.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fundingDescription", "user.fundingDescription.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fundingValue", "user.fundingValue.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fundingAwardDate", "user.fundingAwardDate.notempty");
		Funding funding = (Funding) target;
		String awardDate = funding.getFundingAwardDate() == null ? "": funding.getFundingAwardDate().toString();
		if (StringUtils.isNotBlank(awardDate) && funding.getFundingAwardDate().after(today)) {
			errors.rejectValue("fundingAwardDate", "funding.fundingAwardDate.future");
		}
	}

}
