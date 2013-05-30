package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.ProgramAdvert;
import com.zuehlke.pgadmissions.propertyeditors.DurationOfStudyPropertyEditor;

@Component
public class ProgramAdvertValidator extends AbstractValidator {

    private static final String PROSPECTUS_DURATION_OF_STUDY_EMPTY_OR_NOT_INTEGER = "prospectus.durationOfStudy.emptyOrNotInteger";

    @Override
    public boolean supports(Class<?> clazz) {
        return ProgramAdvert.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        ProgramAdvert programAdvert = (ProgramAdvert) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", EMPTY_FIELD_ERROR_MESSAGE);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "durationOfStudyInMonth", PROSPECTUS_DURATION_OF_STUDY_EMPTY_OR_NOT_INTEGER);
        Integer durationOfStudyInMonth = programAdvert.getDurationOfStudyInMonth();
        if (durationOfStudyInMonth != null && durationOfStudyInMonth.equals(DurationOfStudyPropertyEditor.ERROR_VALUE_FOR_DURATION_OF_STUDY)) {
            errors.rejectValue("durationOfStudyInMonth", PROSPECTUS_DURATION_OF_STUDY_EMPTY_OR_NOT_INTEGER);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "isCurrentlyAcceptingApplications", EMPTY_DROPDOWN_ERROR_MESSAGE);

    }
}
