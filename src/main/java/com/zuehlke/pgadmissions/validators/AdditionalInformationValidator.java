package com.zuehlke.pgadmissions.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dto.AdditionalInformation;

public class AdditionalInformationValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return AdditionalInformation.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		AdditionalInformation addditionalInformation = (AdditionalInformation) target;
		if (addditionalInformation.getAdditionalInformation().length()>5000) {
			errors.rejectValue("additionalInformation", "user.additionalInformation.notvalid");
		}
	}

}
