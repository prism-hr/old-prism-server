package com.zuehlke.pgadmissions.validators;

import org.springframework.validation.Errors;

import com.zuehlke.pgadmissions.domain.FormSectionObject;
import com.zuehlke.pgadmissions.domain.enums.ApplicationFormStatus;

public abstract class FormSectionObjectValidator extends AbstractValidator {

    @Override
    public void addExtraValidation(final Object target, final Errors errors) {
        if (ApplicationFormStatus.UNSUBMITTED != ((FormSectionObject) target).getApplication().getStatus() && !((FormSectionObject) target).isAcceptedTerms()) {
            errors.rejectValue("acceptedTerms", EMPTY_FIELD_ERROR_MESSAGE);
        }
    };
}
