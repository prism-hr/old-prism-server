package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.UserService;

@Component
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
	    RegisteredUser record = (RegisteredUser) target;

	    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", EMPTY_FIELD_ERROR_MESSAGE);
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", EMPTY_FIELD_ERROR_MESSAGE);

		// checking for length of password field
		if(StringUtils.isBlank(record.getPassword())){
		    errors.rejectValue("password", EMPTY_FIELD_ERROR_MESSAGE);
		} else if(record.getPassword().length() < MINIMUM_PASSWORD_CHARACTERS){
            errors.rejectValue("password", "user.password.small");
        } else if(record.getPassword().length() > MAXIMUM_PASSWORD_CHARACTERS){
            errors.rejectValue("password", "user.password.large");
        }
		
		// check if confirm password is not empty and if matches the password
		if(StringUtils.isBlank(record.getConfirmPassword())){
		    errors.rejectValue("confirmPassword", EMPTY_FIELD_ERROR_MESSAGE);
		} else if(!Objects.equal(record.getConfirmPassword(), record.getPassword())){
		    errors.rejectValue("confirmPassword", "user.passwords.notmatch");
		}
		
		if(StringUtils.isBlank(record.getEmail())){
		    errors.rejectValue("email", EMPTY_FIELD_ERROR_MESSAGE);
		} else {
		    RegisteredUser userWithSameEmail = userService.getUserByEmailIncludingDisabledAccounts(record.getEmail());
		    if (userWithSameEmail != null && !userWithSameEmail.getId().equals(record.getId())) {
		        errors.rejectValue("email", "user.email.alreadyexists");
		    }
		}
		
	}
}
