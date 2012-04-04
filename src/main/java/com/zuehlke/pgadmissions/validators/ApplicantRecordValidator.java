package com.zuehlke.pgadmissions.validators;

import java.util.List;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.dto.RegistrationDTO;
import com.zuehlke.pgadmissions.services.UserService;
@Service
public class ApplicantRecordValidator implements Validator {

	private UserService userService;
	private boolean shouldValidateSameEmail;

	ApplicantRecordValidator() {
		this(null);
	}

	@Autowired
	public ApplicantRecordValidator(UserService userService) {
		this.userService = userService;
	}


	@Override
	public boolean supports(Class<?> clazz) {
		return RegistrationDTO.class.equals(clazz);
	}

	public boolean shouldValidateSameEmail() {
		return shouldValidateSameEmail;
	}

	public void shouldValidateSameEmail(boolean validate) {
		this.shouldValidateSameEmail = validate;
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstname", "record.firstname.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastname", "record.lastname.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "record.password.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "record.confirmPassword.notempty");
		RegistrationDTO record = (RegistrationDTO) target;
		if(record.getConfirmPassword()!=null && record.getPassword() !=null && !record.getConfirmPassword().equals(record.getPassword())){
			errors.rejectValue("password", "record.password.notmatch");
			errors.rejectValue("confirmPassword", "record.confirmPassword.notmatch");
		}
		if(record.getPassword().length()<8){
			errors.rejectValue("password", "record.password.notvalid");
		}
		if (shouldValidateSameEmail) {
			List<RegisteredUser> allUsers = userService.getAllUsers();
			for (RegisteredUser user : allUsers) {
				if(user.getEmail().equals(record.getEmail()))
					errors.rejectValue("email", "record.email.alreadyexists");
			}
		}
		if (!EmailValidator.getInstance().isValid(record.getEmail())) {
			errors.rejectValue("email", "record.email.invalid");
		}
	}

}
