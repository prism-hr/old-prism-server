package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.AdditionalInformation;

@Component
public class AdditionalInformationValidator extends FormSectionObjectValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return AdditionalInformation.class.equals(clazz);
	}

	@Override
    public void addExtraValidation(final Object target, final Errors errors) {
		super.addExtraValidation(target, errors);
		
		AdditionalInformation info = (AdditionalInformation) target;

		Boolean hasConvictions = info.getConvictions();
		if (hasConvictions == null) {
			errors.rejectValue("convictions", EMPTY_DROPDOWN_ERROR_MESSAGE);
		} else {
			if (hasConvictions) {
				String convictionsText = info.getConvictionsText();
				if (convictionsText == null || !StringUtils.hasText(convictionsText)) {
					errors.rejectValue("convictionsText", EMPTY_FIELD_ERROR_MESSAGE);
				}
			} 
		}
	}
}
