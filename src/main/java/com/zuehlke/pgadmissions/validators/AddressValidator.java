package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.StringUtils;
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
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "location", "user.street.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "postCode", "user.postCode.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", "user.country.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "startDate", "user.startDate.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "endDate", "user.endDate.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "purpose", "user.purpose.notempty");
		Address address = (Address) target;
		String startDate = address.getStartDate() == null ? "": address.getStartDate().toString();
		if (StringUtils.isNotBlank(startDate) && address.getStartDate().after(address.getEndDate())) {
			errors.rejectValue("startDate", "user.startDate.notvalid");
		}
	}

}
