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
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentAddressLocation", "user.location.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentAddressCountry", "user.country.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactAddressLocation", "user.location.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactAddressCountry", "user.country.notempty");
	}
}
