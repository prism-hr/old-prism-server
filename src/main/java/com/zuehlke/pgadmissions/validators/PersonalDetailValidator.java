package com.zuehlke.pgadmissions.validators;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.LanguageProficiency;
import com.zuehlke.pgadmissions.domain.Nationality;
import com.zuehlke.pgadmissions.domain.PersonalDetail;

@Component
public class PersonalDetailValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return clazz.isAssignableFrom(PersonalDetail.class);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "personalDetails.firstName.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "personalDetails.lastName.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "gender", "personalDetails.gender.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "personalDetails.email.notempty");
		
		if (!errors.hasFieldErrors("email") && !EmailValidator.getInstance().isValid(((PersonalDetail)target).getEmail())) {
			errors.rejectValue("email", "personalDetails.email.invalid");
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "country", "personalDetails.country.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "residenceCountry", "personalDetails.residenceCountry.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "residenceStatus", "personalDetails.residenceStatus.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateOfBirth", "personalDetails.dateOfBirth.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "application", "personalDetails.application.notempty");
		validateCandidateNationalities(target, errors);
		
		validateMaternalGuardianNationalities(target, errors);
	
		validatePaternalGuardianNationalities(target, errors);		
		
		validateLanguageProficiencies(target, errors);
	}

	private void validateLanguageProficiencies(Object target, Errors errors) {
		int numberOfPrimaries;
		if(((PersonalDetail)target).getLanguageProficiencies().isEmpty()){
			errors.rejectValue("languageProficiencies", "personalDetails.languageProficiencies.notempty");
		}
		
		numberOfPrimaries = 0;
		for (LanguageProficiency proficiency : ((PersonalDetail)target).getLanguageProficiencies()) {
			if(proficiency.isPrimary()){
				numberOfPrimaries++;
			}
		}
		if(numberOfPrimaries > 1){
			errors.rejectValue("languageProficiencies", "personalDetails.languageProficiencies.unique");
		}
		if( ((PersonalDetail)target).getLanguageProficiencies().size() > 1 && numberOfPrimaries ==0){
			errors.rejectValue("languageProficiencies", "personalDetails.languageProficiencies.noprimary");
		}
	}

	private void validatePaternalGuardianNationalities(Object target, Errors errors) {
		int numberOfPrimaries;
		numberOfPrimaries = 0;
		for (Nationality nationality : ((PersonalDetail)target).getPaternalGuardianNationalities()) {
			if(nationality.isPrimary()){
				numberOfPrimaries++;
			}
		}
		if(numberOfPrimaries > 1){
			errors.rejectValue("paternalGuardianNationalities", "personalDetails.paternalGuardianNationalities.unique");
		}
		if( ((PersonalDetail)target).getPaternalGuardianNationalities().size() > 1 && numberOfPrimaries ==0){
			errors.rejectValue("paternalGuardianNationalities", "personalDetails.paternalGuardianNationalities.noprimary");
		}
	}

	private void validateMaternalGuardianNationalities(Object target, Errors errors) {
		int numberOfPrimaries;
		
		
		numberOfPrimaries = 0;
		for (Nationality nationality : ((PersonalDetail)target).getMaternalGuardianNationalities()) {
			if(nationality.isPrimary()){
				numberOfPrimaries++;
			}
		}
		if(numberOfPrimaries > 1){
			errors.rejectValue("maternalGuardianNationalities", "personalDetails.maternalGuardianNationalities.unique");
		}
		if( ((PersonalDetail)target).getMaternalGuardianNationalities().size() > 1 && numberOfPrimaries ==0){
			errors.rejectValue("maternalGuardianNationalities", "personalDetails.maternalGuardianNationalities.noprimary");
		}
	}

	private void validateCandidateNationalities(Object target, Errors errors) {
		if(((PersonalDetail)target).getCandidateNationalities().isEmpty()){
			errors.rejectValue("candidateNationalities", "personalDetails.candidateNationalities.notempty");
		}
		int numberOfPrimaries = 0;
		for (Nationality nationality : ((PersonalDetail)target).getCandidateNationalities()) {
			if(nationality.isPrimary()){
				numberOfPrimaries++;
			}
		}
		if(numberOfPrimaries > 1){
			errors.rejectValue("candidateNationalities", "personalDetails.candidateNationalities.unique");
		}
		if( ((PersonalDetail)target).getCandidateNationalities().size() > 1 && numberOfPrimaries ==0){
			errors.rejectValue("candidateNationalities", "personalDetails.candidateNationalities.noprimary");
		}
	}

}
