package com.zuehlke.pgadmissions.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.Advert;

@Component
public class ProgramAdvertValidator extends AbstractValidator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Advert.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        Advert programAdvert = (Advert) target;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "studyDuration", PROSPECTUS_DURATION_OF_STUDY_EMPTY_OR_NOT_INTEGER);
        validateStudyDuration(errors, programAdvert.getStudyDuration());
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "active", EMPTY_DROPDOWN_ERROR_MESSAGE);
    }
    
}