package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.BooleanUtils;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.PersonalDetails;

@Component
public class PersonalDetailsValidator extends FormSectionObjectValidator implements Validator {

    private PassportValidator passportInformationValidator;

    private LanguageQualificationValidator languageQualificationValidator;

    public PersonalDetailsValidator() {
    }

    @Autowired
    public PersonalDetailsValidator(PassportValidator passportInformationValidator, LanguageQualificationValidator languageQualificationValidator) {
        this.passportInformationValidator = passportInformationValidator;
        this.languageQualificationValidator = languageQualificationValidator;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return PersonalDetails.class.isAssignableFrom(clazz);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {

        PersonalDetails personalDetail = (PersonalDetails) target;
        if (personalDetail.getApplication() != null) {
            super.addExtraValidation(target, errors);
        }

        LocalDate today = new LocalDate();
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gender", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstNationality", EMPTY_DROPDOWN_ERROR_MESSAGE);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "residenceCountry", EMPTY_DROPDOWN_ERROR_MESSAGE);

        if (personalDetail.getDateOfBirth() == null) {
            errors.rejectValue("dateOfBirth", EMPTY_FIELD_ERROR_MESSAGE);
        } else if (personalDetail.getDateOfBirth().isAfter(today)) {
            errors.rejectValue("dateOfBirth", "date.field.notpast");
        } else {
            int age = Years.yearsBetween(personalDetail.getDateOfBirth(), new LocalDate()).getYears();
            if (!(age >= 10 && age <= 80)) {
                LocalDate now = new LocalDate();
                LocalDate tenYearsAgo = now.minusYears(10);
                LocalDate eightyYearsAgo = now.minusYears(81).plusDays(1);
                errors.rejectValue("dateOfBirth", "date.field.age",
                        new Object[] { eightyYearsAgo.toString("dd-MMM-yyyy"), tenYearsAgo.toString("dd-MMM-yyyy") }, null);
            }
        }

        if (personalDetail.getFirstNationality() != null && personalDetail.getSecondNationality() != null
                && personalDetail.getFirstNationality().getId().equals(personalDetail.getSecondNationality().getId())) {
            errors.rejectValue("secondNationality", "nationality.duplicate", new Object[] {}, null);
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "disability", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "ethnicity", EMPTY_DROPDOWN_ERROR_MESSAGE);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "englishFirstLanguage", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "requiresVisa", EMPTY_DROPDOWN_ERROR_MESSAGE);

        if (BooleanUtils.isFalse(personalDetail.getEnglishFirstLanguage())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "languageQualificationAvailable", EMPTY_DROPDOWN_ERROR_MESSAGE);

            if (BooleanUtils.isTrue(personalDetail.getLanguageQualificationAvailable())) {
                if (personalDetail.getLanguageQualification() == null) {
                    errors.rejectValue("languageQualification", EMPTY_FIELD_ERROR_MESSAGE);
                } else {
                    errors.pushNestedPath("languageQualification");
                    ValidationUtils.invokeValidator(languageQualificationValidator, personalDetail.getLanguageQualification(), errors);
                    errors.popNestedPath();
                }
            }
        }

        if (BooleanUtils.isTrue(personalDetail.getPassportAvailable()) && BooleanUtils.isTrue(personalDetail.getRequiresVisa())) {
            try {
                errors.pushNestedPath("passport");
                ValidationUtils.invokeValidator(passportInformationValidator, personalDetail.getPassport(), errors);
            } finally {
                errors.popNestedPath();
            }

        }
    }

}
