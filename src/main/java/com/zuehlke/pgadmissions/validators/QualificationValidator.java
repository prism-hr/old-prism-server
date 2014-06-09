package com.zuehlke.pgadmissions.validators;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.ApplicationQualification;

@Component
public class QualificationValidator extends AbstractValidator {

    private static final int MAX_NUMBER_OF_POSITIONS = 6;

    @Override
    public boolean supports(Class<?> clazz) {
        return ApplicationQualification.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        LocalDate today = new LocalDate();
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationInstitution", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationInstitutionCode", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationSubject", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationStartDate", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationLanguage", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "institutionCountry", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationType", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationGrade", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationAwardDate", EMPTY_FIELD_ERROR_MESSAGE);

        ApplicationQualification qualification = (ApplicationQualification) target;

        if (qualification.getStartDate() != null) {
            if (qualification.getStartDate().isAfter(today)) {
                errors.rejectValue("qualificationStartDate", "date.field.notpast");
            } else if (qualification.getAwardDate() != null
                    && qualification.getStartDate().isAfter(qualification.getAwardDate())) {
                errors.rejectValue("qualificationStartDate", "qualification.start_date.notvalid");
            }
        }

        if (qualification.getCompleted()) {
            if (qualification.getAwardDate() != null && qualification.getAwardDate().isAfter(today)) {
                errors.rejectValue("qualificationAwardDate", "date.field.notpast");
            }
        } else {
            if (qualification.getAwardDate() != null && qualification.getAwardDate().isAfter(today)) {
                errors.rejectValue("qualificationAwardDate", "date.field.notfuture");
            }
        }

        if (qualification.getApplication().getApplicationEmploymentPositions().size() >= MAX_NUMBER_OF_POSITIONS + 1) {
            errors.reject("");
        }

        ValidationUtils.rejectIfEmpty(errors, "qualificationInstitution", EMPTY_DROPDOWN_ERROR_MESSAGE);
        
    }
}
