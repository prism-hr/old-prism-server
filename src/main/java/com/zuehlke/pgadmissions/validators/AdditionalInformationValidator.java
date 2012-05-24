package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.AdditionalInformation;

@Component
public class AdditionalInformationValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return AdditionalInformation.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		AdditionalInformation info = (AdditionalInformation) target;
		if (info.getInformationText() != null) {
			if (info.getInformationText().length() > 5000) {
				errors.rejectValue("informationText", "additionalInformation.informationText.notvalid");
			}
		}

		Boolean hasConvictions = info.getConvictions();
		if (hasConvictions == null) {
			errors.rejectValue("convictions", "additionalInformation.convictions.notempty");
		} else {
			if (hasConvictions) {
				String convictionsText = info.getConvictionsText();
				if (convictionsText == null || !StringUtils.hasText(convictionsText)) {
					errors.rejectValue("convictionsText", "additionalInformation.convictionsText.notempty");
				} else {
					if (convictionsText.length() > 5000) {
						errors.rejectValue("convictionsText", "additionalInformation.convictionsText.notvalid");
					}
				}
			} else {
				String convictionsText = info.getConvictionsText();
				if (convictionsText != null && StringUtils.hasText(convictionsText)) {
					errors.rejectValue("convictionsText", "additionalInformation.convictionsText.noTextExpected");
				}
			}
		}
	}
}
