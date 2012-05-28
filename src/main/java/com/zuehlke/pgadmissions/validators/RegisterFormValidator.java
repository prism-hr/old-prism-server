package com.zuehlke.pgadmissions.validators;

import java.util.List;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.UserService;
@Service
public class RegisterFormValidator implements Validator {

	private UserService userService;
	private boolean shouldValidateSameEmail;

	RegisterFormValidator() {
		this(null);
	}

	@Autowired
	public RegisterFormValidator(UserService userService) {
		this.userService = userService;
	}


	@Override
	public boolean supports(Class<?> clazz) {
		return RegisteredUser.class.equals(clazz);
	}

	public boolean shouldValidateSameEmail() {
		return shouldValidateSameEmail;
	}

	public void shouldValidateSameEmail(boolean validate) {
		this.shouldValidateSameEmail = validate;
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "user.firstName.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "user.lastName.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "user.password.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "user.confirmPassword.notempty");
		RegisteredUser record = (RegisteredUser) target;
		if(record.getConfirmPassword()!=null && record.getPassword() !=null && !record.getConfirmPassword().equals(record.getPassword())){
			errors.rejectValue("password", "user.password.notmatch");
			errors.rejectValue("confirmPassword", "user.confirmPassword.notmatch");
		}

		if(record.getPassword().length()<8){
			errors.rejectValue("password", "user.password.notvalid");
		}

		if (shouldValidateSameEmail) {
			List<RegisteredUser> allUsers = userService.getAllUsers();
			for (RegisteredUser user : allUsers) {
				if(user.getEmail().equals(record.getEmail()))
					errors.rejectValue("email", "user.email.alreadyexists");
			}
		}
		if (!EmailValidator.getInstance().isValid(record.getEmail())) {
			errors.rejectValue("email", "user.email.invalid");
		}
	}

}
