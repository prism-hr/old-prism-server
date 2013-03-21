package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Qualification;

@Component
public class QualificationValidator extends FormSectionObjectValidator implements Validator {

    private static final int MAX_NUMBER_OF_POSITIONS = 6;

    @Override
    public boolean supports(Class<?> clazz) {
        return Qualification.class.equals(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {
        super.addExtraValidation(target, errors);

        Date today = new Date();
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationInstitution", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationSubject", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationStartDate", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationLanguage", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "institutionCountry", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationType", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationGrade", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "qualificationAwardDate", EMPTY_FIELD_ERROR_MESSAGE);

        Qualification qualification = (Qualification) target;

        if (qualification.getQualificationStartDate() != null && qualification.getQualificationAwardDate() != null) {
            if (qualification.getQualificationStartDate().after(qualification.getQualificationAwardDate())) {
                errors.rejectValue("qualificationStartDate", "qualification.start_date.notvalid");
            }
        }

        if (qualification.getQualificationStartDate() != null && qualification.getQualificationStartDate().after(today)) {
            errors.rejectValue("qualificationStartDate", "date.field.notpast");
        }

        if (qualification.isQualificationCompleted()) {
            if (qualification.getQualificationAwardDate() != null && qualification.getQualificationAwardDate().after(today)) {
                errors.rejectValue("qualificationAwardDate", "date.field.notpast");
            }
        } else {
            if (qualification.getQualificationAwardDate() != null && qualification.getQualificationAwardDate().before(today)) {
                errors.rejectValue("qualificationAwardDate", "date.field.notfuture");
            }
        }

        if (qualification.getApplication().getEmploymentPositions().size() >= MAX_NUMBER_OF_POSITIONS + 1) {
            errors.reject("");
        }

        if (StringUtils.equalsIgnoreCase("Select...", qualification.getQualificationInstitution())) {
            errors.reject("qualificationInstitution", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }

        String institutionCode = qualification.getQualificationInstitutionCode();
        if (StringUtils.isNotBlank(institutionCode) && StringUtils.equalsIgnoreCase("OTHER", institutionCode)) {
            if (StringUtils.isBlank(qualification.getOtherQualificationInstitution())) {
                errors.rejectValue("otherQualificationInstitution", EMPTY_FIELD_ERROR_MESSAGE);
            }
        }
    }
}
