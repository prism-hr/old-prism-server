package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.PersonalDetails;
import com.zuehlke.pgadmissions.utils.DateUtils;

@Component
public class PersonalDetailsValidator extends FormSectionObjectValidator implements Validator {

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
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "disability", "dropdown.radio.select.none");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "ethnicity", "dropdown.radio.select.none");
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "application", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "englishFirstLanguage", "dropdown.radio.select.none");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "requiresVisa", "dropdown.radio.select.none");
		
		if (personalDetail.getRequiresVisa() != null && personalDetail.getRequiresVisa()) {
		    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passportNumber", "text.field.empty");
		    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nameOnPassport", "text.field.empty");
		    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passportIssueDate", "text.field.empty");
		    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passportExpiryDate", "text.field.empty");
		    
		    Date passportExpiryDate = personalDetail.getPassportExpiryDate();
		    Date passportIssueDate = personalDetail.getPassportIssueDate();
		    
		    if (passportExpiryDate != null) {
		        if (!DateUtils.isToday(passportExpiryDate) && passportExpiryDate.before(new Date())) {
		            errors.rejectValue("passportExpiryDate", "date.field.notfuture");
		        }
		    }
		    
		    if (passportIssueDate != null) {
                if (!DateUtils.isToday(passportIssueDate) && passportIssueDate.after(new Date())) {
                    errors.rejectValue("passportIssueDate", "date.field.notpast");
                }
            }
		    
		    if (passportExpiryDate != null && passportIssueDate != null) {
		        if (org.apache.commons.lang.time.DateUtils.isSameDay(passportExpiryDate, passportIssueDate)) {
		            errors.rejectValue("passportExpiryDate", "date.field.same");
		            errors.rejectValue("passportIssueDate", "date.field.same");
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
