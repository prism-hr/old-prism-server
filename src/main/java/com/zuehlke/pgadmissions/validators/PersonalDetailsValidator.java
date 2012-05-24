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
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "personalDetails.firstName.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "personalDetails.lastName.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gender", "personalDetails.gender.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "personalDetails.email.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber", "personalDetails.phoneNumber.notempty");
		
		validateCandidateNationalities(target, errors);
		
		if (!errors.hasFieldErrors("email") && !EmailValidator.getInstance().isValid(((PersonalDetails)target).getEmail())) {
			errors.rejectValue("email", "personalDetails.email.invalid");
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", "personalDetails.country.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "residenceCountry", "personalDetails.residenceCountry.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateOfBirth", "personalDetails.dateOfBirth.notempty");
		String dob = personalDetail.getDateOfBirth() == null ? "": personalDetail.getDateOfBirth().toString();
		if (StringUtils.isNotBlank(dob) && personalDetail.getDateOfBirth().after(today)) {
			errors.rejectValue("dateOfBirth", "personalDetails.dateOfBirth.future");
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "disability", "personalDetails.disability.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "ethnicity", "personalDetails.ethnicity.notempty");
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "application", "personalDetails.application.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "englishFirstLanguage", "personalDetails.englishFirstLanguage.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "requiresVisa", "personalDetails.requiresVisa.notempty");
	}

	private void validateCandidateNationalities(Object target, Errors errors) {
		if(((PersonalDetails)target).getCandidateNationalities().isEmpty()){
			errors.rejectValue("candidateNationalities", "personalDetails.candidateNationalities.notempty");
		}
	}



}
