package com.zuehlke.pgadmissions.validators;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import com.zuehlke.pgadmissions.dto.UserDTO;
import com.zuehlke.pgadmissions.services.UserService;

@Component
public class SuperadminUserDTOValidator extends AbstractValidator {

	private final UserService userService;

	SuperadminUserDTOValidator(){
		this(null);
	}
	
	@Autowired
	public SuperadminUserDTOValidator(UserService userService) {
		this.userService = userService;
	}

	@Override
	public boolean supports(Class<?> clazz) {		
		return clazz.isAssignableFrom(UserDTO.class);
	}

	@Override
	public void addExtraValidation(Object target, Errors errors) {
		UserDTO user = (UserDTO) target;	
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "text.field.empty");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "text.field.empty");		
		
		if (!EmailValidator.getInstance().isValid(user.getEmail())) {
			errors.rejectValue("email", "text.email.notvalid");
		}
		
		/*
		if (StringUtils.isNotBlank(user.getEmail())) {
			RegisteredUser existingUser = userService.getUserByEmailIncludingDisabledAccounts(user.getEmail());
			if (existingUser != null && existingUser.isInRole(Authority.APPLICANT)) {
				errors.rejectValue("email", "text.email.applicant");
			}
		}
		*/
	}
}
