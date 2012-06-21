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
	private static final int MINIMUM_PASSWORD_CHARACTERS = 8;
	private static final int MAXIMUM_PASSWORD_CHARACTERS = 15;
	
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
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "confirmPassword", "text.field.empty");
		RegisteredUser record = (RegisteredUser) target;
		if(record.getConfirmPassword()!=null && record.getPassword() !=null && !record.getConfirmPassword().equals(record.getPassword())){
			errors.rejectValue("password", "user.passwords.notmatch");
			errors.rejectValue("confirmPassword", "user.passwords.notmatch");
		}

		if(record.getPassword().length() < MINIMUM_PASSWORD_CHARACTERS){
			errors.rejectValue("password", "user.password.small");
		}
		
		if(record.getPassword().length() > MAXIMUM_PASSWORD_CHARACTERS){
			errors.rejectValue("password", "user.password.large");
		}
		
		if(!record.getPassword().matches("[a-zA-Z0-9+]+")){
			errors.rejectValue("password", "user.password.nonalphanumeric");
		}
		
		if (shouldValidateSameEmail) {
			List<RegisteredUser> allUsers = userService.getAllUsers();
			for (RegisteredUser user : allUsers) {
				if(user.getEmail().equals(record.getEmail()))
					errors.rejectValue("email", "user.email.alreadyexists");
			}
		}
		if (!EmailValidator.getInstance().isValid(record.getEmail())) {
			errors.rejectValue("email", "text.email.notvalid");
		}
	}

}
