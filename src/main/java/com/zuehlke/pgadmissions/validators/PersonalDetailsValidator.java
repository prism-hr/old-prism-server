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
    public PersonalDetailsValidator(PassportInformationValidator passportInformationValidator,
            LanguageQualificationValidator languageQualificationValidator) {
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
		if(personalDetail.getApplication() != null){
			super.addExtraValidation(target, errors);
		}
		
		Date today = new Date();
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "dropdown.radio.select.none");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gender", "dropdown.radio.select.none");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber", "text.field.empty");
		
		validateCandidateNationalities(target, errors);
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", "dropdown.radio.select.none");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "residenceCountry", "dropdown.radio.select.none");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateOfBirth", "text.field.empty");
		String dob = personalDetail.getDateOfBirth() == null ? "": personalDetail.getDateOfBirth().toString();
		if (StringUtils.isNotBlank(dob) && personalDetail.getDateOfBirth().after(today)) {
			errors.rejectValue("dateOfBirth", "date.field.notpast");
		} else if (personalDetail.getDateOfBirth() != null) {
            int age = Years.yearsBetween(new DateMidnight(personalDetail.getDateOfBirth()), new DateMidnight(new Date())).getYears();
            if (!(age >= 10 && age <= 80)) {
                DateMidnight now = new DateMidnight();
                DateTime tenYearsAgo = now.toDateTime().minusYears(10);
                DateTime eightyYearsAgo = now.toDateTime().minusYears(81).plusDays(1);
                errors.rejectValue("dateOfBirth", "date.field.age", new Object[] {eightyYearsAgo.toString("dd-MMM-yyyy"), tenYearsAgo.toString("dd-MMM-yyyy")}, null);
            }
        }
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "disability", "dropdown.radio.select.none");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "ethnicity", "dropdown.radio.select.none");
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "application", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "englishFirstLanguage", "dropdown.radio.select.none");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "requiresVisa", "dropdown.radio.select.none");
		
		if (BooleanUtils.isTrue(personalDetail.getPassportAvailable() && BooleanUtils.isTrue(personalDetail.getRequiresVisa()))) {
		    try {
		        errors.pushNestedPath("passportInformation");
    	        ValidationUtils.invokeValidator(passportInformationValidator, personalDetail.getPassportInformation(), errors);
		    } finally {
		        errors.popNestedPath();
		    }
		}
		
		if (BooleanUtils.isTrue(personalDetail.getLanguageQualificationAvailable())) {
		    if (personalDetail.getLanguageQualifications().isEmpty()) {
		        errors.rejectValue("languageQualifications", "text.field.empty");
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
		if(((PersonalDetails)target).getCandidateNationalities().isEmpty()){
			errors.rejectValue("candidateNationalities", "dropdown.radio.select.none");
		}
	}
}
