package com.zuehlke.pgadmissions.validators;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.services.UserService;
import com.zuehlke.pgadmissions.utils.EncryptionUtils;
@Service
public class AccountValidator implements Validator {

	private static final int MINIMUM_PASSWORD_CHARACTERS = 8;
	private static final int MAXIMUM_PASSWORD_CHARACTERS = 15;

	private UserService userService;
	private final EncryptionUtils encryptionUtils;

	AccountValidator() {
		this(null, null);
	}

	@Autowired
	public AccountValidator(UserService userService, EncryptionUtils encryptionUtils) {
		this.userService = userService;
		this.encryptionUtils = encryptionUtils;
	}


	@Override
	public boolean supports(Class<?> clazz) {
		return RegisteredUser.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		RegisteredUser updatedUser = (RegisteredUser) target;
		RegisteredUser existingUser = getCurrentUser();
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "text.field.empty");
		
		if(StringUtils.isBlank(updatedUser.getPassword()) && ( StringUtils.isNotBlank(updatedUser.getNewPassword()) || StringUtils.isNotBlank(updatedUser.getConfirmPassword()))){
			errors.rejectValue("password", "text.field.empty");
		}
		if(StringUtils.isBlank(updatedUser.getConfirmPassword()) && ( StringUtils.isNotBlank(updatedUser.getNewPassword()) || StringUtils.isNotBlank(updatedUser.getPassword()))){
			errors.rejectValue("confirmPassword", "text.field.empty");
		}
		if(StringUtils.isBlank(updatedUser.getNewPassword()) && ( StringUtils.isNotBlank(updatedUser.getConfirmPassword()) || StringUtils.isNotBlank(updatedUser.getPassword()))){
			errors.rejectValue("newPassword", "text.field.empty");
		}
		boolean passwordFieldsFilled = StringUtils.isNotBlank(updatedUser.getConfirmPassword()) && StringUtils.isNotBlank(updatedUser.getNewPassword()) && StringUtils.isNotBlank(updatedUser.getPassword());
		if(passwordFieldsFilled && !encryptionUtils.getMD5Hash(updatedUser.getPassword()).equals(existingUser.getPassword())){
			errors.rejectValue("password", "account.currentpassword.notmatch");
		}
		
		if(passwordFieldsFilled && !updatedUser.getConfirmPassword().equals(updatedUser.getNewPassword())){
			errors.rejectValue("newPassword", "user.passwords.notmatch");
			errors.rejectValue("confirmPassword", "user.passwords.notmatch");
		}

		if(passwordFieldsFilled && updatedUser.getNewPassword().length() < MINIMUM_PASSWORD_CHARACTERS){
			errors.rejectValue("newPassword", "user.password.small");
		}
		
		if(passwordFieldsFilled && updatedUser.getNewPassword().length() > MAXIMUM_PASSWORD_CHARACTERS){
			errors.rejectValue("newPassword", "user.password.large");
		}
		
		if(passwordFieldsFilled && !updatedUser.getNewPassword().matches("[a-zA-Z0-9+]+")){
			errors.rejectValue("newPassword", "user.password.nonalphanumeric");
		}

		if(passwordFieldsFilled && encryptionUtils.getMD5Hash(updatedUser.getNewPassword()).equals(existingUser.getPassword())){
			errors.rejectValue("newPassword", "account.newPassword.same");
		}
		
		List<RegisteredUser> allUsers = userService.getAllUsers();
		allUsers.remove(existingUser);
		for (RegisteredUser user : allUsers) {
			if(user.getUsername().equals(updatedUser.getEmail()))
				errors.rejectValue("email", "account.email.alreadyexists");
		}
		if (!EmailValidator.getInstance().isValid(updatedUser.getEmail())) {
			errors.rejectValue("email", "text.email.notvalid");
		}
		
	}

	public RegisteredUser getCurrentUser() {
		return userService.getCurrentUser();
	}

}
