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
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentAddress1", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentAddress3", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "currentAddressDomicile", EMPTY_DROPDOWN_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactAddress1", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactAddress3", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactAddressDomicile", EMPTY_DROPDOWN_ERROR_MESSAGE);
	}
}
