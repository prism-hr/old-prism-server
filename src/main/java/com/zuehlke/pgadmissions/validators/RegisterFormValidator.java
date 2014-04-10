package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.google.common.base.Objects;
import com.zuehlke.pgadmissions.domain.User;
import com.zuehlke.pgadmissions.domain.UserAccount;
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
		return User.class.equals(clazz);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
	    User user = (User) target;
	    UserAccount account = user.getAccount();

	    if (StringUtils.isEmpty(user.getActivationCode())) {
	        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", EMPTY_FIELD_ERROR_MESSAGE);
	        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", EMPTY_FIELD_ERROR_MESSAGE);	        
	        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", EMPTY_FIELD_ERROR_MESSAGE);	        
	        if (!StringUtils.isBlank(user.getEmail())) {
	            User userWithSameEmail = userService.getUserByEmailIncludingDisabledAccounts(user.getEmail());
	            if (userWithSameEmail != null && !userWithSameEmail.getId().equals(user.getId())) {
	                errors.rejectValue("email", "user.email.alreadyexists");
	            }
	        }
	    }

	    errors.pushNestedPath("account");
		if(StringUtils.isBlank(account.getPassword())){
		    errors.rejectValue("password", EMPTY_FIELD_ERROR_MESSAGE);
		} else if(user.getPassword().length() < MINIMUM_PASSWORD_CHARACTERS){
            errors.rejectValue("password", "user.password.small");
        } else if(user.getPassword().length() > MAXIMUM_PASSWORD_CHARACTERS){
            errors.rejectValue("password", "user.password.large");
        }
		
        if (StringUtils.isBlank(account.getConfirmPassword())) {
            errors.rejectValue("confirmPassword", EMPTY_FIELD_ERROR_MESSAGE);
        } else if (!Objects.equal(account.getConfirmPassword(), user.getPassword())) {
            errors.rejectValue("confirmPassword", "user.passwords.notmatch");
        }
        errors.popNestedPath();
	}
}