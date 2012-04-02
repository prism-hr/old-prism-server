package com.zuehlke.pgadmissions.validators;

import java.util.Date;

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
		Date today = new Date();
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressLocation", "user.location.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressPostCode", "user.postCode.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressCountry", "user.country.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressStartDate", "user.startDate.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressPurpose", "user.purpose.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "addressContactAddress", "user.contactAddress.notempty");
		Address address = (Address) target;
		String startDate = address.getAddressStartDate() == null ? "": address.getAddressStartDate().toString();
		String endDate = address.getAddressEndDate() == null ? "": address.getAddressEndDate().toString();
		if (StringUtils.isNotBlank(startDate) && address.getAddressEndDate() != null && address.getAddressStartDate().after(address.getAddressEndDate())) {
			errors.rejectValue("addressStartDate", "user.startDate.notvalid");
		}
		if (StringUtils.isNotBlank(startDate) && address.getAddressStartDate().after(today)) {
			errors.rejectValue("addressStartDate", "address.startDate.future");
		}
		if (StringUtils.isNotBlank(endDate) && address.getAddressEndDate().after(today)) {
			errors.rejectValue("addressEndDate", "address.endDate.future");
		}
	}

}
