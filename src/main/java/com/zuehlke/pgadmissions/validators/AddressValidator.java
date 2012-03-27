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
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressLocation", "user.location.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressPostCode", "user.postCode.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressCountry", "user.country.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressStartDate", "user.startDate.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressPurpose", "user.purpose.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressContactAddress", "user.contactAddress.notempty");
		Address address = (Address) target;
		String startDate = address.getAddressStartDate() == null ? "": address.getAddressStartDate().toString();
		if (StringUtils.isNotBlank(startDate) && address.getAddressEndDate() != null && address.getAddressStartDate().after(address.getAddressEndDate())) {
			errors.rejectValue("addressStartDate", "user.startDate.notvalid");
		}
	}

}
