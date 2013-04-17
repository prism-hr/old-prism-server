package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.PersonalDetails;

@Component
public class PersonalDetailsValidator extends FormSectionObjectValidator implements Validator {

    private PassportInformationValidator passportInformationValidator;

    private LanguageQualificationValidator languageQualificationValidator;

    public PersonalDetailsValidator() {
        this(null, null);
    }

    @Autowired
    public PersonalDetailsValidator(PassportInformationValidator passportInformationValidator, LanguageQualificationValidator languageQualificationValidator) {
        this.passportInformationValidator = passportInformationValidator;
        this.languageQualificationValidator = languageQualificationValidator;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(PersonalDetails.class);
    }

    @Override
    public void addExtraValidation(Object target, Errors errors) {

        PersonalDetails personalDetail = (PersonalDetails) target;
        if (personalDetail.getApplication() != null) {
            super.addExtraValidation(target, errors);
        }

        Date today = new Date();
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gender", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber", EMPTY_FIELD_ERROR_MESSAGE);
        validateCandidateNationalities(target, errors);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "residenceCountry", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateOfBirth", EMPTY_FIELD_ERROR_MESSAGE);
        String dob = personalDetail.getDateOfBirth() == null ? "" : personalDetail.getDateOfBirth().toString();
        if (StringUtils.isNotBlank(dob) && personalDetail.getDateOfBirth().after(today)) {
            errors.rejectValue("dateOfBirth", "date.field.notpast");
        } else if (personalDetail.getDateOfBirth() != null) {
            int age = Years.yearsBetween(new DateMidnight(personalDetail.getDateOfBirth()), new DateMidnight(new Date())).getYears();
            if (!(age >= 10 && age <= 80)) {
                DateMidnight now = new DateMidnight();
                DateTime tenYearsAgo = now.toDateTime().minusYears(10);
                DateTime eightyYearsAgo = now.toDateTime().minusYears(81).plusDays(1);
                errors.rejectValue("dateOfBirth", "date.field.age",
                        new Object[] { eightyYearsAgo.toString("dd-MMM-yyyy"), tenYearsAgo.toString("dd-MMM-yyyy") }, null);
            }
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "disability", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "ethnicity", EMPTY_DROPDOWN_ERROR_MESSAGE);

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "application", EMPTY_FIELD_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "englishFirstLanguage", EMPTY_DROPDOWN_ERROR_MESSAGE);
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "requiresVisa", EMPTY_DROPDOWN_ERROR_MESSAGE);
        
        if(BooleanUtils.isFalse(personalDetail.getEnglishFirstLanguage())){
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "languageQualificationAvailable", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }

        if (BooleanUtils.isTrue(personalDetail.getPassportAvailable()) && BooleanUtils.isTrue(personalDetail.getRequiresVisa())) {
            try {
                errors.pushNestedPath("passportInformation");
                ValidationUtils.invokeValidator(passportInformationValidator, personalDetail.getPassportInformation(), errors);
            } finally {
                errors.popNestedPath();
            }
        }

        if (BooleanUtils.isTrue(personalDetail.getLanguageQualificationAvailable())) {
            if (personalDetail.getLanguageQualifications().isEmpty()) {
                errors.rejectValue("languageQualifications", EMPTY_FIELD_ERROR_MESSAGE);
            } else {
                for (int idx = 0; idx < personalDetail.getLanguageQualifications().size(); idx++) {
                    errors.pushNestedPath(String.format("languageQualifications[%s]", idx));
                    ValidationUtils.invokeValidator(languageQualificationValidator, personalDetail.getLanguageQualifications().get(idx), errors);
                    errors.popNestedPath();
                }
            }
        }
    }

    private void validateCandidateNationalities(Object target, Errors errors) {
        if (((PersonalDetails) target).getCandidateNationalities().isEmpty()) {
            errors.rejectValue("candidateNationalities", EMPTY_DROPDOWN_ERROR_MESSAGE);
        }
    }
}
