package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.dto.ProjectAdvertDTO;
import com.zuehlke.pgadmissions.propertyeditors.DurationOfStudyPropertyEditor;

@Component
public class ProjectAdvertDTOValidator extends AbstractValidator {

    private static final String PROSPECTUS_DURATION_OF_STUDY_EMPTY_OR_NOT_INTEGER = "prospectus.durationOfStudy.emptyOrNotInteger";

    @Override
    public boolean supports(Class<?> clazz) {
        return ProjectAdvertDTO.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        ProjectAdvertDTO dto = (ProjectAdvertDTO) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "program", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "studyDuration", PROSPECTUS_DURATION_OF_STUDY_EMPTY_OR_NOT_INTEGER);

        Integer studyDuration = dto.getStudyDuration();
        if (studyDuration == null || studyDuration == DurationOfStudyPropertyEditor.ERROR_VALUE_FOR_DURATION_OF_STUDY) {
            errors.rejectValue("studyDuration", PROSPECTUS_DURATION_OF_STUDY_EMPTY_OR_NOT_INTEGER);
        } else if (studyDuration.equals(DurationOfStudyPropertyEditor.ERROR_UNIT_FOR_DURATION_OF_STUDY)) {
            errors.rejectValue("studyDuration", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "active", EMPTY_DROPDOWN_ERROR_MESSAGE);

    }
}
