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
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressLocation", "user.location.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressCountry", "user.country.notempty");
	}
}
