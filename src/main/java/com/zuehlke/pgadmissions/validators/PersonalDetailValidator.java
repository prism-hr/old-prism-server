package com.zuehlke.pgadmissions.validators;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.PersonalDetail;
import com.zuehlke.pgadmissions.dto.Address;

@Component
public class PersonalDetailValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(PersonalDetail.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		PersonalDetail personalDetail = (PersonalDetail) target;
		Date today = new Date();
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "personalDetails.firstName.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "personalDetails.lastName.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gender", "personalDetails.gender.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "personalDetails.email.notempty");
		
		validateCandidateNationalities(target, errors);
		
		if (!errors.hasFieldErrors("email") && !EmailValidator.getInstance().isValid(((PersonalDetail)target).getEmail())) {
			errors.rejectValue("email", "personalDetails.email.invalid");
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", "personalDetails.country.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "residenceCountry", "personalDetails.residenceCountry.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateOfBirth", "personalDetails.dateOfBirth.notempty");
		String dob = personalDetail.getDateOfBirth() == null ? "": personalDetail.getDateOfBirth().toString();
		if (StringUtils.isNotBlank(dob) && personalDetail.getDateOfBirth().after(today)) {
			errors.rejectValue("dateOfBirth", "personalDetails.dateOfBirth.future");
		}
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "application", "personalDetails.application.notempty");
	}

	private void validateCandidateNationalities(Object target, Errors errors) {
		if(((PersonalDetail)target).getCandidateNationalities().isEmpty()){
			errors.rejectValue("candidateNationalities", "personalDetails.candidateNationalities.notempty");
		}
	}



}
