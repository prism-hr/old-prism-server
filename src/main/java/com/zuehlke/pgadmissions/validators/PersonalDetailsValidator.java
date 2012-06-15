package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.PersonalDetails;

@Component
public class PersonalDetailsValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(PersonalDetails.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		PersonalDetails personalDetail = (PersonalDetails) target;
		Date today = new Date();
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gender", "dropdown.radio.select.none");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber", "text.field.empty");
		
		validateCandidateNationalities(target, errors);
		
		if (!errors.hasFieldErrors("email") && !EmailValidator.getInstance().isValid(((PersonalDetails)target).getEmail())) {
			errors.rejectValue("email", "text.email.notvalid");
		}
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
	}

	private void validateCandidateNationalities(Object target, Errors errors) {
		if(((PersonalDetails)target).getCandidateNationalities().isEmpty()){
			errors.rejectValue("candidateNationalities", "dropdown.radio.select.none");
		}
	}



}
