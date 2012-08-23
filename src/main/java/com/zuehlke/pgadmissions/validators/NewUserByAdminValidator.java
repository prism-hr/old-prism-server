package com.zuehlke.pgadmissions.validators;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.domain.RegisteredUser;
import com.zuehlke.pgadmissions.domain.enums.Authority;
import com.zuehlke.pgadmissions.services.UserService;

@Service
public class NewUserByAdminValidator extends AbstractValidator {

	private final UserService userService;
	
	NewUserByAdminValidator(){
		this(null);
	}

	@Autowired
	public NewUserByAdminValidator(UserService userService) {
		this.userService = userService;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return RegisteredUser.class.equals(clazz);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
		RegisteredUser user = (RegisteredUser) target;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "text.field.empty");

		if (!EmailValidator.getInstance().isValid(user.getEmail())) {
			errors.rejectValue("email", "text.email.notvalid");
		}
		if (StringUtils.isNotBlank(user.getEmail())) {
			RegisteredUser existingUser = userService.getUserByEmailIncludingDisabledAccounts(user.getEmail());
			if (existingUser != null && existingUser.isInRole(Authority.APPLICANT)) {
				errors.rejectValue("email", "text.email.applicant");
			}
		}
	}
	
	
}
