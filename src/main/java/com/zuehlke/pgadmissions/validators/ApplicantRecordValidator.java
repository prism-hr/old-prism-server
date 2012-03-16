package com.zuehlke.pgadmissions.validators;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.dto.ApplicantRecordDTO;
@Service
public class ApplicantRecordValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return ApplicantRecordDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstname", "record.firstname.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastname", "record.lastname.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "record.email.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "record.password.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "record.confirmPassword.notempty");
		ApplicantRecordDTO record = (ApplicantRecordDTO) target;
		if(record.getConfirmPassword()!=null && record.getPassword() !=null && !record.getConfirmPassword().equals(record.getPassword())){
			errors.rejectValue("password", "record.password.notvalid");
			errors.rejectValue("confirmPassword", "record.confirmPassword.notvalid");
		}
		if (!EmailValidator.getInstance().isValid(record.getEmail())) {
			errors.rejectValue("email", "record.email.invalid");
		}
	}

}
