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
		
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "user.firstName.notempty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "user.lastName.notempty");
		
		if(StringUtils.isBlank(updatedUser.getPassword()) && ( StringUtils.isNotBlank(updatedUser.getNewPassword()) || StringUtils.isNotBlank(updatedUser.getConfirmPassword()))){
			errors.rejectValue("password", "account.password.notempty");
		}
		if(StringUtils.isBlank(updatedUser.getConfirmPassword()) && ( StringUtils.isNotBlank(updatedUser.getNewPassword()) || StringUtils.isNotBlank(updatedUser.getPassword()))){
			errors.rejectValue("confirmPassword", "account.confirmPassword.notempty");
		}
		if(StringUtils.isBlank(updatedUser.getNewPassword()) && ( StringUtils.isNotBlank(updatedUser.getConfirmPassword()) || StringUtils.isNotBlank(updatedUser.getPassword()))){
			errors.rejectValue("newPassword", "account.newPassword.notempty");
		}
		boolean passwordFieldsFilled = StringUtils.isNotBlank(updatedUser.getConfirmPassword()) && StringUtils.isNotBlank(updatedUser.getNewPassword()) && StringUtils.isNotBlank(updatedUser.getPassword());
		if(passwordFieldsFilled && !encryptionUtils.getMD5Hash(updatedUser.getPassword()).equals(existingUser.getPassword())){
			errors.rejectValue("password", "account.currentpassword.notmatch");
		}
		
		if(passwordFieldsFilled && !updatedUser.getConfirmPassword().equals(updatedUser.getNewPassword())){
			errors.rejectValue("newPassword", "account.newPassword.notmatch");
			errors.rejectValue("confirmPassword", "account.confirmPassword.notmatch");
		}

		if(passwordFieldsFilled && updatedUser.getNewPassword().length() < MINIMUM_PASSWORD_CHARACTERS){
			errors.rejectValue("newPassword", "account.newPassword.notvalid");
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
			errors.rejectValue("email", "account.email.invalid");
		}
		
	}

	public RegisteredUser getCurrentUser() {
		return userService.getCurrentUser();
	}

}
