package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dto.AddressSectionDTO;

@Component
public class AddressSectionDTOValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return AddressSectionDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentAddressLocation", "user.location.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentAddressCountry", "user.country.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactAddressLocation", "user.location.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactAddressCountry", "user.country.notempty");

		AddressSectionDTO address = (AddressSectionDTO) target;

		if (address.getCurrentAddressLocation() != null) {
			if (address.getCurrentAddressLocation().length() > 2000) {
				errors.rejectValue("currentAddressLocation", "user.addressLength.exceeded");
			}
		}

		if (address.getContactAddressLocation() != null) {
			if (address.getContactAddressLocation().length() > 2000) {
				errors.rejectValue("contactAddressLocation", "user.addressLength.exceeded");
			}
		}
	}
}
