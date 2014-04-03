package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
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

        LocalDate baseline = new LocalDate();
        Date today = baseline.toDate();
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gender", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstNationality", EMPTY_DROPDOWN_ERROR_MESSAGE);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "residenceCountry", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateOfBirth", EMPTY_FIELD_ERROR_MESSAGE);
        String dob = personalDetail.getDateOfBirth() == null ? "" : personalDetail.getDateOfBirth().toString();
        if (StringUtils.isNotBlank(dob) && personalDetail.getDateOfBirth().after(today)) {
            errors.rejectValue("dateOfBirth", "date.field.notpast");
        } else if (personalDetail.getDateOfBirth() != null) {
            int age = Years.yearsBetween(new DateTime(personalDetail.getDateOfBirth()).withTimeAtStartOfDay(), new DateTime()).getYears();
            if (!(age >= 10 && age <= 80)) {
                DateTime now = new DateTime().withTimeAtStartOfDay();
                DateTime tenYearsAgo = now.toDateTime().minusYears(10);
                DateTime eightyYearsAgo = now.toDateTime().minusYears(81).plusDays(1);
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

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "application", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "englishFirstLanguage", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "requiresVisa", EMPTY_DROPDOWN_ERROR_MESSAGE);

        if (BooleanUtils.isFalse(personalDetail.getEnglishFirstLanguage())) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "languageQualificationAvailable", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }

        if (BooleanUtils.isTrue(personalDetail.getPassportAvailable()) && BooleanUtils.isTrue(personalDetail.getRequiresVisa())) {
            try {
                errors.pushNestedPath("passportInformation");
                ValidationUtils.invokeValidator(passportInformationValidator, personalDetail.getPassport(), errors);
            } finally {
                errors.popNestedPath();
            }

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
    }

}
