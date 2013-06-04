package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.Advert;
import com.zuehlke.pgadmissions.propertyeditors.DurationOfStudyPropertyEditor;

@Component
public class ProgramAdvertValidator extends AbstractValidator {

    private static final String PROSPECTUS_DURATION_OF_STUDY_EMPTY_OR_NOT_INTEGER = "prospectus.durationOfStudy.emptyOrNotInteger";

    @Override
    public boolean supports(Class<?> clazz) {
        return Advert.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        Advert programAdvert = (Advert) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", EMPTY_FIELD_ERROR_MESSAGE);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "studyDuration", PROSPECTUS_DURATION_OF_STUDY_EMPTY_OR_NOT_INTEGER);
        Integer durationOfStudyInMonth = programAdvert.getStudyDuration();
        if (durationOfStudyInMonth != null && durationOfStudyInMonth.equals(DurationOfStudyPropertyEditor.ERROR_VALUE_FOR_DURATION_OF_STUDY)) {
            errors.rejectValue("studyDuration", PROSPECTUS_DURATION_OF_STUDY_EMPTY_OR_NOT_INTEGER);
        }
        if (durationOfStudyInMonth != null && durationOfStudyInMonth.equals(DurationOfStudyPropertyEditor.ERROR_UNIT_FOR_DURATION_OF_STUDY)) {
        	errors.rejectValue("studyDuration", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "active", EMPTY_DROPDOWN_ERROR_MESSAGE);

    }
}
