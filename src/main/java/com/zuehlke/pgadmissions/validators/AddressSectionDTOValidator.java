package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dto.AddressSectionDTO;

@Component
public class AddressSectionDTOValidator implements Validator {

	private static final int MAXIMUM_CHARS = 500;

	@Override
	public boolean supports(Class<?> clazz) {
		return AddressSectionDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentAddressLocation", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentAddressCountry", "dropdown.radio.select.none");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactAddressLocation", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactAddressCountry", "dropdown.radio.select.none");

		AddressSectionDTO address = (AddressSectionDTO) target;

		if (address.getCurrentAddressLocation() != null) {
			if (address.getCurrentAddressLocation().length() > MAXIMUM_CHARS) {
				errors.rejectValue("currentAddressLocation", "user.addressLength.exceeded");
			}
		}

		if (address.getContactAddressLocation() != null) {
			if (address.getContactAddressLocation().length() > MAXIMUM_CHARS) {
				errors.rejectValue("contactAddressLocation", "user.addressLength.exceeded");
			}
		}
	}
}
