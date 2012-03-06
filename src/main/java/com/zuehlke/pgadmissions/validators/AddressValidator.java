package com.zuehlke.pgadmissions.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dto.Address;

public class AddressValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Address.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "street", "user.street.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "postCode", "user.postCode.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "city", "user.city.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", "user.country.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "startDate", "user.startDate.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "endDate", "user.endDate.notempty");
	}

}
