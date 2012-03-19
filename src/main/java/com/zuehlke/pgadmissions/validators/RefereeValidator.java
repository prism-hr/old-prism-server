package com.zuehlke.pgadmissions.validators;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.Referee;

@Service
public class RefereeValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Referee.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Referee referee = (Referee) target;
		if (!EmailValidator.getInstance().isValid(referee.getEmail())) {
			errors.rejectValue("email", "referee.email.invalid");
		}
		if((referee.getAddressCountry()==null) && ( referee.getAddressLocation()!=null && !referee.getAddressLocation().isEmpty() || referee.getAddressPostcode()!=null && !referee.getAddressPostcode().isEmpty() )){
					errors.rejectValue("addressCountry", "referee.addressCountry.notempty");
		}
		if((referee.getAddressLocation() == null || referee.getAddressLocation().isEmpty() )  && (referee.getAddressCountry()!=null || referee.getAddressPostcode()!=null && !referee.getAddressPostcode().isEmpty())){
			errors.rejectValue("addressLocation", "referee.addressLocation.notempty");
		}
		if((referee.getAddressPostcode() == null || referee.getAddressPostcode().isEmpty()) && (referee.getAddressLocation()!=null && !referee.getAddressLocation().isEmpty() || referee.getAddressLocation()!=null && !referee.getAddressLocation().isEmpty())){
			errors.rejectValue("addressPostcode", "referee.addressPostcode.notempty");
		}
		if((referee.getJobEmployer()==null || referee.getJobEmployer().isEmpty()) && (referee.getJobTitle()!=null && !referee.getJobTitle().isEmpty())){
			errors.rejectValue("jobEmployer", "referee.jobEmployer.notempty");
		}
		if((referee.getJobEmployer()!=null && !referee.getJobEmployer().isEmpty()) && (referee.getJobTitle()==null || referee.getJobTitle().isEmpty())){
			errors.rejectValue("jobTitle", "referee.jobTitle.notempty");
		}
		if(referee.getPhoneNumbers()==null || referee.getPhoneNumbers().size() == 0){
			errors.rejectValue("phoneNumbers", "referee.phoneNumbers.notempty");
		}
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstname", "referee.firstname.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastname", "referee.lastname.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "relationship", "referee.relationship.notempty");
	}


}
