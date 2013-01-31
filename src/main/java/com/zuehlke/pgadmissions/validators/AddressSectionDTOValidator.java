package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dto.AddressSectionDTO;

@Component
public class AddressSectionDTOValidator extends FormSectionObjectValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return AddressSectionDTO.class.equals(clazz);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
		super.addExtraValidation(target, errors);		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentAddress1", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentAddress3", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentAddressCountry", "dropdown.radio.select.none");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactAddress1", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactAddress3", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactAddressCountry", "dropdown.radio.select.none");
	}
}
