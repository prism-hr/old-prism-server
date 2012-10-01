package com.zuehlke.pgadmissions.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.UserService;
@Service
public class RegisterFormValidator extends AbstractValidator {

	private UserService userService;
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

	@Override
	public void addExtraValidation(Object target, Errors errors) {
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
		
		RegisteredUser userWithSameEmail = userService.getUserByEmail(record.getEmail());
		if (userWithSameEmail != null && !record.equals(userWithSameEmail)) {
		    errors.rejectValue("email", "user.email.alreadyexists");
		}
	}
}
