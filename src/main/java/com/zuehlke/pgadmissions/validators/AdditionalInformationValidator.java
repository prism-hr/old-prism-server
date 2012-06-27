package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.AdditionalInformation;

@Component
public class AdditionalInformationValidator implements Validator {

	private static final int MAXIMUM_CHARS = 5000;

	@Override
	public boolean supports(Class<?> clazz) {
		return AdditionalInformation.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		AdditionalInformation info = (AdditionalInformation) target;
		if (info.getInformationText() != null) {
			if (info.getInformationText().length() > MAXIMUM_CHARS) {
				errors.rejectValue("informationText", "additionalInformation.text.notvalid");
			}
		}

		Boolean hasConvictions = info.getConvictions();
		if (hasConvictions == null) {
			errors.rejectValue("convictions", "dropdown.radio.select.none");
		} else {
			if (hasConvictions) {
				String convictionsText = info.getConvictionsText();
				if (convictionsText == null || !StringUtils.hasText(convictionsText)) {
					errors.rejectValue("convictionsText", "text.field.empty");
				} else {
					if (convictionsText.length() > MAXIMUM_CHARS) {
						errors.rejectValue("convictionsText", "additionalInformation.text.notvalid");
					}
				}
			} 
		}
	}
}
