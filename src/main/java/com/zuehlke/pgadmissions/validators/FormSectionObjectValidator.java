package com.zuehlke.pgadmissions.validators;

import org.springframework.validation.Errors;

import com.zuehlke.pgadmissions.domain.FormSectionObject;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public  abstract class FormSectionObjectValidator {

	void validate(Object target, Errors errors) {
		if(ApplicationFormStatus.UNSUBMITTED != ((FormSectionObject)target).getApplication().getStatus() && !((FormSectionObject)target).isAcceptedTerms()){
			errors.rejectValue("acceptedTerms", "text.field.empty");
		}
	};
}
